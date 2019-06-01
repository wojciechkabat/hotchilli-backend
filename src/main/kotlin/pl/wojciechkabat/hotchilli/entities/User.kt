package pl.wojciechkabat.hotchilli.entities

import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        @Column(name = "username", nullable = false)
        val username: String,

        @Column(name = "age", nullable = false)
        val age: Int,

        @OneToMany
        @JoinColumn(name = "user_id", referencedColumnName = "id")
        val pictures: List<Picture>
)