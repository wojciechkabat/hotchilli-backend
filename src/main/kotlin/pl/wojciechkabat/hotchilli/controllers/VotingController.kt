package pl.wojciechkabat.hotchilli.controllers

import org.springframework.web.bind.annotation.*
import pl.wojciechkabat.hotchilli.dtos.VoteDto
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.services.VoteService
import java.security.Principal
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

@RestController
@CrossOrigin
class VotingController(
        val voteService: VoteService
) {
    @PostMapping("/voting")
    fun postVote(@RequestBody voteDto: VoteDto, principal: Principal) {
        val currentUser = User(Random().nextLong(), "currentUser", "asdsad", LocalDate.now(), ArrayList(), ArrayList(), "Adsa")
        voteService.persistVote(voteDto, currentUser)
    }

}