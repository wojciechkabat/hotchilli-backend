package pl.wojciechkabat.hotchilli.services

import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.junit.MockitoJUnitRunner
import pl.wojciechkabat.hotchilli.entities.*
import pl.wojciechkabat.hotchilli.repositories.UserRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors
import java.util.stream.LongStream
import kotlin.collections.ArrayList
import org.assertj.core.api.Assertions.assertThat as assertThat

@RunWith(MockitoJUnitRunner::class)
class UserServiceImplTest {
    @Mock
    lateinit var random: Random
    @Mock
    lateinit var userRepository: UserRepository
    @Mock
    lateinit var voteService: VoteService
    @InjectMocks
    lateinit var userService: UserServiceImpl

    @Test
    fun shouldNotProvideRandomUsersThatIHaveVotedForBefore() {
        val requestingUserIdentifier = "1"
        val idsOfUsersAlreadyVotedFor = setOf(10L, 20L, 30L)

        Mockito.`when`(userRepository.getMaxId()).thenReturn(250L)
        Mockito.`when`(voteService.findIdsOfUsersVotedFor(requestingUserIdentifier)).thenReturn(idsOfUsersAlreadyVotedFor)
        Mockito.`when`(random.longs(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(generateLongStreamOf(12L, 13L, 10L, 30L))
        Mockito.`when`(userRepository.findUsersByIdIn(any())).thenReturn(listOf(TestUtils.mockUserEntity(12L)))

        userService.provideRandomUsers(GenderDisplayOption.MALE, requestingUserIdentifier)

        argumentCaptor<Set<Long>>().apply {
            verify(userRepository).findUsersByIdIn(capture())
            assertThat(firstValue).doesNotContain(10L, 20L, 30L)
        }
    }

    @Test
    fun shouldNotReturnActiveUserWhenProvidingRandomUsers() {
        val requestingUserIdentifier = "1"
        val idsOfUsersAlreadyVotedFor = emptySet<Long>()

        Mockito.`when`(userRepository.getMaxId()).thenReturn(250L)
        Mockito.`when`(voteService.findIdsOfUsersVotedFor(requestingUserIdentifier)).thenReturn(idsOfUsersAlreadyVotedFor)
        Mockito.`when`(random.longs(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(generateLongStreamOf(12L, 1L))
        Mockito.`when`(userRepository.findUsersByIdIn(any())).thenReturn(listOf(TestUtils.mockUserEntity(12L)))

        userService.provideRandomUsers(GenderDisplayOption.MALE, requestingUserIdentifier)

        argumentCaptor<Set<Long>>().apply {
            verify(userRepository).findUsersByIdIn(capture())
            assertThat(firstValue).doesNotContain(1L)
            assertThat(firstValue).contains(12L)
        }
    }

    @Test
    fun shouldNotReturnUsersThatDoNotPassGenderCriteria() {
        val requestingUserIdentifier = "1"
        val idsOfUsersAlreadyVotedFor = setOf(10L, 20L, 30L, 40L)
        val expectedFilteredOutUserIds = setOf(12L, 31L, 45L)

        Mockito.`when`(userRepository.getMaxId()).thenReturn(250L)
        Mockito.`when`(voteService.findIdsOfUsersVotedFor(requestingUserIdentifier)).thenReturn(idsOfUsersAlreadyVotedFor)
        Mockito.`when`(random.longs(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(generateLongStreamOf(12L, 10L, 30L, 31L, 45L))
        Mockito.`when`(userRepository.findUsersByIdIn(expectedFilteredOutUserIds)).thenReturn(
                listOf(
                        User(12L, "email", "username", "password", LocalDate.now(), ArrayList(), ArrayList(), Gender.MALE, createdAt = LocalDateTime.now(), userSettings = UserSettings(12L, true, "en")),
                        User(31L, "email", "username", "password", LocalDate.now(), ArrayList(), ArrayList(), Gender.FEMALE, createdAt = LocalDateTime.now(), userSettings = UserSettings(12L, true, "en")),
                        User(45L, "email", "username", "password", LocalDate.now(), ArrayList(), ArrayList(), Gender.FEMALE, createdAt = LocalDateTime.now(), userSettings = UserSettings(12L, true, "en"))
                )
        )

        val providedUsers = userService.provideRandomUsers(GenderDisplayOption.MALE, requestingUserIdentifier)
        val providedUsersIds = providedUsers.stream().map { it.id }.collect(Collectors.toList())
        assertThat(providedUsersIds).contains(12L)
        assertThat(providedUsersIds).doesNotContain(31L, 45L)
    }

    @Test
    fun shouldRetryProcessIfThereAreNoRandomUsersToReturnAndReturnEmptyAfterSecondAttempt() {
        val requestingUserIdentifier = "1"
        val idsOfUsersAlreadyVotedFor = setOf(10L, 20L, 30L, 40L)
        val expectedFilteredOutUserIds = emptySet<Long>()

        Mockito.`when`(userRepository.getMaxId()).thenReturn(250L)
        Mockito.`when`(voteService.findIdsOfUsersVotedFor(requestingUserIdentifier)).thenReturn(idsOfUsersAlreadyVotedFor)
        doAnswer { generateLongStreamOf(12L, 13L, 10L, 30L) }.`when`(random).longs(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())
        Mockito.`when`(userRepository.findUsersByIdIn(expectedFilteredOutUserIds)).thenReturn(emptyList())

        val providedUsers = userService.provideRandomUsers(GenderDisplayOption.MALE, requestingUserIdentifier)

        Mockito.verify(userRepository, times(2)).findUsersByIdIn(any())
        assertThat(providedUsers).isEmpty()
    }

    private fun generateLongStreamOf(vararg longs: Long): LongStream {
        return LongStream.of(*longs)
    }
}