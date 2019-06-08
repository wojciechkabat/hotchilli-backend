package pl.wojciechkabat.hotchilli.services

import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.entities.User
import java.security.Principal

@Service
class SecurityServiceImpl(
        val userService: UserService
): SecurityService {
    override fun retrieveActiveUser(principal: Principal): User {
        return userService.findByEmail(principal.name)
    }
}