package pl.wojciechkabat.hotchilli.services

import pl.wojciechkabat.hotchilli.dtos.GuestLoginDto
import pl.wojciechkabat.hotchilli.dtos.RegistrationDto

interface AccountService {
    fun register(registrationDto: RegistrationDto)
    fun loginGuestUser(deviceId: String): Map<String, String>
    fun registerGuestUser(guestLoginDto: GuestLoginDto)
}