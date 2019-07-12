package pl.wojciechkabat.hotchilli.services

import pl.wojciechkabat.hotchilli.entities.Pin
import pl.wojciechkabat.hotchilli.entities.PinType
import pl.wojciechkabat.hotchilli.entities.User

interface PinService {
    fun generatePinFor(user: User, type: PinType): String
    fun findByTypeAndUser(type: PinType, user: User): Pin
    fun delete(pin: Pin)
}
