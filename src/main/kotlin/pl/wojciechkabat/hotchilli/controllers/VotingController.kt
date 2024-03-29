package pl.wojciechkabat.hotchilli.controllers

import org.springframework.web.bind.annotation.*
import pl.wojciechkabat.hotchilli.dtos.VoteDto
import pl.wojciechkabat.hotchilli.dtos.GuestVoteDto
import pl.wojciechkabat.hotchilli.entities.Gender
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.entities.UserSettings
import pl.wojciechkabat.hotchilli.services.VoteService
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

@RestController
@CrossOrigin
class VotingController(
        val voteService: VoteService
) {
    @PostMapping("/voting")
    fun postVote(@RequestBody voteDto: VoteDto) {
        val currentUser = User(Random().nextLong(), "currentUser", null,"asdsad", "Adsa", LocalDate.now(), ArrayList(), ArrayList(), Gender.MALE, LocalDateTime.now(), true, UserSettings(1L, true, "en"))
        voteService.persistVote(voteDto, currentUser)
    }

    @PostMapping("guest/voting")
    fun postVoteGuest(@RequestBody guestVoteDto: GuestVoteDto) {
        voteService.persistGuestVote(guestVoteDto)
    }

}