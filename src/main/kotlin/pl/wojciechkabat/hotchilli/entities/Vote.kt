package pl.wojciechkabat.hotchilli.entities

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "votes")
data class Vote(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long?,
        @Column(name = "voting_user_id", nullable = false)
        val votingUserId: Long,
        @Column(name = "rated_user_id", nullable = false)
        val ratedUserId: Long,
        @Column(name = "rating", nullable = false)
        val rating: Double,
        @Column(name = "created_at", nullable = false)
        val createdAt: LocalDateTime
)