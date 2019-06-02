package pl.wojciechkabat.hotchilli.services

import pl.wojciechkabat.hotchilli.dtos.VoteDto
import pl.wojciechkabat.hotchilli.entities.User

interface VotingService {
    fun persistVote(voteDto: VoteDto, currentUser: User)
}