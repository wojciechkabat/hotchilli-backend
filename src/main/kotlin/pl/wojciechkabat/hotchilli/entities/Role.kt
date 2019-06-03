package pl.wojciechkabat.hotchilli.entities

import pl.wojciechkabat.hotchilli.security.common.RoleEnum
import javax.persistence.*

@Entity
@Table(name = "roles")
data class Role(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        @Enumerated(value = EnumType.STRING)
        val value: RoleEnum? = null
)
