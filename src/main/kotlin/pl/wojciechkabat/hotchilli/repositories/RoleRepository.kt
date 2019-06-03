package pl.wojciechkabat.hotchilli.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pl.wojciechkabat.hotchilli.entities.Role
import pl.wojciechkabat.hotchilli.security.common.RoleEnum
import java.util.*

@Repository
interface RoleRepository: JpaRepository<Role, Long> {
    fun findByValue(value: RoleEnum): Optional<Role>
}