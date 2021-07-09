package com.goolue.makaniti.configClasses

import com.amazonaws.regions.Region
import java.time.Duration

// public
data class AwsHttpClient(val maxConcurrency: Int, val writeTimeout: Duration)
data class AwsS3Service(val checksumValidationEnabled: Boolean, val chunkedEncodingEnabled: Boolean)
data class Aws(val httpClient: AwsHttpClient, val s3Service: AwsS3Service)

data class PublicConfig(val env: String, val aws: Aws)

// secret
data class SecretAws(val region: Region, val credentialProvider: String, val accessKey: String, val secret: String,
                     val clientId: String, val clientSecret: String, val clientTenant: String)
data class SecretConfig(val env: String, val secretAws: SecretAws)

// all
data class Config(val secretConfig: SecretConfig, val publicConfig: PublicConfig)
