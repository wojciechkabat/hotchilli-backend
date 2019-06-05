package pl.wojciechkabat.hotchilli.services

import pl.wojciechkabat.hotchilli.entities.GuestUser

interface GuestUserService {
    fun findByDeviceId(deviceId: String): GuestUser
}