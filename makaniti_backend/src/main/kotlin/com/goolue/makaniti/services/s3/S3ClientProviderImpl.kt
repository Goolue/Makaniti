package com.goolue.makaniti.services.s3

import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Single
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Configuration
import utils.getConfig
import utils.logger
import java.time.Duration
import kotlin.reflect.full.declaredMemberProperties

class S3ClientProviderImpl(
  val region: Region,
  val credentialsProvider: String,
  val maxConcurrency: Int,
  val writeTimeout: Duration,
  val checksumValidationEnabled: Boolean,
  val chunkedEncodingEnabled: Boolean
) : S3ClientProvider {

  companion object {
    private val logger = logger()

    fun getFromConfig(): Single<S3ClientProviderImpl> {
      return getConfig()
        .map {
          val secretAws = it.secretConfig.secretAws
          val publicAws = it.publicConfig.aws

          S3ClientProviderImpl(
            region = Region.of(secretAws.region.name),
            credentialsProvider = secretAws.credentialProvider,
            maxConcurrency = publicAws.httpClient.maxConcurrency,
            writeTimeout = publicAws.httpClient.writeTimeout,
            checksumValidationEnabled = publicAws.s3Service.checksumValidationEnabled,
            chunkedEncodingEnabled = publicAws.s3Service.chunkedEncodingEnabled
          )
        }
    }
  }

  override fun getS3Client(): S3AsyncClient {
    logger.info("creating S3 client: ${getPropertyVals()}")

    return S3AsyncClient.builder()
      .httpClient(getHttpClient())
      .serviceConfiguration(getServiceConfig())
      .region(region)
      .credentialsProvider(ProfileCredentialsProvider.create(credentialsProvider))
      .build()
  }

  private fun getServiceConfig() = S3Configuration.builder()
    .checksumValidationEnabled(checksumValidationEnabled)
    .chunkedEncodingEnabled(chunkedEncodingEnabled)
    .build()

  private fun getHttpClient() = NettyNioAsyncHttpClient.builder()
    .writeTimeout(writeTimeout)
    .maxConcurrency(maxConcurrency)
    .build()

  private fun getPropertyVals() =
    this::class.declaredMemberProperties
      .associateBy({it.name}, {it.getter.call(this).toString()})
}
