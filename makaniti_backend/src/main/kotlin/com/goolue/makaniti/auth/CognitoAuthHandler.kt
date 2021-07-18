package com.goolue.makaniti.auth

import com.goolue.makaniti.configs.SecretAws
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Single
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.auth.oauth2.OAuth2Auth
import io.vertx.ext.auth.oauth2.OAuth2Options
import io.vertx.ext.auth.oauth2.providers.AmazonCognitoAuth
import io.vertx.rxjava3.SingleHelper
import utils.getConfig

class CognitoAuthHandler : AuthHandler {

  override fun createAuth(vertx: Vertx): Future<OAuth2Auth> {
    return SingleHelper.toFuture(getAuthConfigs())
      .flatMap { AmazonCognitoAuth.discover(vertx, it) }
  }

  private fun getAuthConfigs(): Single<OAuth2Options> {
    return getConfig()
      .map { it.secretConfig.secretAws }
      .map { oAuth2Options(it) }
  }

  private fun oAuth2Options(it: SecretAws) = OAuth2Options()
    .setClientID(it.clientId)
    .setClientSecret(it.clientSecret)
    .setTenant(it.clientTenant)
    .setSite("https://cognito-idp.${it.region.name}.amazonaws.com/{tenant}")
}
