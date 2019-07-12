package pl.wojciechkabat.hotchilli.services

interface EmailService {
    fun sendAccountConfirmationEmail(email: String, languageCode: String, pin: String)
}