package pl.wojciechkabat.hotchilli.dtos

data class EmailContentDto(
        val receiverAddress: String,
        val subject: String,
        val content: String
)