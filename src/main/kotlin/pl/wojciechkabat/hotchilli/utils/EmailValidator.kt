package pl.wojciechkabat.hotchilli.utils

import java.util.regex.Pattern
import java.util.stream.Collectors.toList as toList

class EmailValidator()  {
    companion object EmailValidator {
        private val VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)

        fun validate(email: String): Boolean {
            return VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()
        }
    }

}