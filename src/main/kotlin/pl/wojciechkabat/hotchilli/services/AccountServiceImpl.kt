package pl.wojciechkabat.hotchilli.services

import org.slf4j.LoggerFactory
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.dtos.RegistrationDto
import pl.wojciechkabat.hotchilli.entities.Picture
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.exceptions.IncorrectEmailFormatException
import pl.wojciechkabat.hotchilli.exceptions.IncorrectPasswordFormatException
import pl.wojciechkabat.hotchilli.exceptions.NoSuchRoleInDbException
import pl.wojciechkabat.hotchilli.exceptions.UserWithLoginAlreadyExistsException
import pl.wojciechkabat.hotchilli.repositories.RoleRepository
import pl.wojciechkabat.hotchilli.repositories.UserRepository
import pl.wojciechkabat.hotchilli.security.common.RoleEnum
import pl.wojciechkabat.hotchilli.utils.PictureMapper
import pl.wojciechkabat.hotchilli.utils.Validators
import javax.transaction.Transactional

@Service
class AccountServiceImpl (
        private val userRepository: UserRepository,
        private val roleRepository: RoleRepository,
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
                        PictureMapper.mapToEntity(registrationDto.pictures),
                        listOf(roleRepository.findByValue(RoleEnum.USER).orElseThrow(({ NoSuchRoleInDbException() }))),
                        bCryptPasswordEncoder.encode(registrationDto.password)
                )
        )

        logger.info("Account created for user with email: " + registrationDto.email)
    }

    private fun validateEmailFormat(email: String) {
        if (!Validators.validateEmail(email)) {
            logger.error("Wrong email format: $email")
            throw IncorrectEmailFormatException()
        }
    }

    private fun validatePasswordFormat(password: String) {
        if (!Validators.validatePassword(password)) {
            logger.error("Wrong password format")
            throw IncorrectPasswordFormatException()
        }
    }
}