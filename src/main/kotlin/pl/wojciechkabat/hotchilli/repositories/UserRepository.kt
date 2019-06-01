package pl.wojciechkabat.hotchilli.repositories

import pl.wojciechkabat.hotchilli.dtos.UserDto

interface UserRepository {
    fun findRandomUsers(number: Int): List<UserDto>
}