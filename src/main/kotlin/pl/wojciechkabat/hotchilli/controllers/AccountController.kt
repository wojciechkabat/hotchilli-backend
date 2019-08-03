package pl.wojciechkabat.hotchilli.controllers

import org.springframework.web.bind.annotation.*
import pl.wojciechkabat.hotchilli.dtos.FacebookLoginDto
import pl.wojciechkabat.hotchilli.dtos.PictureDto
import pl.wojciechkabat.hotchilli.dtos.RegistrationDto
import pl.wojciechkabat.hotchilli.exceptions.NoUserAssociatedToFacebookIdException
import pl.wojciechkabat.hotchilli.services.AccountService
import pl.wojciechkabat.hotchilli.services.FacebookService
import pl.wojciechkabat.hotchilli.services.SecurityService
import pl.wojciechkabat.hotchilli.utils.facebookModels.FacebookPhoto
import java.security.Principal

@RestController
@CrossOrigin
class AccountController(
        val accountService: AccountService,
        val securityService: SecurityService,
        val facebookService: FacebookService
) {
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

    @DeleteMapping("users/me")
    fun deleteAccount(principal: Principal) {
        val activeUser = securityService.retrieveActiveUser(principal)
        return accountService.deleteAccountFor(activeUser)
    }

    @PostMapping("/registration")
    fun register(@RequestBody registrationDto: RegistrationDto) {
        accountService.register(registrationDto)
    }

    @PostMapping("/loginFB")
    fun loginWithFacebook(@RequestHeader("fb-access-token") accessToken: String, @RequestBody facebookLoginDto: FacebookLoginDto): Map<String, String> {
        val currentFacebookUser = facebookService.getCurrentFacebookUser(accessToken)
        var authTokens: Map<String, String>
        try {
            authTokens = accountService.loginFacebookUser(currentFacebookUser, facebookLoginDto.deviceId)
        } catch (e: NoUserAssociatedToFacebookIdException) {
            accountService.registerFacebookUser(currentFacebookUser, accessToken, facebookLoginDto)
            authTokens = accountService.loginFacebookUser(currentFacebookUser, facebookLoginDto.deviceId)
        }

        return authTokens
    }

    @PutMapping("/registration/confirmation")
    fun accountConfirmation(principal: Principal, @RequestBody pin: String) {
        val activeUser = securityService.retrieveActiveUser(principal)
        accountService.confirmAccount(pin, activeUser)
    }

    @GetMapping("/registration/confirmation/resend")
    fun resendConfirmationEmail(principal: Principal) {
        val activeUser = securityService.retrieveActiveUser(principal)
        accountService.resendConfirmationEmail(activeUser)
    }


    @GetMapping("account/active")
    fun isAccountActive(principal: Principal): Boolean? {
        val activeUser = securityService.retrieveActiveUser(principal)
        return activeUser.isActive
    }

    @GetMapping("pictures/fb")
    fun getFacebookPhotos(@RequestHeader("fb-access-token") accessToken: String): List<FacebookPhoto> {
        return this.facebookService.getCurrentUserFacebookPhotos(accessToken)
    }
}