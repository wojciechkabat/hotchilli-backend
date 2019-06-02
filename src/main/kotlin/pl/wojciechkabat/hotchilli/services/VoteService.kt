package pl.wojciechkabat.hotchilli.services

import pl.wojciechkabat.hotchilli.dtos.VoteDataDto
import pl.wojciechkabat.hotchilli.dtos.VoteDto
import pl.wojciechkabat.hotchilli.entities.User

interface VoteService {
    fun persistVote(voteDto: VoteDto, currentUser: User)
    fun findVoteDataForUser(userId: Long): VoteDataDto
}