package utils

import com.goolue.makaniti.configs.Config
import com.goolue.makaniti.configs.PublicConfig
import com.goolue.makaniti.configs.SecretConfig
import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.aws.RegionDecoder
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.vertx.core.impl.logging.LoggerFactory
import java.util.*

private const val secretConfPath = "/secret/secret-prod.yml"
private const val publicConfPath = "/application-prod.yml"

private val logger = LoggerFactory.getLogger("ConfigsLoader")
private val configCache: Single<Config> by lazy {
  loadConfig().cache()
}

fun getConfig(): Single<Config> = configCache

private fun loadConfig(): @NonNull Single<Config> {
  val public = Single.fromCallable { getPublicConfig() }
  val private = Single.fromCallable { getSecretConfig() }
  return Single.zip(public, private) { pub, sec -> Config(sec, pub) }
}

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
