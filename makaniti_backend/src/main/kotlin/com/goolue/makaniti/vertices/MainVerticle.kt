package com.goolue.makaniti.vertices

import com.goolue.makaniti.auth.CognitoAuthHandler
import com.goolue.makaniti.routers.getMainRouter
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.ext.auth.oauth2.OAuth2Auth
import utils.logger

class MainVerticle : AbstractVerticle() {

  companion object {
    private const val PORT = 8080
    private val authHandler = CognitoAuthHandler()
    private val logger = logger()
  }

  fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(MainVerticle())
  }

  override fun start(startPromise: Promise<Void>) {
    authHandler.createAuth(vertx)
      .onSuccess { oauth -> run(startPromise, oauth) }
      .onFailure { err ->
        stop()
        startPromise.fail(err)
      }
  }

  override fun stop() {
    logger.warn("stopping!")
    super.stop()
  }

  private fun run(startPromise: Promise<Void>, authProvider: OAuth2Auth) {
    val mainRouter = getMainRouter(vertx, authProvider)

    vertx.createHttpServer()
      .requestHandler(mainRouter)
      .listen(PORT) { http ->
        if (http.succeeded()) {
          startPromise.complete()
          logger.info("HTTP server started on port $PORT")
        } else {
          startPromise.fail(http.cause());
        }
      }
  }
}
