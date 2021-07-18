package com.goolue.makaniti.repos.dynamoDb

import com.goolue.makaniti.repos.AwsConfigObjectsCreator.getHttpClient
import io.reactivex.rxjava3.core.Single
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import utils.getConfig
import utils.logger
import java.time.Duration
import kotlin.reflect.full.declaredMemberProperties

class DynamoDbServiceProviderImpl(
  val region: Region,
  val credentialsProvider: String,
  val maxConcurrency: Int,
  val writeTimeout: Duration,
) : DynamoDbServiceProvider {

  companion object {
    private val logger = logger()
    fun getFromConfig(): Single<DynamoDbServiceProvider> {
      return getConfig()
        .map {
          val secretAws = it.secretConfig.secretAws
          val publicAws = it.publicConfig.aws

          DynamoDbServiceProviderImpl(
            region = Region.of(secretAws.region.name),
            credentialsProvider = secretAws.credentialProvider,
            maxConcurrency = publicAws.httpClient.maxConcurrency,
            writeTimeout = publicAws.httpClient.writeTimeout,
          )
        }
    }
  }

  override fun getDynamoDbClient(): DynamoDbEnhancedAsyncClient {
    logger.info("creating DynamoDb client: ${getPropertyVals()}")

    return DynamoDbEnhancedAsyncClient.builder()
      .dynamoDbClient(getClient())
      .build()
  }

  private fun getClient(): DynamoDbAsyncClient? {
    return DynamoDbAsyncClient.builder()
      .httpClient(getHttpClient(writeTimeout, maxConcurrency))
      .region(region)
      .credentialsProvider(ProfileCredentialsProvider.create(credentialsProvider))
      .build()
  }

  private fun getPropertyVals() =
    this::class.declaredMemberProperties
      .associateBy({ it.name }, { it.getter.call(this).toString() })
}
