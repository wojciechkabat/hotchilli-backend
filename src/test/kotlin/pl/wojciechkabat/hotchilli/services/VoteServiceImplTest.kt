package pl.wojciechkabat.hotchilli.services

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner
import pl.wojciechkabat.hotchilli.dtos.VoteDto
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.entities.Vote
import pl.wojciechkabat.hotchilli.repositories.VoteRepository
import java.time.LocalDate
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat as assertThat;

@RunWith(MockitoJUnitRunner::class)
class VoteServiceImplTest {
    @Mock
    lateinit var voteRepository: VoteRepository
    @InjectMocks
    lateinit var voteService: VoteServiceImpl
    @Captor
    lateinit var voteCaptor: ArgumentCaptor<Vote>

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
                        LocalDateTime.now() //fixme
                )
        )
    }

    private fun mockUserEntity(id: Long): User {
        return User(
                id,
                "someEmail@pl.pl",
                "someUserName",
                "somePassword",
                LocalDate.now(),
                ArrayList(),
                ArrayList()
        )
    }
}