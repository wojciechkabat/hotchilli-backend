package pl.wojciechkabat.hotchilli.entities

import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long?,

        @Column(name = "email", nullable = false)
        var email: String,

        @Column(name = "username", nullable = false)
        var username: String,

        @Column(name = "password")
        var password: String,

        @Column(name = "birthday")
        var dateOfBirth: LocalDate,

        @OneToMany(mappedBy = "owner")
        @Cascade(CascadeType.ALL)
        var pictures: MutableList<Picture> = ArrayList(),

        @ManyToMany(fetch = FetchType.EAGER)
        @Cascade(CascadeType.ALL)
        @JoinTable(name = "user_roles",
                joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")])
        var roles: List<Role>
) {
    fun addPicture(picture: Picture) {
        pictures.add(picture)
        picture.owner = this
    }
}