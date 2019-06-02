package pl.wojciechkabat.hotchilli.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.wojciechkabat.hotchilli.entities.Vote

@Repository
interface VoteRepository: JpaRepository<Vote, Long>