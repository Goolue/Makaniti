package com.goolue.makaniti.auth.converters

import com.goolue.makaniti.domain.User
import io.vertx.ext.auth.authorization.impl.AuthorizationsImpl

interface AuthUserConverter {
  fun convertUser(authUser: io.vertx.ext.auth.User): User

  fun getIllegalUser(): User = User("", "", "illegal user", "", AuthorizationsImpl())
}
