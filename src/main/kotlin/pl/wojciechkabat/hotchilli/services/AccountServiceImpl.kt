package pl.wojciechkabat.hotchilli.services

import com.cloudinary.utils.StringUtils.isBlank
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.dtos.FacebookLoginDto
import pl.wojciechkabat.hotchilli.dtos.PictureDto
import pl.wojciechkabat.hotchilli.dtos.RegistrationDto
import pl.wojciechkabat.hotchilli.entities.*
import pl.wojciechkabat.hotchilli.exceptions.*
import pl.wojciechkabat.hotchilli.repositories.RoleRepository
import pl.wojciechkabat.hotchilli.repositories.UserRepository
import pl.wojciechkabat.hotchilli.security.common.RoleEnum
import pl.wojciechkabat.hotchilli.security.model.RefreshTokenService
import pl.wojciechkabat.hotchilli.security.model.TokenService
import pl.wojciechkabat.hotchilli.utils.PictureMapper
import pl.wojciechkabat.hotchilli.utils.Validators
import pl.wojciechkabat.hotchilli.utils.facebookModels.FacebookUser
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.stream.Collectors
import javax.transaction.Transactional
import kotlin.collections.ArrayList

@Service
class AccountServiceImpl(
        private val userRepository: UserRepository,
        private val roleRepository: RoleRepository,
        private val pictureService: PictureService,
        private val voteService: VoteService,
        private val pinService: PinService,
        private val emailService: EmailService,
        private val tokenService: TokenService,
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
                roles = mutableListOf(roleRepository.findByValue(RoleEnum.USER).orElseThrow(({ NoSuchRoleInDbException() }))),
                gender = registrationDto.gender,
                createdAt = LocalDateTime.now(),
                isActive = false,
                userSettings = UserSettings(null, true, determineSupportedPushNotificationLanguage(registrationDto.languageCode))
        )

        if (registrationDto.pictures.isNotEmpty()) {
            registrationDto.pictures.stream().forEach { picture ->
                user.addPicture(
                        Picture(null,
                                picture.externalIdentifier,
                                picture.url,
                                user
                        ))
            }
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
    override fun loginFacebookUser(facebookUser: FacebookUser, deviceId: String): Map<String, String> {
        logger.info("Facebook login attempt by user with email: " + facebookUser.email)

        val activeUser = userRepository.findByFacebookId(facebookUser.facebookId).orElseThrow { NoUserAssociatedToFacebookIdException() }
        val authentication = UsernamePasswordAuthenticationToken(
                activeUser.email,
                null,
                AuthorityUtils.createAuthorityList(RoleEnum.FACEBOOK_USER.name))

        authentication.details = deviceId
        return tokenService.getTokens(authentication)
    }

    @Transactional
    override fun registerFacebookUser(currentFacebookUser: FacebookUser, accessToken: String, facebookLoginDto: FacebookLoginDto) {
        val userFoundByEmail = userRepository.findByEmail(currentFacebookUser.email)
        if (userFoundByEmail.isPresent) {
            connectAccountToFacebookFor(userFoundByEmail.get(), currentFacebookUser.facebookId)
            logger.info("Account connected with facebook for user with email: ${currentFacebookUser.email}")
        } else {
            createNewUserFrom(currentFacebookUser, facebookLoginDto)
            logger.info("Account created by Facebook for user with email: ${currentFacebookUser.email}")
        }
    }

    @Transactional
    override fun addPicture(pictureDto: PictureDto, user: User): PictureDto {
        val persistedPicture = pictureService.savePicture(pictureDto, user)
        user.addPicture(persistedPicture)
        return PictureMapper.mapToDto(persistedPicture)
    }

    @Transactional
    override fun deletePicture(pictureId: Long, user: User) {
        if (user.pictures.stream().noneMatch { picture -> pictureId == picture.id }) {
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

    private fun connectAccountToFacebookFor(user: User, facebookId: String): User {
        user.roles.add(
                roleRepository.findByValue(RoleEnum.FACEBOOK_USER)
                        .orElseThrow { NoSuchRoleInDbException() }
        )
        user.facebookId = facebookId
        return user
    }

    private fun createNewUserFrom(facebookUser: FacebookUser, facebookLoginDto: FacebookLoginDto): User {
        val userToCreate = User(
                id = null,
                email = parseEmail(facebookUser),
                facebookId = facebookUser.facebookId,
                username = facebookUser.firstName,
                password = null,
                dateOfBirth = parseDateFrom(facebookUser.birthday),
                pictures = ArrayList(),
                roles = roleRepository.findByValueIn(listOf(RoleEnum.USER, RoleEnum.FACEBOOK_USER)),
                gender = parseSexFrom(facebookUser.gender),
                createdAt = LocalDateTime.now(),
                isActive = false,
                userSettings = UserSettings(null, true, determineSupportedPushNotificationLanguage(facebookLoginDto.languageCode))
        )
        return userRepository.save(userToCreate)
    }

    private fun parseEmail(facebookUser: FacebookUser): String {
        return if (!isBlank(facebookUser.email)) facebookUser.email else "no-email-" + facebookUser.facebookId
    }

    private fun parseDateFrom(birthday: String?): LocalDate {
        if (birthday != null) {
            val dateFormat = SimpleDateFormat("MM/dd/yyyy")
            try {
                return dateFormat.parse(birthday).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            } catch (e: ParseException) {
                throw RuntimeException()
            }

        }
        return LocalDate.now().withYear(2000)
    }

    private fun parseSexFrom(gender: String?): Gender {
        if (gender != null) {
            return when (gender) {
                "male" -> Gender.MALE
                "female" -> Gender.FEMALE
                else -> Gender.FEMALE
            }
        }
        return Gender.FEMALE
    }
}