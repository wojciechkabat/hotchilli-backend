package pl.wojciechkabat.hotchilli.dtos

data class GuestVoteDto (
        val ratedUserId: Long,
        val rating: Double,
        val deviceId: String
)