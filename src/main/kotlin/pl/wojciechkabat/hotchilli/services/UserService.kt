package pl.wojciechkabat.hotchilli.services

import pl.wojciechkabat.hotchilli.dtos.UserDto
import pl.wojciechkabat.hotchilli.entities.User

interface UserService {
    fun provideRandomUsers(number: Int): List<UserDto>
    fun findByEmail(email: String): User
    fun getUserDataFor(activeUser: User): UserDto
}