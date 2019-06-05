package pl.wojciechkabat.hotchilli.services

import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.entities.GuestUser
import pl.wojciechkabat.hotchilli.exceptions.NoGuestUserAssociatedToDeviceIdException
import pl.wojciechkabat.hotchilli.repositories.GuestUserRepository

@Service
class GuestUserServiceImpl(
        val guestUserRepository: GuestUserRepository
): GuestUserService {
    override fun findByDeviceId(deviceId: String): GuestUser {
        return guestUserRepository.findByDeviceId(deviceId).orElseThrow { NoGuestUserAssociatedToDeviceIdException() }
    }
}