package com.goolue.makaniti.auth

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.auth.oauth2.OAuth2Auth

interface AuthHandler {
  fun createAuth(vertx: Vertx): Future<OAuth2Auth>
}
