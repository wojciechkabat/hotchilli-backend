package pl.wojciechkabat.hotchilli.services

import pl.wojciechkabat.hotchilli.entities.User
import java.security.Principal

interface SecurityService {
    fun retrieveActiveUser(principal: Principal): User
}