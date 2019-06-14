package pl.wojciechkabat.hotchilli.controllers

import org.springframework.web.bind.annotation.*
import pl.wojciechkabat.hotchilli.dtos.PictureDto
import pl.wojciechkabat.hotchilli.dtos.RegistrationDto
import pl.wojciechkabat.hotchilli.services.AccountService
import pl.wojciechkabat.hotchilli.services.SecurityService
import java.security.Principal

@RestController
@CrossOrigin
class AccountController(
        val accountService: AccountService,
        val securityService: SecurityService
) {
    @PostMapping("/registration")
    fun register(@RequestBody registrationDto: RegistrationDto) {
        accountService.register(registrationDto)
    }

    @PostMapping("/pictures")
    fun addPicture(principal: Principal, @RequestBody pictureDto: PictureDto) {
        val activeUser = securityService.retrieveActiveUser(principal)
        accountService.addPicture(pictureDto, activeUser)
    }

}