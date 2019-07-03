package pl.wojciechkabat.hotchilli.dtos

import pl.wojciechkabat.hotchilli.entities.Gender
import java.time.LocalDate

data class UpdateUserDto (
        val username: String,
        val gender: Gender,
        val dateOfBirth: LocalDate
)