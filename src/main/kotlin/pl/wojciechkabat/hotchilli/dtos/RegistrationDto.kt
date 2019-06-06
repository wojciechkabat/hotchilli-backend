package pl.wojciechkabat.hotchilli.dtos

import java.time.LocalDate

data class RegistrationDto (
        val email: String,
        val password: String,
        val username: String,
        val pictures: List<PictureDto>,
        val dateOfBirth: LocalDate
)