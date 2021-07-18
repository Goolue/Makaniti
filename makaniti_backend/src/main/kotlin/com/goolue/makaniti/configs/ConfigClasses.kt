package com.goolue.makaniti.configs

import com.amazonaws.regions.Region
import java.time.Duration
sealed class ConfigClass()

// public
data class AwsHttpClient(val maxConcurrency: Int, val writeTimeout: Duration) : ConfigClass()
data class AwsS3Service(val checksumValidationEnabled: Boolean, val chunkedEncodingEnabled: Boolean) : ConfigClass()
data class Aws(val httpClient: AwsHttpClient, val s3Service: AwsS3Service) : ConfigClass()

data class PublicConfig(val env: String, val aws: Aws) : ConfigClass()

// secret
data class SecretAws(val region: Region, val credentialProvider: String, val accessKey: String, val secret: String,
                     val clientId: String, val clientSecret: String, val clientTenant: String) : ConfigClass()
data class SecretConfig(val env: String, val secretAws: SecretAws) : ConfigClass()

// all
data class Config(val secretConfig: SecretConfig, val publicConfig: PublicConfig) : ConfigClass()
