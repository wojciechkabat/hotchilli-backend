package pl.wojciechkabat.hotchilli.services

import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.dtos.GuestVoteDto
import pl.wojciechkabat.hotchilli.dtos.VoteData
import pl.wojciechkabat.hotchilli.dtos.VoteDto
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.entities.Vote
import pl.wojciechkabat.hotchilli.repositories.VoteRepository
import java.time.LocalDateTime

@Service
class VoteServiceImpl(val voteRepository: VoteRepository) : VoteService {
    override fun findVoteDataForUsers(userIds: List<Long>): List<VoteData> {
        return voteRepository.findVoteDataByUserIds(userIds)
    }

    override fun persistVote(voteDto: VoteDto, currentUser: User) {
        voteRepository.save(
                Vote(
                        null,
                        currentUser.id.toString(),
                        voteDto.ratedUserId,
                        voteDto.rating,
                        LocalDateTime.now()
                )
        )
    }

    override fun persistGuestVote(guestVoteDto: GuestVoteDto) {
        voteRepository.save(
                Vote(
                        null,
                        guestVoteDto.deviceId,
                        guestVoteDto.ratedUserId,
                        guestVoteDto.rating,
                        LocalDateTime.now()
                )
        )
    }
}