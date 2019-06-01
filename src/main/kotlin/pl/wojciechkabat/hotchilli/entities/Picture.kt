package pl.wojciechkabat.hotchilli.entities

import javax.persistence.*

@Entity
@Table(name = "pictures")
data class Picture(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        @Column(name = "external_id")
        val externalIdentifier: String?,

        @Column(name = "url", nullable = false)
        val url: String
)
