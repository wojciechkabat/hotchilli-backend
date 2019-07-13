package pl.wojciechkabat.hotchilli.entities

import javax.persistence.*

@Table(name = "user_settings")
@Entity
data class UserSettings(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(name = "notifications_enabled", nullable = false)
        var notificationsEnabled: Boolean,

        @Column(name = "notifications_language", nullable = false, length = 2)
        val notificationsLanguageCode: String
)