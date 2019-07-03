package pl.wojciechkabat.hotchilli.dtos

import pl.wojciechkabat.hotchilli.entities.Gender
import java.time.LocalDate

data class UserDto (
        val id: Long?,
        val username: String,
        val age: Int,
        val dateOfBirth: LocalDate,
        val pictures: List<PictureDto>,
        val averageRating: Double,
        val voteCount: Long,
        val gender: Gender
)