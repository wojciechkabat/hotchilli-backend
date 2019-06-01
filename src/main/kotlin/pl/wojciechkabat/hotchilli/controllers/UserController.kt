package pl.wojciechkabat.hotchilli.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.wojciechkabat.hotchilli.dtos.UserDto
import pl.wojciechkabat.hotchilli.services.UserServiceImpl

@RestController
class UserController(
        val userService: UserServiceImpl
) {
    @GetMapping("/users/random")
    fun greeting(@RequestParam(value = "number") number: Int): List<UserDto> {
        return userService.provideRandomUsers(number)
    }

}