package pl.wojciechkabat.hotchilli.services

import pl.wojciechkabat.hotchilli.dtos.UserDto

interface UserService {
    fun provideRandomUsers(number: Int): List<UserDto>
}