package pl.wojciechkabat.hotchilli.services

import pl.wojciechkabat.hotchilli.dtos.FacebookLoginDto
import pl.wojciechkabat.hotchilli.dtos.FacebookPostRegistrationDto
import pl.wojciechkabat.hotchilli.dtos.PictureDto
import pl.wojciechkabat.hotchilli.dtos.RegistrationDto
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.utils.facebookModels.FacebookUser

interface AccountService {
    fun register(registrationDto: RegistrationDto)
    fun addPicture(pictureDto: PictureDto, user: User): PictureDto
    fun deletePicture(pictureId: Long, user: User)
    fun deleteAccountFor(user: User)
    fun confirmAccount(pin: String, user: User)
    fun resendConfirmationEmail(activeUser: User)
    fun loginFacebookUser(facebookUser: FacebookUser, deviceId: String): Map<String, String>
    fun registerFacebookUser(currentFacebookUser: FacebookUser, accessToken: String, facebookLoginDto: FacebookLoginDto)
    fun postRegisterFacebookUser(user: User, facebookPostRegistrationDto: FacebookPostRegistrationDto)
}