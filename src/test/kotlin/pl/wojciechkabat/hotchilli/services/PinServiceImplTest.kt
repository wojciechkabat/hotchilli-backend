package pl.wojciechkabat.hotchilli.services

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import pl.wojciechkabat.hotchilli.entities.Pin
import pl.wojciechkabat.hotchilli.entities.PinType
import pl.wojciechkabat.hotchilli.repositories.PinRepository
import java.util.*
import java.util.regex.Pattern

import org.assertj.core.api.Assertions.assertThat as assertThat

@RunWith(MockitoJUnitRunner::class)
class PinServiceImplTest {
    @Mock
    lateinit var pinRepository: PinRepository
    @InjectMocks
    lateinit var pinService: PinServiceImpl

    @Captor
    lateinit var pinCaptor: ArgumentCaptor<Pin>

    private val email = "test@test.pl"

    @Test
    fun shouldGenerate4DigitConfirmationPin() {
        val user = TestUtils.mockUserEntity(email)

        val generatedNumber = pinService.generatePinFor(user, PinType.CONFIRMATION)
        assertThat(generatedNumber).hasSize(4)
        assertThat(java.lang.Long.valueOf(generatedNumber)).isGreaterThan(0)
    }

    @Test
    fun shouldGenerate4DigitConfirmationPinWithoutChar() {
        val user = TestUtils.mockUserEntity(email)

        val generatedNumber = pinService.generatePinFor(user, PinType.CONFIRMATION)
        assertThat(generatedNumber).containsOnlyDigits()
    }

    @Test
    fun shouldGenerate6DigitResetPasswordPin() {
        val user = TestUtils.mockUserEntity(email)

        val generatedNumber = pinService.generatePinFor(user, PinType.PASSWORD_RESET)
        assertThat(generatedNumber).hasSize(6)
        assertThat(generatedNumber).containsPattern(Pattern.compile("[A-Za-z0-9]{6}"))
    }


    @Test
    fun shouldGenerateAndStoreResetPasswordPin() {
        val user = TestUtils.mockUserEntity(email)

        `when`(pinRepository.findByTypeAndUser(PinType.PASSWORD_RESET, user)).thenReturn(Optional.empty())

        pinService.generatePinFor(user, PinType.PASSWORD_RESET)

        verify(pinRepository).save(pinCaptor.capture())
        assertThat(pinCaptor.value).isEqualToIgnoringGivenFields(
                Pin(
                        null,
                        "someValue",
                        PinType.PASSWORD_RESET,
                        user
                ), "value")
        assertThat(pinCaptor.value.value).isNotEmpty()
    }

    @Test
    fun shouldGenerateAndChangeResetPasswordPinWhenOneExists() {
        val user = TestUtils.mockUserEntity(email)

        `when`(pinRepository.findByTypeAndUser(PinType.PASSWORD_RESET, user))
                .thenReturn(Optional.of(Pin(123L, "1234", PinType.PASSWORD_RESET, user)))

        pinService.generatePinFor(user, PinType.PASSWORD_RESET)

        verify(pinRepository).saveAndFlush(pinCaptor.capture())
        assertThat(pinCaptor.value).isEqualToIgnoringGivenFields(
                Pin(
                        123L,
                        "someValue",
                        PinType.PASSWORD_RESET,
                        user
                ), "value")
        assertThat(pinCaptor.value.value).isNotEmpty()
    }

}