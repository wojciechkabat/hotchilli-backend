package pl.wojciechkabat.hotchilli.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.wojciechkabat.hotchilli.entities.GuestUser
import pl.wojciechkabat.hotchilli.entities.User
import java.util.*

@Repository
interface GuestUserRepository : JpaRepository<GuestUser, Long> {
    fun findByDeviceId(deviceId: String): Optional<GuestUser>
}