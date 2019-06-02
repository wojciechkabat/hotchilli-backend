package pl.wojciechkabat.hotchilli.repositories

import org.springframework.stereotype.Repository
import pl.wojciechkabat.hotchilli.dtos.PictureDto
import pl.wojciechkabat.hotchilli.dtos.UserDto
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

@Repository
class UserRepositoryMockImpl : UserRepository {
    override fun findRandomUsers(number: Int): List<UserDto> {
        val mockUsers: MutableList<UserDto> = ArrayList()

        val random = Random()
        mockUsers.add(UserDto(random.nextLong(),
                "Magdalena",
                23,
                listOf(PictureDto(random.nextLong(), null, "https://images.unsplash.com/photo-1511654433543-916c15d46ad6?ixlib=rb-1.2.1&w=1000&q=80")),
                7.6,
                311))
        mockUsers.add(UserDto(random.nextLong(),
                "Kasia",
                21,
                listOf(PictureDto(random.nextLong(), null, "https://images.pexels.com/photos/247878/pexels-photo-247878.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500")),
                6.1,
                11))
        mockUsers.add(UserDto(random.nextLong(),
                "Anita",
                18,
                listOf(PictureDto(random.nextLong(), null, "https://i1.wp.com/tricksmaze.com/wp-content/uploads/2017/10/Stylish-Girls-Profile-Pictures-11.jpg?resize=466%2C466&ssl=1")),
                7.6,
                233))
        mockUsers.add(UserDto(
                random.nextLong(), "Kamila",
                19,
                listOf(PictureDto(random.nextLong(), null, "https://assets.capitalfm.com/2018/23/lilliya-scarlett-instagram-1528814125-custom-0.png")),
                8.2,
                1221))
        mockUsers.add(UserDto(random.nextLong(),
                "Wiktoria",
                24,
                listOf(PictureDto(random.nextLong(), null, "https://www.decentfashion.in/wp-content/uploads/2018/10/girl-whatsapp-dp-images-14.jpg")),
                6.9,
                98))
        mockUsers.add(UserDto(random.nextLong(),
                "Anna",
                26,
                listOf(PictureDto(random.nextLong(), null, "https://i.pinimg.com/originals/f2/51/c1/f251c14e4319222dd53eacdf73e09199.jpg")),
                5.8,
                14))


        return mockUsers.stream().limit(number.toLong()).collect(Collectors.toList())
    }
}