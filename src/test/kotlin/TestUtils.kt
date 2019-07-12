import pl.wojciechkabat.hotchilli.entities.Gender
import pl.wojciechkabat.hotchilli.entities.User
import java.time.LocalDate
import java.time.LocalDateTime

class TestUtils  {
    companion object TestUtils {
        fun mockUserEntity(email: String): User {
            return User(
                    1L,
                    email,
                    "someUserName",
                    "somePassword",
                    LocalDate.now(),
                    ArrayList(),
                    ArrayList(),
                    gender = Gender.MALE,
                    createdAt = LocalDateTime.now()
            )
        }

        fun mockUserEntity(id: Long): User {
            return User(
                    id,
                    "someEmail@pl.pl",
                    "someUserName",
                    "somePassword",
                    LocalDate.now(),
                    java.util.ArrayList(),
                    java.util.ArrayList(),
                    gender = Gender.MALE,
                    createdAt = LocalDateTime.now()
            )
        }
    }

}