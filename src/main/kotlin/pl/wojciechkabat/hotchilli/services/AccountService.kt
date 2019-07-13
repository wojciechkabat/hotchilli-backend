package pl.wojciechkabat.hotchilli.services

import pl.wojciechkabat.hotchilli.dtos.PictureDto
import pl.wojciechkabat.hotchilli.dtos.RegistrationDto
import pl.wojciechkabat.hotchilli.entities.User

interface AccountService {
    fun register(registrationDto: RegistrationDto)
    fun addPicture(pictureDto: PictureDto, user: User): PictureDto
    fun deletePicture(pictureId: Long, user: User)
    fun deleteAccountFor(user: User)
    fun confirmAccount(pin: String, user: User)
    fun resendConfirmationEmail(activeUser: User)
}