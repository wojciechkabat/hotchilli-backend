package pl.wojciechkabat.hotchilli.controllers

import org.springframework.web.bind.annotation.*
import pl.wojciechkabat.hotchilli.dtos.UserDto
import pl.wojciechkabat.hotchilli.services.SecurityService
import pl.wojciechkabat.hotchilli.services.UserServiceImpl
import java.security.Principal

@RestController
@CrossOrigin
class UserController(
        val userService: UserServiceImpl,
        val securityService: SecurityService
) {

    @GetMapping("users/me")
    fun getInfoForCurrentUser(principal: Principal): UserDto {
        val activeUser = securityService.retrieveActiveUser(principal)
        return userService.getUserDataFor(activeUser)
    }

    @GetMapping("/users/random")
    fun getRandomUsers(@RequestParam("number") number: Int): List<UserDto> {
        return userService.provideRandomUsers(number)
    }

    @GetMapping("guest/users/random")
    fun getRandomUsersForGuest(@RequestParam("number") number: Int): List<UserDto> {
        return userService.provideRandomUsers(number)
    }

}