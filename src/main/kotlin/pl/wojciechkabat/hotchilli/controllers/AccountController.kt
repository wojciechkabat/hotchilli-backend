package pl.wojciechkabat.hotchilli.controllers

import org.springframework.web.bind.annotation.*
import pl.wojciechkabat.hotchilli.dtos.GuestLoginDto
import pl.wojciechkabat.hotchilli.dtos.RegistrationDto
import pl.wojciechkabat.hotchilli.exceptions.NoGuestUserAssociatedToDeviceIdException
import pl.wojciechkabat.hotchilli.services.AccountService

@RestController
@CrossOrigin
class AccountController(
        val accountService: AccountService
) {
    @PostMapping("/registration")
    fun register(@RequestBody registrationDto: RegistrationDto) {
        accountService.register(registrationDto)
    }

    @PostMapping("login/guest")
    fun loginGuestUser(@RequestBody guestLoginDto: GuestLoginDto):  Map<String, String> {
        var authTokens: Map<String, String>
        try {
            authTokens = accountService.loginGuestUser(guestLoginDto.deviceId)
        } catch (e: NoGuestUserAssociatedToDeviceIdException) {
            accountService.registerGuestUser(guestLoginDto)
            authTokens = accountService.loginGuestUser(guestLoginDto.deviceId)
        }
        return authTokens
    }
}