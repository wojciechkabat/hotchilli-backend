package pl.wojciechkabat.hotchilli.services

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pl.wojciechkabat.hotchilli.dtos.PictureDto
import pl.wojciechkabat.hotchilli.dtos.RegistrationDto
import pl.wojciechkabat.hotchilli.entities.Gender
import pl.wojciechkabat.hotchilli.entities.Picture
import pl.wojciechkabat.hotchilli.entities.Role
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.exceptions.IncorrectEmailFormatException
import pl.wojciechkabat.hotchilli.exceptions.IncorrectPasswordFormatException
import pl.wojciechkabat.hotchilli.exceptions.UserDoesNotOwnResourceException
import pl.wojciechkabat.hotchilli.exceptions.UserWithLoginAlreadyExistsException
import pl.wojciechkabat.hotchilli.repositories.RoleRepository
import pl.wojciechkabat.hotchilli.repositories.UserRepository
import pl.wojciechkabat.hotchilli.security.common.RoleEnum
import java.time.LocalDate
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
    lateinit var pictureService: PictureService
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

        Mockito.`when`(bCryptPasswordEncoder.encode("123456Kk")).thenReturn("encodedPassword")
        Mockito.`when`(roleRepository.findByValue(RoleEnum.USER)).thenReturn(Optional.of(userRole))

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
                        Gender.MALE
                )
        )

        Mockito.verify(userRepository, times(1)).save(userArgumentCaptor.capture())


        val expectedUser = User(
                id =null,
                email = "some@email.com",
                username = "someUserName",
                password = "encodedPassword",
                dateOfBirth = dateOfBirth,
                roles = listOf(userRole),
                gender = Gender.MALE
        )

        expectedUser.pictures = mutableListOf(
                Picture(
                        null,
                        "externalIdentifier",
                        "http://url",
                        expectedUser
                )
        )

        assertThat(userArgumentCaptor.value).isEqualToIgnoringGivenFields(
                expectedUser, "pictures"
        )
        assertThat(userArgumentCaptor.value.pictures.size).isEqualTo(1)
    }

    @Test(expected = UserWithLoginAlreadyExistsException::class)
    fun shouldThrowExceptionWhenRegisteringAUserThatAlreadyExists() {
        val repeatingEmail = "repeated@email.pl"

        Mockito.`when`(userRepository.findByEmail(repeatingEmail)).thenReturn(Optional.of(mockUserEntity(repeatingEmail)))

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
        val user = User(
                id =null,
                email = "some@email.com",
                username = "someUserName",
                password = "encodedPassword",
                dateOfBirth = LocalDate.now(),
                roles = listOf(Role(0, RoleEnum.USER)),
                gender = Gender.MALE
        )

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
        val user = User(
                id =null,
                email = "some@email.com",
                username = "someUserName",
                password = "encodedPassword",
                dateOfBirth = LocalDate.now(),
                roles = listOf(Role(0, RoleEnum.USER)),
                gender = Gender.MALE
        )

        user.pictures.add(Picture(123L, "asd", "asda", user))

        accountServiceImpl.deletePicture(123L, user)

        Mockito.verify(pictureService, times(1)).deleteById(123L)
    }

    @Test(expected = UserDoesNotOwnResourceException::class)
    fun shouldThrowExceptionIfUserDoesNotOwnPictureWhenDeletingUserPicture() {
        val user = User(
                id =null,
                email = "some@email.com",
                username = "someUserName",
                password = "encodedPassword",
                dateOfBirth = LocalDate.now(),
                roles = listOf(Role(0, RoleEnum.USER)),
                gender = Gender.MALE
        )

        accountServiceImpl.deletePicture(123L, user)

        Mockito.verify(pictureService, times(1)).deleteById(123L)
    }

    private fun mockUserEntity(email: String): User {
        return User(
                1L,
                email,
                "someUserName",
                "somePassword",
                LocalDate.now(),
                ArrayList(),
                ArrayList(),
                gender = Gender.MALE
        )
    }
}