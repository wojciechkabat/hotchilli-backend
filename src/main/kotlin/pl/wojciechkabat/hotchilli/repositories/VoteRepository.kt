package pl.wojciechkabat.hotchilli.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.wojciechkabat.hotchilli.dtos.VoteDataDto
import pl.wojciechkabat.hotchilli.entities.Vote

@Repository
interface VoteRepository: JpaRepository<Vote, Long> {
    @Query("SELECT new pl.wojciechkabat.hotchilli.dtos.VoteDataDto(avg(v.rating), count(v)) " +
            "FROM Vote v WHERE v.ratedUserId = :userId")
    fun findVoteDataByUserId(@Param("userId") userId: Long): VoteDataDto
}