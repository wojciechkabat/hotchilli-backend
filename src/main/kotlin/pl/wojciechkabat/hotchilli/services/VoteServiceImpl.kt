package pl.wojciechkabat.hotchilli.services

import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.dtos.GuestVoteDto
import pl.wojciechkabat.hotchilli.dtos.VoteData
import pl.wojciechkabat.hotchilli.dtos.VoteDto
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.entities.Vote
import pl.wojciechkabat.hotchilli.exceptions.GuestVoteLimitExceededException
import pl.wojciechkabat.hotchilli.repositories.VoteRepository
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class VoteServiceImpl(val voteRepository: VoteRepository, val clock: Clock) : VoteService {
    private val GUEST_VOTE_LIMIT: Int = 3

    override fun findVoteDataForUsers(userIds: List<Long>): List<VoteData> {
        if(userIds.isEmpty()) {
            return emptyList()
        }
        return voteRepository.findVoteDataByUserIds(userIds)
    }

    override fun findVoteDataForUser(userId: Long): VoteData {
        val optionalVoteData = voteRepository.findVoteDataByUserId(userId)
        return if (optionalVoteData.isPresent) {
            optionalVoteData.get()
        } else {
            VoteData(userId, 0.0, 0)

        }
    }

    override fun deleteAllVotesForUser(user: User) {
        voteRepository.deleteAllVotesForUserWithId(user.id!!)
    }

    override fun persistVote(voteDto: VoteDto, currentUser: User) {
        voteRepository.save(
                Vote(
                        null,
                        currentUser.id.toString(),
                        voteDto.ratedUserId,
                        voteDto.rating,
                        LocalDateTime.now(clock)
                )
        )
    }

    override fun persistGuestVote(guestVoteDto: GuestVoteDto) {
        val votesToday = voteRepository.findCountOfUserVotesAfterDate(guestVoteDto.deviceId, LocalDate.now().atStartOfDay())

        if (votesToday >= GUEST_VOTE_LIMIT) {
            throw GuestVoteLimitExceededException()
        }

        voteRepository.save(
                Vote(
                        null,
                        guestVoteDto.deviceId,
                        guestVoteDto.ratedUserId,
                        guestVoteDto.rating,
                        LocalDateTime.now(clock)
                )
        )
    }

    override fun findIdsOfUsersVotedFor(userIdentifier: String): Set<Long> {
        return voteRepository.findIdsOfUsersVotedFor(userIdentifier)
    }
}