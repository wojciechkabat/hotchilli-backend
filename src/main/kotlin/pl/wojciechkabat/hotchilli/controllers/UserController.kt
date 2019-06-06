package pl.wojciechkabat.hotchilli.controllers

import org.springframework.web.bind.annotation.*
import pl.wojciechkabat.hotchilli.dtos.UserDto
import pl.wojciechkabat.hotchilli.services.UserServiceImpl

@RestController
@CrossOrigin
class UserController(
        val userService: UserServiceImpl
) {
    @GetMapping("/users/random")
    fun getRandomUsers(@RequestParam("number") number: Int): List<UserDto> {
        return userService.provideRandomUsers(number)
    }

    @GetMapping("guest/users/random")
    fun getRandomUsersForGuest(@RequestParam("number") number: Int): List<UserDto> {
        return userService.provideRandomUsers(number)
    }

}