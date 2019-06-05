package pl.wojciechkabat.hotchilli.services

import pl.wojciechkabat.hotchilli.dtos.RegistrationDto

interface AccountService {
    fun register(registrationDto: RegistrationDto)
}