package com.goolue.makaniti.domain

import io.vertx.ext.auth.authorization.Authorizations

data class User(val sub: String, val email: String, val username: String, val phoneNum: String, val authorizations: Authorizations) {
}
