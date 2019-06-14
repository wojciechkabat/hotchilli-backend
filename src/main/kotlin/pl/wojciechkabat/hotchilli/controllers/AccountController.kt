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
    fun addPicture(principal: Principal, @RequestBody pictureDto: PictureDto): PictureDto{
        val activeUser = securityService.retrieveActiveUser(principal)
        return accountService.addPicture(pictureDto, activeUser)
    }

    @DeleteMapping("/pictures/{id}")
    fun deletePicture(principal: Principal, @PathVariable("id") pictureId: Long) {
        val activeUser = securityService.retrieveActiveUser(principal)
        return accountService.deletePicture(pictureId, activeUser)
    }
}