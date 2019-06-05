package pl.wojciechkabat.hotchilli.services

import pl.wojciechkabat.hotchilli.entities.GuestUser
import pl.wojciechkabat.hotchilli.entities.User
import java.security.Principal

interface SecurityService {
    fun retrieveActiveUser(principal: Principal): User
    fun retrieveGuestUser(principal: Principal): GuestUser
    fun isGuest(principal: Principal): Boolean
}