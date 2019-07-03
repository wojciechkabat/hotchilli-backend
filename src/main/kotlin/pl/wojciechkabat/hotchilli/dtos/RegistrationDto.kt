package pl.wojciechkabat.hotchilli.dtos

import pl.wojciechkabat.hotchilli.entities.Gender
import java.time.LocalDate

data class RegistrationDto (
        val email: String,
        val username: String,
        val password: String,
        val pictures: List<PictureDto>,
        val dateOfBirth: LocalDate,
        val gender: Gender
)