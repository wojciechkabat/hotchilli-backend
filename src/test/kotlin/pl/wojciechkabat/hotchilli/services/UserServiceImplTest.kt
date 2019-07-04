package pl.wojciechkabat.hotchilli.services

import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.junit.MockitoJUnitRunner
import pl.wojciechkabat.hotchilli.entities.Gender
import pl.wojciechkabat.hotchilli.entities.GenderDisplayOption
import pl.wojciechkabat.hotchilli.entities.Picture
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.repositories.UserRepository
import java.time.LocalDate
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
        Mockito.`when`(random.longs(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(LongStream.of(12L, 13L, 10L, 30L))

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
        Mockito.`when`(random.longs(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(LongStream.of(12L, 1L))

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
        Mockito.`when`(random.longs(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(LongStream.of(12L, 10L, 30L, 31L, 45L))
        Mockito.`when`(userRepository.findUsersByIdIn(expectedFilteredOutUserIds)).thenReturn(
                listOf(
                        User(12L, "email", "username", "password", LocalDate.now(), ArrayList(), ArrayList(), Gender.MALE),
                        User(31L, "email", "username", "password", LocalDate.now(), ArrayList(), ArrayList(), Gender.FEMALE),
                        User(45L, "email", "username", "password", LocalDate.now(), ArrayList(), ArrayList(), Gender.FEMALE)
                )
        )

        val providedUsers = userService.provideRandomUsers(GenderDisplayOption.MALE, requestingUserIdentifier)
        val providedUsersIds = providedUsers.stream().map { it.id }.collect(Collectors.toList())
        assertThat(providedUsersIds).contains(12L)
        assertThat(providedUsersIds).doesNotContain(31L, 45L)
    }
}