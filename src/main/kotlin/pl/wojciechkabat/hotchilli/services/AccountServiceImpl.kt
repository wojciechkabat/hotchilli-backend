package pl.wojciechkabat.hotchilli.services

import org.slf4j.LoggerFactory
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.dtos.PictureDto
import pl.wojciechkabat.hotchilli.dtos.RegistrationDto
import pl.wojciechkabat.hotchilli.entities.Picture
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.exceptions.*
import pl.wojciechkabat.hotchilli.repositories.PictureRepository
import pl.wojciechkabat.hotchilli.repositories.RoleRepository
import pl.wojciechkabat.hotchilli.repositories.UserRepository
import pl.wojciechkabat.hotchilli.security.common.RoleEnum
import pl.wojciechkabat.hotchilli.utils.PictureMapper
import pl.wojciechkabat.hotchilli.utils.Validators
import java.time.LocalDateTime
import javax.transaction.Transactional

@Service
class AccountServiceImpl(
        private val userRepository: UserRepository,
        private val roleRepository: RoleRepository,
        private val pictureService: PictureService,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder
) : AccountService {
    private val logger = LoggerFactory.getLogger(AccountService::class.java)

    @Transactional
    override fun register(registrationDto: RegistrationDto) {
        logger.info("Attempting to register user with email: ${registrationDto.email}")

        if (userRepository.findByEmail(registrationDto.email).isPresent) {
            logger.error("Trying to create account with already existing email: ${registrationDto.email}")
            throw UserWithLoginAlreadyExistsException()
        }

        validateEmailFormat(registrationDto.email)
        validatePasswordFormat(registrationDto.password)

        val user = User(
                id = null,
                email = registrationDto.email,
                username = registrationDto.username,
                password = bCryptPasswordEncoder.encode(registrationDto.password),
                dateOfBirth = registrationDto.dateOfBirth,
                roles = listOf(roleRepository.findByValue(RoleEnum.USER).orElseThrow(({ NoSuchRoleInDbException() }))),
                gender = registrationDto.gender,
                createdAt = LocalDateTime.now()
        )

        if(registrationDto.pictures.isNotEmpty()) {
            registrationDto.pictures.stream().forEach { picture -> user.addPicture(
                    Picture(null,
                            picture.externalIdentifier,
                            picture.url,
                            user
                    ))}
        }

        userRepository.save(user)

        logger.info("Account created for user with email: ${registrationDto.email}")
    }

    @Transactional
    override fun addPicture(pictureDto: PictureDto, user: User): PictureDto {
        val persistedPicture = pictureService.savePicture(pictureDto, user)
        user.addPicture(persistedPicture)
        return PictureMapper.mapToDto(persistedPicture)
    }

    override fun deletePicture(pictureId: Long, user: User) {
        if(user.pictures.stream().noneMatch {picture -> pictureId == picture.id }) {
            throw UserDoesNotOwnResourceException()
        }
        pictureService.deleteById(pictureId)
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