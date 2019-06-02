package pl.wojciechkabat.hotchilli.dtos

data class VoteData (
        val userId: Long,
        val averageRating: Double?,
        val voteCount: Long
)