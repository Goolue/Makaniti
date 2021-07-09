package com.goolue.makaniti.auth

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.auth.oauth2.OAuth2Auth
import io.vertx.ext.auth.oauth2.OAuth2Options
import io.vertx.ext.auth.oauth2.providers.AmazonCognitoAuth
import utils.getConfig

class CognitoAuthHandler : AuthHandler {

  override fun createAuth(vertx: Vertx): Future<OAuth2Auth> {
    val secretAws = getConfig().secretConfig.secretAws

    return AmazonCognitoAuth.discover(
      vertx,
      OAuth2Options()
        .setClientID(secretAws.clientId)
        .setClientSecret(secretAws.clientSecret)
        .setTenant(secretAws.clientTenant)
        .setSite("https://cognito-idp.${secretAws.region.name}.amazonaws.com/{tenant}")
    )
  }
}
