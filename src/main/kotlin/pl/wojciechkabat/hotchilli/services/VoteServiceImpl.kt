package pl.wojciechkabat.hotchilli.services

import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.dtos.VoteDataDto
import pl.wojciechkabat.hotchilli.dtos.VoteDto
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.entities.Vote
import pl.wojciechkabat.hotchilli.repositories.VoteRepository
import java.time.LocalDateTime

@Service
class VoteServiceImpl(val voteRepository: VoteRepository) : VoteService {
    override fun persistVote(voteDto: VoteDto, currentUser: User) {
        voteRepository.save(
                Vote(
                        null,
                        currentUser.id!!,
                        voteDto.ratedUserId,
                        voteDto.rating,
                        LocalDateTime.now()
                )
        )
    }

    override fun findVoteDataForUser(userId: Long): VoteDataDto {
        return voteRepository.findVoteDataByUserId(userId)
    }
}