package pl.wojciechkabat.hotchilli.services

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.entities.GuestUser
import pl.wojciechkabat.hotchilli.entities.User
import java.security.Principal

@Service
class SecurityServiceImpl(
        val userService: UserService,
        val guestUserService: GuestUserService
) : SecurityService {
    override fun isGuest(principal: Principal): Boolean {
        return (principal as UsernamePasswordAuthenticationToken).authorities.contains(SimpleGrantedAuthority("ROLE_GUEST"))
    }

    override fun retrieveGuestUser(principal: Principal): GuestUser {
        return guestUserService.findByDeviceId(principal.name)
    }

    override fun retrieveActiveUser(principal: Principal): User {
        return userService.findByEmail(principal.name)
    }
}