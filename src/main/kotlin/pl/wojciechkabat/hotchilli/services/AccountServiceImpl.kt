package pl.wojciechkabat.hotchilli.services

import org.slf4j.LoggerFactory
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.dtos.RegistrationDto
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.exceptions.IncorrectEmailFormatException
import pl.wojciechkabat.hotchilli.exceptions.IncorrectPasswordFormatException
import pl.wojciechkabat.hotchilli.exceptions.NoSuchRoleInDbException
import pl.wojciechkabat.hotchilli.exceptions.UserWithLoginAlreadyExistsException
import pl.wojciechkabat.hotchilli.repositories.RoleRepository
import pl.wojciechkabat.hotchilli.repositories.UserRepository
import pl.wojciechkabat.hotchilli.security.common.RoleEnum
import java.util.regex.Pattern
import javax.transaction.Transactional

@Service
class AccountServiceImpl (
        private val userRepository: UserRepository,
        private val roleRepository: RoleRepository,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder
) : AccountService {
    private val logger = LoggerFactory.getLogger(AccountService::class.java)

    private val VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
    private val VALID_PASSWORD_REGEX = Pattern.compile("^(?=.*[0-9])(?=.*[A-Z]).{6,}$")

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
                        23,
                        ArrayList(),
                        listOf(roleRepository.findByValue(RoleEnum.USER).orElseThrow(({ NoSuchRoleInDbException() }))),
                        bCryptPasswordEncoder.encode(registrationDto.password)
                )
        )

        logger.info("Account created for user with email: " + registrationDto.email)
    }

    private fun validateEmailFormat(email: String) {
        if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
            logger.error("Wrong email format: $email")
            throw IncorrectEmailFormatException()
        }
    }

    private fun validatePasswordFormat(password: String) {
        if (!VALID_PASSWORD_REGEX.matcher(password).find()) {
            logger.error("Wrong password format")
            throw IncorrectPasswordFormatException()
        }
    }
}