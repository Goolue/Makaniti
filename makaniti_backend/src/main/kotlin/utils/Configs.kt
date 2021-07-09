package utils

import com.goolue.makaniti.configClasses.Config
import com.goolue.makaniti.configClasses.PublicConfig
import com.goolue.makaniti.configClasses.SecretConfig
import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.aws.RegionDecoder
import io.vertx.core.impl.logging.LoggerFactory

private const val secretConfPath = "/secret/secret-prod.yml"
private const val publicConfPath = "/application-prod.yml"

private val logger = LoggerFactory.getLogger("ConfigsLoader")
private val configCache: Config by lazy { loadConfig() }

fun getConfig(): Config = configCache

private fun loadConfig() = Config(getSecretConfig(), getPublicConfig())

private fun getPublicConfig(): PublicConfig {
  logger.info("loading public configs from $publicConfPath")
  return ConfigLoader.Builder()
    .addDecoder(RegionDecoder())
    .build()
    .loadConfigOrThrow(publicConfPath)
}

private fun getSecretConfig(): SecretConfig {
  logger.info("loading secret configs from $secretConfPath")
  return ConfigLoader.Builder()
    .addDecoder(RegionDecoder())
    .build()
    .loadConfigOrThrow(secretConfPath)
}
