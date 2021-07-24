package com.goolue.makaniti.persisted

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import utils.logger

abstract class AbstractRepo<T> {
  abstract fun getTable(): DynamoDbAsyncTable<T>

  fun save(item: T): Single<Boolean> =
    Maybe.fromFuture(getTable().putItem(item))
      .map { true }
      .defaultIfEmpty(true)
      .onErrorReturn {
        false
      }
      .doOnError {
        logger().warn("error saving  $item into db. error: $it")
      }
      .doOnSuccess {
        logger().debug("successfully saved item $item")
      }

  fun delete(uid: String): Single<Boolean> {
    val key = Key.builder().partitionValue(uid).build()
    return Maybe.fromFuture(getTable().deleteItem(key))
      .map { true }
      .defaultIfEmpty(true)
      .onErrorReturn {
        false
      }
      .doOnError {
        logger().warn("error deleting $uid into db. error: $it")
      }
      .doOnSuccess {
        logger().debug("successfully deleted $uid")
      }
  }

  fun fetchByUid(uid: String): Maybe<T> {
    val key = Key.builder().partitionValue(uid).build()
    return Maybe.fromFuture(getTable().getItem(key))
      .doOnError {
        logger().warn("error fetching item $uid. error: $it")
      }
  }

}
