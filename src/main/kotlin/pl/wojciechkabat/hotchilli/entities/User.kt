package pl.wojciechkabat.hotchilli.entities

import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "users")
@NamedEntityGraphs(
        NamedEntityGraph(name = "User.eagerRoles", attributeNodes = [NamedAttributeNode("roles")]),
        NamedEntityGraph(name = "User.eagerPictures", attributeNodes = [NamedAttributeNode("pictures")])
)
data class User(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long?,

        @Column(name = "email", nullable = false)
        var email: String,

        @Column(name = "facebook_id")
        var facebookId: String? = null,

        @Column(name = "username", nullable = false)
        var username: String,

        @Column(name = "password")
        var password: String? = null,

        @Column(name = "birthday")
        var dateOfBirth: LocalDate,

        @OneToMany(mappedBy = "owner")
        @Cascade(CascadeType.ALL)
        var pictures: MutableList<Picture> = ArrayList(),

        @ManyToMany
        @JoinTable(name = "user_roles",
                joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")])
        var roles: MutableList<Role>,

        @Column(name = "gender")
        @Enumerated(EnumType.STRING)
        var gender: Gender,

        @Column(name = "created_at")
        var createdAt: LocalDateTime,

        @Column(name = "is_active")
        var isActive: Boolean? = null,

        @OneToOne(fetch = FetchType.LAZY)
        @Cascade(CascadeType.ALL)
        @JoinColumn(name = "settings_id")
        val userSettings: UserSettings
) {
    fun addPicture(picture: Picture) {
        pictures.add(picture)
        picture.owner = this
    }
}