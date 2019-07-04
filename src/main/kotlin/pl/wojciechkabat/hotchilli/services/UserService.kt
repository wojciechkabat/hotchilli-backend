package pl.wojciechkabat.hotchilli.services

import pl.wojciechkabat.hotchilli.dtos.UpdateUserDto
import pl.wojciechkabat.hotchilli.dtos.UserDto
import pl.wojciechkabat.hotchilli.entities.GenderDisplayOption
import pl.wojciechkabat.hotchilli.entities.User

interface UserService {
    fun findByEmail(email: String): User
    fun getUserDataFor(user: User): UserDto
    fun provideRandomUsers(genderDisplayOption: GenderDisplayOption, requestingUserIdentifier: String): List<UserDto>
    fun updateUserDataFor(user: User, updateUserDto: UpdateUserDto): UserDto
}