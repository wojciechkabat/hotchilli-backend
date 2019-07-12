package pl.wojciechkabat.hotchilli.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.wojciechkabat.hotchilli.entities.Pin
import pl.wojciechkabat.hotchilli.entities.PinType
import pl.wojciechkabat.hotchilli.entities.User
import java.util.*

@Repository
interface PinRepository: JpaRepository<Pin, Long> {
    fun findByTypeAndUser(type: PinType, user: User): Optional<Pin>
}