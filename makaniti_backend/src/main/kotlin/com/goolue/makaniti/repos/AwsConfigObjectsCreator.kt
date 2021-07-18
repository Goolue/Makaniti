package com.goolue.makaniti.repos

import software.amazon.awssdk.http.async.SdkAsyncHttpClient
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.services.s3.S3Configuration
import java.time.Duration

object AwsConfigObjectsCreator {
  internal fun getHttpClient(writeTimeout: Duration, maxConcurrency: Int): SdkAsyncHttpClient =
    NettyNioAsyncHttpClient.builder()
      .writeTimeout(writeTimeout)
      .maxConcurrency(maxConcurrency)
      .build()

  internal fun getServiceConfig(checksumValidationEnabled: Boolean, chunkedEncodingEnabled: Boolean): S3Configuration =
    S3Configuration.builder()
      .checksumValidationEnabled(checksumValidationEnabled)
      .chunkedEncodingEnabled(chunkedEncodingEnabled)
      .build()
}
