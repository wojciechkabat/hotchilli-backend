package pl.wojciechkabat.hotchilli.services

import pl.wojciechkabat.hotchilli.dtos.VoteData
import pl.wojciechkabat.hotchilli.dtos.VoteDto
import pl.wojciechkabat.hotchilli.dtos.GuestVoteDto
import pl.wojciechkabat.hotchilli.entities.User

interface VoteService {
    fun persistVote(voteDto: VoteDto, currentUser: User)
    fun persistGuestVote(guestVoteDto: GuestVoteDto)
    fun findVoteDataForUsers(userIds: List<Long>): List<VoteData>
    fun findVoteDataForUser(userId: Long): VoteData
    fun findIdsOfUsersVotedFor(userIdentifier: String): Set<Long>
    fun deleteAllVotesForUser(user: User)
}