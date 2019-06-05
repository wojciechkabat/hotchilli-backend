package pl.wojciechkabat.hotchilli.utils

import java.util.regex.Pattern
import java.util.stream.Collectors.toList as toList

class PasswordValidator {
    companion object PasswordValidator {
        private val VALID_PASSWORD_REGEX = Pattern.compile("^(?=.*[0-9])(?=.*[A-Z]).{6,}$")

        fun validate(password: String): Boolean {
            return VALID_PASSWORD_REGEX.matcher(password).find()
        }
    }

}