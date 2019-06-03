package pl.wojciechkabat.hotchilli.entities

import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long?,

        @Column(name = "email", nullable = false)
        val email: String,

        @Column(name = "username", nullable = false)
        val username: String,

        @Column(name = "age", nullable = false)
        val age: Int,

        @OneToMany
        @Cascade(CascadeType.ALL)
        @JoinColumn(name = "user_id", referencedColumnName = "id")
        val pictures: List<Picture>,

        @ManyToMany(fetch = FetchType.EAGER)
        @Cascade(CascadeType.ALL)
        @JoinTable(name = "user_roles",
                joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")])
        val roles: List<Role>,

        @Column(name = "password")
        val password: String
)