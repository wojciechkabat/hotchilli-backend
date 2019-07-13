package pl.wojciechkabat.hotchilli.entities

import javax.persistence.*

@Table(name = "PINS")
@Entity
data class Pin(
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(name = "value", nullable = false)
        var value: String,

        @Column(name = "type", nullable = false)
        @Enumerated(value = EnumType.STRING)
        val type: PinType,

        @OneToOne
        @JoinColumn(name = "user_id")
        val user: User
) {
    fun isValid(pinGivenByUser: String): Boolean {
        return this.value == pinGivenByUser
    }
}