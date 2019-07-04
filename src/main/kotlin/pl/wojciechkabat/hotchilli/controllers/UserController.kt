package pl.wojciechkabat.hotchilli.controllers

import org.springframework.web.bind.annotation.*
import pl.wojciechkabat.hotchilli.dtos.UpdateUserDto
import pl.wojciechkabat.hotchilli.dtos.UserDto
import pl.wojciechkabat.hotchilli.entities.GenderDisplayOption
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

    @PutMapping("users/me")
    fun updateUserData(principal: Principal, @RequestBody updateUserDto: UpdateUserDto): UserDto {
        val activeUser = securityService.retrieveActiveUser(principal)
        return userService.updateUserDataFor(activeUser, updateUserDto)
    }

    @GetMapping("/users/random")
    fun getRandomUsers(principal: Principal, @RequestParam("genderDisplay") genderDisplayOption: GenderDisplayOption): List<UserDto> {
        val activeUser = securityService.retrieveActiveUser(principal)
        return userService.provideRandomUsers(genderDisplayOption, activeUser.id!!.toString())
    }

    @GetMapping("guest/users/random")
    fun getRandomUsersForGuest(@RequestParam("genderDisplay") genderDisplayOption: GenderDisplayOption,
                               @RequestParam("deviceId") deviceId: String): List<UserDto> {
        return userService.provideRandomUsers(genderDisplayOption, deviceId)
    }

}