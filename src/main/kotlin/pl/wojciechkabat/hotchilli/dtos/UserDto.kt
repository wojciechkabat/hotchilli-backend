package pl.wojciechkabat.hotchilli.dtos

data class UserDto (
        val id: Long?,
        val username: String,
        val age: Int,
        val pictures: List<PictureDto>,
        val averageRating: Double,
        val voteCount: Long
)