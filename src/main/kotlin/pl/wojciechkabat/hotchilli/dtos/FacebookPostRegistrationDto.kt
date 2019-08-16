package pl.wojciechkabat.hotchilli.dtos

import pl.wojciechkabat.hotchilli.entities.Gender
import java.time.LocalDate

data class FacebookPostRegistrationDto (
        val username: String,
        val pictures: List<PictureDto>,
        val dateOfBirth: LocalDate,
        val gender: Gender
)