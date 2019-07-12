package pl.wojciechkabat.hotchilli.services

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.entities.Pin
import pl.wojciechkabat.hotchilli.entities.PinType
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.exceptions.NoSuchPinInDBException
import pl.wojciechkabat.hotchilli.repositories.PinRepository
import javax.transaction.Transactional


@Service
@Transactional
class PinServiceImpl(var pinRepository: PinRepository) : PinService {
    override fun findByTypeAndUser(type: PinType, user: User): Pin {
        return pinRepository.findByTypeAndUser(PinType.CONFIRMATION, user).orElseThrow { NoSuchPinInDBException() }
    }

    override fun delete(pin: Pin) {
        pinRepository.delete(pin)
    }

    override fun generatePinFor(user: User, type: PinType): String {
        val pin = generatePin(type)
        saveOrUpdatePin(pin, user, type)
        return pin
    }

    private fun saveOrUpdatePin(pinValue: String, user: User, pinType: PinType) {
        val pin = pinRepository.findByTypeAndUser(pinType, user)
        if (pin.isPresent) {
            pin.get().value = pinValue
            pinRepository.saveAndFlush(pin.get())
        } else {
            pinRepository.save(
                    Pin(
                            null,
                            pinValue,
                            pinType,
                            user
                    )
            )
        }
    }

    private fun generatePin(type: PinType): String {
        return if (PinType.CONFIRMATION === type) {
            RandomStringUtils.randomNumeric(4, 4)
        } else RandomStringUtils.randomAlphanumeric(6, 6) // else PASSWORD_RESET
    }
}