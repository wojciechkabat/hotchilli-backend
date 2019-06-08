package pl.wojciechkabat.hotchilli.utils

import pl.wojciechkabat.hotchilli.dtos.PictureDto
import pl.wojciechkabat.hotchilli.entities.Picture
import java.util.regex.Pattern
import java.util.stream.Collectors.toList as toList

class Validators  {
    companion object Validators {
        private val VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
        private val VALID_PASSWORD_REGEX = Pattern.compile("^(?=.*[0-9])(?=.*[A-Z]).{6,}$")

        fun validateEmail(email: String): Boolean {
            return VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()
        }

        fun validatePassword(password: String): Boolean {
            return VALID_PASSWORD_REGEX.matcher(password).find()
        }
    }
}