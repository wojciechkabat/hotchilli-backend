package pl.wojciechkabat.hotchilli.entities

import javax.persistence.*

@Entity
@Table(name = "guest_users")
data class GuestUser(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long?,

        @Column(name = "device_id")
        val deviceId: String
)