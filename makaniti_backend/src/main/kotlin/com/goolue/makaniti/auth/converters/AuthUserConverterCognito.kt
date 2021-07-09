package com.goolue.makaniti.auth.converters

import com.goolue.makaniti.domain.User
import io.vertx.core.json.JsonObject

class AuthUserConverterCognito : AuthUserConverter {
  override fun convertUser(authUser: io.vertx.ext.auth.User): User {
    val json = authUser.attributes().map["accessToken"] as JsonObject
    val sub: String = json.getString("sub")
    val email = json.getString("email")
    val phoneNum = json.getString("phone_number")
    val userName = json.getString("cognito:username")

    listOf(sub, email, phoneNum, userName)
      .contains(null)
      .let {
        if (it) return getIllegalUser()
        return User(sub, email, userName, phoneNum, authUser.authorizations())
      }
  }
}
