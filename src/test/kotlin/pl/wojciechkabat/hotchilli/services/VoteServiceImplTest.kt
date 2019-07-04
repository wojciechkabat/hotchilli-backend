package pl.wojciechkabat.hotchilli.services

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner
import pl.wojciechkabat.hotchilli.dtos.GuestVoteDto
import pl.wojciechkabat.hotchilli.dtos.VoteData
import pl.wojciechkabat.hotchilli.dtos.VoteDto
import pl.wojciechkabat.hotchilli.entities.Gender
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.entities.Vote
import pl.wojciechkabat.hotchilli.exceptions.GuestVoteLimitExceededException
import pl.wojciechkabat.hotchilli.repositories.VoteRepository
import java.time.*
import java.util.*
import org.assertj.core.api.Assertions.assertThat as assertThat;


@RunWith(MockitoJUnitRunner::class)
class VoteServiceImplTest {
    @Mock
    lateinit var voteRepository: VoteRepository
    @Mock
    lateinit var clock: Clock
    @InjectMocks
    lateinit var voteService: VoteServiceImpl
    @Captor
    lateinit var voteCaptor: ArgumentCaptor<Vote>

    private val LOCAL_DATE_TIME = LocalDateTime.of(1989, 1, 13, 12, 10, 12)

    @Before
    fun setUp() {
        val fixedClock = Clock.fixed(LOCAL_DATE_TIME.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"))
        Mockito.`when`(clock.instant()).thenReturn(fixedClock.instant())
        Mockito.`when`(clock.zone).thenReturn(fixedClock.zone)
    }

    @Test
    fun shouldPersistVoteForLoggedInUser() {
        voteService.persistVote(
                VoteDto(12L, 2.6),
                mockUserEntity(1L)
        )

        Mockito.verify(voteRepository, times(1)).save(voteCaptor.capture())

        assertThat(voteCaptor.value).isEqualTo(
                Vote(
                        null,
                        "1",
                        12L,
                        2.6,
                        LOCAL_DATE_TIME
                )
        )
    }

    @Test
    fun shouldPersistVoteForGuestUser() {
        voteService.persistGuestVote(
                GuestVoteDto(12L, 2.6, "someDeviceId")
        )

        Mockito.verify(voteRepository, times(1)).save(voteCaptor.capture())

        assertThat(voteCaptor.value).isEqualTo(
                Vote(
                        null,
                        "someDeviceId",
                        12L,
                        2.6,
                        LOCAL_DATE_TIME
                )
        )
    }

    @Test(expected = GuestVoteLimitExceededException::class)
    fun shouldThrowExceptionWhenGuestLimitReached() {
        Mockito.`when`(voteRepository.findCountOfUserVotesAfterDate("someDeviceId", LocalDate.now().atStartOfDay())).thenReturn(20)

        voteService.persistGuestVote(
                GuestVoteDto(12L, 2.6, "someDeviceId")
        )
    }

    @Test
    fun shouldReturnEmptyVoteDataIfNoVotesWereGivenToUser() {
        Mockito.`when`(voteRepository.findVoteDataByUserId(120L)).thenReturn(Optional.empty())
        val voteData = voteService.findVoteDataForUser(120L)
        assertThat(voteData).isEqualTo(VoteData(120L, 0.0, 0))
    }

    private fun mockUserEntity(id: Long): User {
        return User(
                id,
                "someEmail@pl.pl",
                "someUserName",
                "somePassword",
                LocalDate.now(),
                ArrayList(),
                ArrayList(),
                Gender.MALE,
                LocalDateTime.now()
        )
    }
}