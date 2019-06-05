package pl.wojciechkabat.hotchilli.services

import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.dtos.GuestLoginDto
import pl.wojciechkabat.hotchilli.dtos.RegistrationDto
import pl.wojciechkabat.hotchilli.entities.GuestUser
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.exceptions.*
import pl.wojciechkabat.hotchilli.repositories.GuestUserRepository
import pl.wojciechkabat.hotchilli.repositories.RoleRepository
import pl.wojciechkabat.hotchilli.repositories.UserRepository
import pl.wojciechkabat.hotchilli.security.common.RoleEnum
import pl.wojciechkabat.hotchilli.security.model.TokenService
import pl.wojciechkabat.hotchilli.utils.EmailValidator
import pl.wojciechkabat.hotchilli.utils.PasswordValidator
import java.util.regex.Pattern
import javax.transaction.Transactional

@Service
class AccountServiceImpl(
        private val userRepository: UserRepository,
        private val guestUserRepository: GuestUserRepository,
        private val roleRepository: RoleRepository,
        private val tokenService: TokenService,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder
) : AccountService {
    private val logger = LoggerFactory.getLogger(AccountService::class.java)

    @Transactional
    override fun register(registrationDto: RegistrationDto) {
        if (userRepository.findByEmail(registrationDto.email).isPresent) {
            logger.error("Trying to create account with already existing email: " + registrationDto.email)
            throw UserWithLoginAlreadyExistsException()
        }

        validateEmailFormat(registrationDto.email)
        validatePasswordFormat(registrationDto.password)

        userRepository.save(
                User(
                        null,
                        registrationDto.email,
                        registrationDto.username,
                        registrationDto.dateOfBirth,
                        ArrayList(),
                        listOf(roleRepository.findByValue(RoleEnum.USER).orElseThrow(({ NoSuchRoleInDbException() }))),
                        bCryptPasswordEncoder.encode(registrationDto.password)
                )
        )

        logger.info("Account created for user with email: " + registrationDto.email)
    }

    override fun loginGuestUser(deviceId: String): Map<String, String> {
        logger.info("Guest login attempt by user with device ID: $deviceId")

        guestUserRepository.findByDeviceId(deviceId).orElseThrow { NoGuestUserAssociatedToDeviceIdException() }

        val authentication = UsernamePasswordAuthenticationToken(
                deviceId,
                null,
                AuthorityUtils.createAuthorityList(RoleEnum.GUEST.name))

        authentication.details = deviceId
        return tokenService.getTokens(authentication, true)
    }

    override fun registerGuestUser(guestLoginDto: GuestLoginDto) {
        guestUserRepository.save(
                GuestUser(
                        null,
                        guestLoginDto.deviceId
                )
        )
        logger.info("Guest account created for user with device ID: ${guestLoginDto.deviceId}")
    }

    private fun validateEmailFormat(email: String) {
        if (!EmailValidator.validate(email)) {
            logger.error("Wrong email format: $email")
            throw IncorrectEmailFormatException()
        }
    }

    private fun validatePasswordFormat(password: String) {
        if (!PasswordValidator.validate(password)) {
            logger.error("Wrong password format")
            throw IncorrectPasswordFormatException()
        }
    }
}