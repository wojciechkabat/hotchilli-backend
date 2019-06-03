package pl.wojciechkabat.hotchilli.controllers

import org.springframework.web.bind.annotation.*
import pl.wojciechkabat.hotchilli.dtos.RegistrationDto
import pl.wojciechkabat.hotchilli.dtos.UserDto
import pl.wojciechkabat.hotchilli.services.AccountService
import pl.wojciechkabat.hotchilli.services.UserServiceImpl

@RestController
@CrossOrigin
class AccountController(
        val accountService: AccountService
) {
    @PostMapping("/registration")
    fun register(@RequestBody registrationDto: RegistrationDto) {
        accountService.register(registrationDto)
    }

}