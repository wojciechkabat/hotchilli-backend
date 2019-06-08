package pl.wojciechkabat.hotchilli.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.wojciechkabat.hotchilli.dtos.VoteData
import pl.wojciechkabat.hotchilli.entities.Vote
import java.time.LocalDateTime
import java.util.*

@Repository
interface VoteRepository: JpaRepository<Vote, Long> {
    @Query("SELECT new pl.wojciechkabat.hotchilli.dtos.VoteData(v.ratedUserId, avg(v.rating), count(v)) " +
            "FROM Vote v GROUP BY v.ratedUserId having v.ratedUserId IN :userIds")
    fun findVoteDataByUserIds(@Param("userIds") userIds: List<Long>): List<VoteData>

    @Query("SELECT COUNT(*) FROM Vote v where v.votingUserIdentifier = :votingUserId AND v.createdAt >= :date")
    fun findCountOfUserVotesAfterDate(@Param("votingUserId")votingUserIdentifier: String,
                                      @Param("date") date: LocalDateTime): Long

    @Query("SELECT new pl.wojciechkabat.hotchilli.dtos.VoteData(v.ratedUserId, avg(v.rating), count(v)) " +
            "FROM Vote v GROUP BY v.ratedUserId having v.ratedUserId = :userId")
    fun findVoteDataByUserId(@Param("userId") userId: Long): Optional<VoteData>
}