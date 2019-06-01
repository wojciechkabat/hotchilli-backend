package pl.wojciechkabat.hotchilli.services

import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.dtos.UserDto
import pl.wojciechkabat.hotchilli.repositories.UserRepository

@Service
class UserServiceImpl(val userRepository: UserRepository) : UserService {
    override fun provideRandomUsers(number: Int): List<UserDto> {
        return userRepository.findRandomUsers(number)
    }
}