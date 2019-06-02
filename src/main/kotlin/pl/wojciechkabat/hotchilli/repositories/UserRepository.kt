package pl.wojciechkabat.hotchilli.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.wojciechkabat.hotchilli.entities.User

@Repository
interface UserRepository : JpaRepository<User, Long> {
    @Query(nativeQuery=true, value="SELECT *  FROM users ORDER BY random() LIMIT :number")
    fun findRandomUsers(@Param("number") numberOfUsers: Int): List<User>
}