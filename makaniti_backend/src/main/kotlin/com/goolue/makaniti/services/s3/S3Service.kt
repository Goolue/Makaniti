package com.goolue.makaniti.services.s3

import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.core.Observable
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.*
import utils.logger
import java.util.*


class S3Service(val s3: S3AsyncClient) {
  companion object {
    private val logger = logger()
  }

  fun isBucketExists(bucketName: String): @NonNull Single<Boolean> =
    Single.fromFuture(s3.headBucket(HeadBucketRequest.builder().bucket(bucketName).build()))
      .map { true }
      .onErrorReturn { false }

  fun createBucket(bucketName: String): @NonNull Single<Boolean> {
    val single = Single.fromFuture(s3.createBucket(CreateBucketRequest.builder().bucket(bucketName).build()))
      .doOnError {
        logger.warn("cannot create bucket '$bucketName'. error: $it")
      }
      .doOnSuccess {
        logger.debug("successfully created bucket '$bucketName'")
      }
    return convertRespToBoolean(single)
  }

  fun createItemInBucket(bucketName: String, itemKey: String, itemContent: ByteArray): @NonNull Single<Boolean> {
    val putObjectRequest = PutObjectRequest.builder()
      .bucket(bucketName)
      .key(itemKey)
      .build()
    val single = Single.fromFuture(s3.putObject(putObjectRequest, AsyncRequestBody.fromBytes(itemContent)))
      .doOnError {
        logger.warn("cannot put item '$itemKey' in bucket '$bucketName'. error: $it")
      }
      .doOnSuccess {
        logger.debug("successfully created '$itemKey' in bucket '$bucketName'")
      }
    return convertRespToBoolean(single)
  }

  fun listBucketItemNames(bucketName: String): @NonNull Observable<String> =
    listAllBucketItemsAsS3Objs(bucketName).map { it.key() }

  fun deleteBucket(bucketName: String): @NonNull Single<Boolean> {
    return deleteAllItemsInBucket(bucketName)
      .flatMap {
        if (!it) Single.just(false)
        else {
          val deleteBucketRequest = s3.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build())
          val single = Single.fromFuture(deleteBucketRequest)
            .doOnError { err ->
              logger.warn("cannot delete bucket '$bucketName'. error: $err")
            }
            .doOnSuccess {
              logger.debug("successfully deleted bucket '$bucketName'")
            }
          convertRespToBoolean(single)
        }
      }
  }

  fun deleteItemInBucketBucket(bucketName: String, itemKey: String): @NonNull Single<Boolean> {
    val single =
      Single.fromFuture(s3.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(itemKey).build()))
        .doOnError {
          logger.warn("cannot delete item '$itemKey' from bucket '$bucketName'. error: $it")
        }
        .doOnSuccess {
          logger.debug("successfully deleted '$itemKey' from bucket '$bucketName'")
        }
    return convertRespToBoolean(single)
  }

  private fun <T : S3Response> convertRespToBoolean(single: @NonNull Single<T>): @NonNull Single<Boolean> =
    single.map { resp -> Optional.of(resp) }
      .onErrorReturnItem(Optional.empty())
      .map { it.isPresent }

  private fun listAllBucketItemsAsS3Objs(bucketName: String): @NonNull Observable<S3Object> {
    val listObjsRequest = ListObjectsV2Request.builder().bucket(bucketName).build()
    val listObjsResp = s3.listObjectsV2(listObjsRequest)

    return Single.fromFuture(listObjsResp)
      .flatMap { listBucketObjsRec(bucketName, it, emptyList()) }
      .flatMapObservable { Observable.fromIterable(it) }
      .doOnError {
        logger.warn("cannot list items in bucket '$bucketName'. error: $it")
      }
  }

  private fun deleteAllItemsInBucket(bucketName: String): @NonNull Single<Boolean> {
    val listObjsRequest = ListObjectsV2Request.builder().bucket(bucketName).build()
    val listObjsResp = s3.listObjectsV2(listObjsRequest)

    return Single.fromFuture(listObjsResp)
      .flatMap { listBucketObjsRec(bucketName, it, emptyList()) }
      .flatMapObservable { Observable.fromIterable(it) }
      .map { it.key() }
      .flatMap { deleteItemInBucketBucket(bucketName, it).toObservable() }
      .all { it }
  }

  private fun listBucketObjsRec(bucketName: String, response: ListObjectsV2Response, objsAcc: List<S3Object>): Single<List<S3Object>> {
    logger.debug("listing items in bucket $bucketName recursively. curr acc is: $objsAcc")

    if (!response.isTruncated) return Single.just(objsAcc + response.contents())

    return Single.fromFuture(
      s3.listObjectsV2(
        // TODO extract request builders to utils / class
        ListObjectsV2Request.builder()
          .bucket(bucketName)
          .continuationToken(response.nextContinuationToken())
          .build()
      )
    )
      .doOnError {
        logger.warn("cannot list items from bucket '$bucketName'. error: $it")
      }
      .flatMap {
        listBucketObjsRec(bucketName, it, objsAcc + it.contents())
      }
  }
}
