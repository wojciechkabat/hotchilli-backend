package pl.wojciechkabat.hotchilli.repositories

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.wojciechkabat.hotchilli.entities.User
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {
    @EntityGraph(value = "User.eagerPictures")
    fun findUsersByIdIn(@Param("ids") ids: Set<Long>): List<User>

    @EntityGraph(value = "User.eagerRoles")
    fun findByEmail(email: String): Optional<User>

    @Query("SELECT coalesce(max(u.id), 0) FROM User u")
    fun getMaxId(): Long

    fun findByFacebookId(facebookId: String): Optional<User>
}