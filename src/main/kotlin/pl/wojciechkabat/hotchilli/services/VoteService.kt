package pl.wojciechkabat.hotchilli.services

import pl.wojciechkabat.hotchilli.dtos.VoteData
import pl.wojciechkabat.hotchilli.dtos.VoteDto
import pl.wojciechkabat.hotchilli.entities.User

interface VoteService {
    fun persistVote(voteDto: VoteDto, currentUser: User)
    fun findVoteDataForUsers(userIds: List<Long>): List<VoteData>
}