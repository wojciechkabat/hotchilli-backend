package pl.wojciechkabat.hotchilli

import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.dtos.UserDto
import pl.wojciechkabat.hotchilli.entities.Picture
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.repositories.UserRepository
import javax.annotation.PostConstruct
import kotlin.collections.ArrayList

@Component
class InitialData(val userRepository: UserRepository) {
    @PostConstruct
    fun init() {
//        val mockUsers: MutableList<UserDto> = ArrayList()
//
//        userRepository.save(
//                User(
//                        null,
//                        "Magdalena",
//                        23,
//                        listOf(Picture(null, null, "https://images.unsplash.com/photo-1511654433543-916c15d46ad6?ixlib=rb-1.2.1&w=1000&q=80"))
//                )
//        )
//
//
//        userRepository.save(
//                User(
//                        null,
//                        "Kasia",
//                        21,
//                        listOf(Picture(null, null, "https://images.pexels.com/photos/247878/pexels-photo-247878.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"))
//                )
//        )
//
//        userRepository.save(
//                User(
//                        null,
//                        "Anita",
//                        18,
//                        listOf(Picture(null, null, "https://i1.wp.com/tricksmaze.com/wp-content/uploads/2017/10/Stylish-Girls-Profile-Pictures-11.jpg?resize=466%2C466&ssl=1"))
//                )
//        )
//
//        userRepository.save(
//                User(
//                        null,
//                        "Kamila",
//                        19,
//                        listOf(Picture(null, null, "https://assets.capitalfm.com/2018/23/lilliya-scarlett-instagram-1528814125-custom-0.png"))
//                )
//        )
//
//        userRepository.save(
//                User(
//                        null,
//                        "Wiktoria",
//                        24,
//                        listOf(Picture(null, null, "https://www.decentfashion.in/wp-content/uploads/2018/10/girl-whatsapp-dp-images-14.jpg"))
//                )
//        )
//
//
//        userRepository.save(
//                User(
//                        null,
//                        "Anna",
//                        26,
//                        listOf(Picture(null, null, "https://i.pinimg.com/originals/f2/51/c1/f251c14e4319222dd53eacdf73e09199.jpg"))
//                )
//        )
    }
}