package pl.wojciechkabat.hotchilli.services

import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.dtos.PictureDto
import pl.wojciechkabat.hotchilli.dtos.RegistrationDto
import pl.wojciechkabat.hotchilli.entities.Picture
import pl.wojciechkabat.hotchilli.entities.PinType
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.entities.UserSettings
import pl.wojciechkabat.hotchilli.exceptions.*
import pl.wojciechkabat.hotchilli.repositories.RoleRepository
import pl.wojciechkabat.hotchilli.repositories.UserRepository
import pl.wojciechkabat.hotchilli.security.common.RoleEnum
import pl.wojciechkabat.hotchilli.security.model.RefreshTokenService
import pl.wojciechkabat.hotchilli.utils.PictureMapper
import pl.wojciechkabat.hotchilli.utils.Validators
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors
import javax.transaction.Transactional

@Service
class AccountServiceImpl(
        private val userRepository: UserRepository,
        private val roleRepository: RoleRepository,
        private val pictureService: PictureService,
        private val voteService: VoteService,
        private val pinService: PinService,
        private val emailService: EmailService,
        private val refreshTokenService: RefreshTokenService,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder
) : AccountService {
    private val logger = LoggerFactory.getLogger(AccountService::class.java)
    private val SUPPORTED_PUSH_NOTIFICATION_LANGUAGES = Arrays.asList("pl", "en")

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
                createdAt = LocalDateTime.now(),
                isActive = false,
                userSettings = UserSettings(null, true, determineSupportedPushNotificationLanguage(registrationDto.languageCode))
        )

        if(registrationDto.pictures.isNotEmpty()) {
            registrationDto.pictures.stream().forEach { picture -> user.addPicture(
                    Picture(null,
                            picture.externalIdentifier,
                            picture.url,
                            user
                    ))}
        }

        val persistedUser = userRepository.save(user)
        val confirmationPin = pinService.generatePinFor(persistedUser, PinType.CONFIRMATION)

        logger.info("Account created for user with email: ${registrationDto.email}")

        emailService.sendAccountConfirmationEmail(
                user.email,
                persistedUser.userSettings.notificationsLanguageCode,
                confirmationPin
        )
    }

    @Transactional
    override fun confirmAccount(pin: String, user: User) {
        val confirmationPinForUser = pinService.findByTypeAndUser(PinType.CONFIRMATION, user)
        if (!confirmationPinForUser.isValid(pin)) {
            logger.error("Wrong PIN given for account confirmation for user: ${user.email}")
            throw IncorrectPINGivenException()
        }
        user.isActive = true
        pinService.delete(confirmationPinForUser)
        logger.info("Account confirmed for user with email: ${user.email}")
    }

    override fun resendConfirmationEmail(activeUser: User) {
        logger.info("Resend confirmation email function invoked: " + activeUser.email)
        val confirmationPin = pinService.generatePinFor(activeUser, PinType.CONFIRMATION)
        emailService.sendAccountConfirmationEmail(
                activeUser.email,
                activeUser.userSettings.notificationsLanguageCode,
                confirmationPin
        )
    }

    @Transactional
    override fun deleteAccountFor(user: User) {
        refreshTokenService.deleteByUser(user)
        SecurityContextHolder.clearContext()
        pictureService.deleteByIds(user.pictures.stream().map { picture -> picture.id!! }.collect(Collectors.toList()))
        user.pictures.clear()
        voteService.deleteAllVotesForUser(user)
        userRepository.delete(user)
    }

    @Transactional
    override fun addPicture(pictureDto: PictureDto, user: User): PictureDto {
        val persistedPicture = pictureService.savePicture(pictureDto, user)
        user.addPicture(persistedPicture)
        return PictureMapper.mapToDto(persistedPicture)
    }

    @Transactional
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


    private fun determineSupportedPushNotificationLanguage(userLanguageCode: String?): String {
        val langugeCodeToPersist: String = userLanguageCode ?: "en"
        return if (SUPPORTED_PUSH_NOTIFICATION_LANGUAGES.contains(langugeCodeToPersist.toLowerCase())) langugeCodeToPersist.toLowerCase() else "en"
    }
}