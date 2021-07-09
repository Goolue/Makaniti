package com.goolue.makaniti.routers

import com.goolue.makaniti.auth.converters.AuthUserConverterCognito
import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Vertx
import io.vertx.ext.auth.oauth2.OAuth2Auth
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.OAuth2AuthHandler
import io.vertx.kotlin.core.json.jsonArrayOf
import io.vertx.kotlin.core.json.jsonObjectOf

private val authUserConverter = AuthUserConverterCognito()

fun getMainRouter(vertx: Vertx, authProvider: OAuth2Auth): Router {
  val mainRouter = Router.router(vertx)
  val apiRouter = getApiRouter(vertx, authProvider)

  mainRouter.mountSubRouter("/api/v1", apiRouter)

  return mainRouter
}

private fun getApiRouter(vertx: Vertx, authProvider: OAuth2Auth): Router {
  val apiRouter = Router.router(vertx)

  apiRouter.get("/*")
    .handler(OAuth2AuthHandler.create(vertx, authProvider))
    .handler { ctx -> ctx.next() }

  apiRouter.get("/greet")
    .handler(OAuth2AuthHandler.create(vertx, authProvider))
    .handler { ctx ->
      val user = authUserConverter.convertUser(ctx.user())
      ctx.response()
        .setStatusCode(HttpResponseStatus.OK.code())
        .putHeader("Content-Type", HttpHeaderValues.APPLICATION_JSON)
        .end(jsonObjectOf("response" to "hello $user").toString())
    }

  apiRouter.mountSubRouter("/users/", getApiUsersRouter(vertx))
  return apiRouter
}

private fun getApiUsersRouter(vertx: Vertx): Router {
  val usersRouter = Router.router(vertx)
  usersRouter.get(":sub/items/")
    .handler { ctx ->
      ctx.response()
        .setStatusCode(HttpResponseStatus.OK.code())
        .putHeader("Content-Type", HttpHeaderValues.APPLICATION_JSON)
        .end(jsonArrayOf(1, 2, 3, 4).toString())
    }

  return usersRouter
}
