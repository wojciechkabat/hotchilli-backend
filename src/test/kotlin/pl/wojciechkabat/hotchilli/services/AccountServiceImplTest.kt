package pl.wojciechkabat.hotchilli.services

import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pl.wojciechkabat.hotchilli.dtos.PictureDto
import pl.wojciechkabat.hotchilli.dtos.RegistrationDto
import pl.wojciechkabat.hotchilli.entities.*
import pl.wojciechkabat.hotchilli.exceptions.IncorrectEmailFormatException
import pl.wojciechkabat.hotchilli.exceptions.IncorrectPasswordFormatException
import pl.wojciechkabat.hotchilli.exceptions.UserDoesNotOwnResourceException
import pl.wojciechkabat.hotchilli.exceptions.UserWithLoginAlreadyExistsException
import pl.wojciechkabat.hotchilli.repositories.RoleRepository
import pl.wojciechkabat.hotchilli.repositories.UserRepository
import pl.wojciechkabat.hotchilli.security.common.RoleEnum
import pl.wojciechkabat.hotchilli.security.model.RefreshTokenService
import pl.wojciechkabat.hotchilli.security.model.TokenService
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import org.assertj.core.api.Assertions.assertThat as assertThat;

@RunWith(MockitoJUnitRunner::class)
class AccountServiceImplTest {
    @Mock
    lateinit var userRepository: UserRepository
    @Mock
    lateinit var roleRepository: RoleRepository
    @Mock
    lateinit var pinService: PinService
    @Mock
    lateinit var pictureService: PictureService
    @Mock
    lateinit var emailService: EmailService
    @Mock
    lateinit var tokenService: TokenService
    @Mock
    lateinit var refreshTokenService: RefreshTokenService
    @Mock
    lateinit var voteService: VoteService
    @Mock
    lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder
    @InjectMocks
    lateinit var accountServiceImpl: AccountServiceImpl

    @Captor
    private lateinit var userArgumentCaptor: ArgumentCaptor<User>

    @Test
    fun shouldCorrectlyRegisterUser() {
        val userRole = Role(0, RoleEnum.USER)
        val dateOfBirth = LocalDate.now()


        val expectedUser = User(
                id = null,
                email = "some@email.com",
                facebookId = null,
                username = "someUserName",
                password = "encodedPassword",
                dateOfBirth = dateOfBirth,
                roles = mutableListOf(userRole),
                gender = Gender.MALE,
                createdAt = LocalDateTime.now(),
                isActive = false,
                userSettings = UserSettings(null, true, "pl")
        )

        expectedUser.pictures = mutableListOf(
                Picture(
                        null,
                        "externalIdentifier",
                        "http://url",
                        expectedUser
                )
        )

        Mockito.`when`(bCryptPasswordEncoder.encode("123456Kk")).thenReturn("encodedPassword")
        Mockito.`when`(roleRepository.findByValue(RoleEnum.USER)).thenReturn(Optional.of(userRole))
        Mockito.`when`(userRepository.save(any<User>())).thenReturn(expectedUser)

        accountServiceImpl.register(
                RegistrationDto(
                        "some@email.com",
                        "someUserName",
                        "123456Kk",
                        listOf(
                                PictureDto(
                                        null,
                                        "externalIdentifier",
                                        "http://url"
                                )
                        ),
                        dateOfBirth,
                        Gender.MALE,
                        "pl"
                )
        )

        Mockito.verify(userRepository, times(1)).save(userArgumentCaptor.capture())

        assertThat(userArgumentCaptor.value).isEqualToIgnoringGivenFields(
                expectedUser, "pictures", "createdAt"
        )
        assertThat(userArgumentCaptor.value.pictures.size).isEqualTo(1)
    }

    @Test(expected = UserWithLoginAlreadyExistsException::class)
    fun shouldThrowExceptionWhenRegisteringAUserThatAlreadyExists() {
        val repeatingEmail = "repeated@email.pl"

        Mockito.`when`(userRepository.findByEmail(repeatingEmail)).thenReturn(Optional.of(TestUtils.mockUserEntity(repeatingEmail)))

        accountServiceImpl.register(
                RegistrationDto(
                        repeatingEmail,
                        "somePassword",
                        "someUserName",
                        ArrayList(),
                        LocalDate.now(),
                        gender = Gender.MALE
                )
        )
    }

    @Test(expected = IncorrectEmailFormatException::class)
    fun shouldThrowExceptionWhenRegisteringAUserAndEmailFormatIsIncorrect() {
        val incorrectEmail = "incorrectemail"

        accountServiceImpl.register(
                RegistrationDto(
                        incorrectEmail,
                        "somePassword",
                        "someUserName",
                        ArrayList(),
                        LocalDate.now(),
                        gender = Gender.MALE
                )
        )
    }

    @Test(expected = IncorrectPasswordFormatException::class)
    fun shouldThrowExceptionWhenRegisteringAUserAndPasswordFormatIsIncorrect() {
        val incorrectlyFormattedPassword = "incorrectPassword"
        accountServiceImpl.register(
                RegistrationDto(
                        "some@email.com",
                        incorrectlyFormattedPassword,
                        "someUserName",
                        ArrayList(),
                        LocalDate.now(),
                        gender = Gender.MALE
                )
        )
    }

    @Test
    fun shouldAddUserPicture() {
        val user = TestUtils.mockUserEntity("someemail")

        val pictureDto = PictureDto(
                null,
                "someExternalIdentifier",
                "http://url1"
        )

        Mockito.`when`(pictureService.savePicture(pictureDto, user)).thenReturn(Picture(12L, "someExternalIdentifier", "http://url1", user))

        accountServiceImpl.addPicture(pictureDto, user)

        Mockito.verify(pictureService, times(1)).savePicture(pictureDto, user)
        assertThat(user.pictures.size).isEqualTo(1)
    }

    @Test
    fun shouldDeleteUserPicture() {
        val user = TestUtils.mockUserEntity("someemail")

        user.pictures.add(Picture(123L, "asd", "asda", user))

        accountServiceImpl.deletePicture(123L, user)

        Mockito.verify(pictureService, times(1)).deleteById(123L)
    }

    @Test(expected = UserDoesNotOwnResourceException::class)
    fun shouldThrowExceptionIfUserDoesNotOwnPictureWhenDeletingUserPicture() {
        val user = TestUtils.mockUserEntity("someemail")
        user.pictures = ArrayList()

        accountServiceImpl.deletePicture(123L, user)

        Mockito.verify(pictureService, times(1)).deleteById(123L)
    }

    @Test
    fun shouldDeletePicturesWhenDeletingAccount() {
        val user = TestUtils.mockUserEntity("someemail")

        user.pictures = mutableListOf(
                Picture(
                        122L,
                        "someExternalId",
                        "someUrl",
                        user
                ),
                Picture(
                        123L,
                        "someExternalId",
                        "someUrl",
                        user
                ))

        accountServiceImpl.deleteAccountFor(user)

        argumentCaptor<List<Long>>().apply {
            verify(pictureService).deleteByIds(capture())
            assertThat(firstValue).contains(122L, 123L)
        }
    }


    @Test
    fun shouldDeleteTokensWhenDeletingAccount() {
        val user = TestUtils.mockUserEntity("someemail")

        accountServiceImpl.deleteAccountFor(user)

        argumentCaptor<List<Long>>().apply {
            verify(refreshTokenService).deleteByUser(user)
        }
    }


    @Test
    fun shouldDeleteAllVotesForUserWhenDeletingAccount() {
        val user = TestUtils.mockUserEntity("someemail")

        accountServiceImpl.deleteAccountFor(user)

        argumentCaptor<List<Long>>().apply {
            verify(voteService).deleteAllVotesForUser(user)
        }
    }

    @Test
    fun shouldDeleteUserWhenDeletingAccount() {
        val user = TestUtils.mockUserEntity("someemail")

        accountServiceImpl.deleteAccountFor(user)

        argumentCaptor<List<Long>>().apply {
            verify(userRepository).delete(user)
        }
    }

    @Test
    fun shouldConfirmAccount() {
        val user = TestUtils.mockUserEntity("someEmail")
        user.isActive = false

        val pinValue = "1234"
        val providedPinByUser = "1234"

        `when`(pinService.findByTypeAndUser(PinType.CONFIRMATION, user)).thenReturn(
                Pin(
                        123L,
                        pinValue,
                        PinType.CONFIRMATION,
                        user
                )
        )
        accountServiceImpl.confirmAccount(providedPinByUser, user)
        assertThat(user.isActive!!).isTrue()
    }

    @Test
    fun shouldGenerateAndPersistConfirmationPinWhenRegistering() {
        val persistedUser = TestUtils.mockUserEntity(99L)

        `when`(roleRepository.findByValue(RoleEnum.USER)).thenReturn(Optional.of(Role(1L, RoleEnum.USER)))
        `when`(userRepository.save(any<User>())).thenReturn(persistedUser)
        `when`(bCryptPasswordEncoder.encode(any())).thenReturn("someEncodedPassword")

        accountServiceImpl.register(
                RegistrationDto(
                        "some@email.com",
                        "someUserName",
                        "123456Kk",
                        listOf(
                                PictureDto(
                                        null,
                                        "externalIdentifier",
                                        "http://url"
                                )
                        ),
                        LocalDate.now(),
                        Gender.MALE
                )
        )

        verify(pinService, times(1)).generatePinFor(persistedUser, PinType.CONFIRMATION)
    }

    @Test
    fun shouldSendAccountConfirmationEmailWhenRegistering() {
        val userToRegister = TestUtils.mockUserEntity(1L)

        `when`(roleRepository.findByValue(RoleEnum.USER)).thenReturn(Optional.of(Role(1L, RoleEnum.USER)))
        `when`(userRepository.save(any<User>())).thenReturn(userToRegister)
        `when`(pinService.generatePinFor(userToRegister, PinType.CONFIRMATION)).thenReturn("1234")
        `when`(bCryptPasswordEncoder.encode("123456Kk")).thenReturn("encodedPassword")

        accountServiceImpl.register(
                RegistrationDto(
                        "some@email.com",
                        "someUserName",
                        "123456Kk",
                        listOf(
                                PictureDto(
                                        null,
                                        "externalIdentifier",
                                        "http://url"
                                )
                        ),
                        LocalDate.now(),
                        Gender.MALE
                )
        )
        verify(emailService).sendAccountConfirmationEmail("some@email.com", "en", "1234")
    }

    @Test
    fun shouldSetPushNotificationsLanguageToSupportedLanguageWhenRegistering() {
        `when`(roleRepository.findByValue(RoleEnum.USER)).thenReturn(Optional.of(Role(1L, RoleEnum.USER)))
        `when`(userRepository.save(any<User>())).thenReturn(TestUtils.mockUserEntity("somemeial"))
        `when`(bCryptPasswordEncoder.encode("123456Kk")).thenReturn("encodedPassword")

        accountServiceImpl.register(
                RegistrationDto(
                        "some@email.com",
                        "someUserName",
                        "123456Kk",
                        listOf(
                                PictureDto(
                                        null,
                                        "externalIdentifier",
                                        "http://url"
                                )
                        ),
                        LocalDate.now(),
                        Gender.MALE,
                        "pl"
                )
        )

        verify(userRepository).save(userArgumentCaptor.capture())
        assertThat(userArgumentCaptor.value.userSettings).isEqualToComparingFieldByField(
                UserSettings(
                        null,
                        true,
                        "pl"
                )
        )
    }

    @Test
    fun shouldSetPushNotificationsLanguageToEnglishIfUserLanguageNotSupportedWhenRegistering() {
        `when`(roleRepository.findByValue(RoleEnum.USER)).thenReturn(Optional.of(Role(1L, RoleEnum.USER)))
        `when`(userRepository.save(any<User>())).thenReturn(TestUtils.mockUserEntity("somemeial"))
        `when`(bCryptPasswordEncoder.encode("123456Kk")).thenReturn("encodedPassword")

        accountServiceImpl.register(
                RegistrationDto(
                        "some@email.com",
                        "someUserName",
                        "123456Kk",
                        listOf(
                                PictureDto(
                                        null,
                                        "externalIdentifier",
                                        "http://url"
                                )
                        ),
                        LocalDate.now(),
                        Gender.MALE,
                        "hu"
                )
        )

        verify(userRepository).save(userArgumentCaptor.capture())
        assertThat(userArgumentCaptor.value.userSettings).isEqualToComparingFieldByField(
                UserSettings(
                        null,
                        true,
                        "en"
                )
        )
    }

    @Test
    fun shouldResendConfirmationPinEmail() {
        val user = TestUtils.mockUserEntity("someemail@pl.pl")
        `when`(pinService.generatePinFor(user, PinType.CONFIRMATION)).thenReturn("1234")
        accountServiceImpl.resendConfirmationEmail(user)
        verify(emailService, times(1)).sendAccountConfirmationEmail("someemail@pl.pl", "en", "1234")
    }

    @Test
    fun shouldGenerateNewPinWhenResendingConfirmationMail() {
        val user = TestUtils.mockUserEntity("someemail@pl.pl")
        accountServiceImpl.resendConfirmationEmail(user)
        verify(pinService, times(1)).generatePinFor(user, PinType.CONFIRMATION)
    }
}