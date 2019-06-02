package pl.wojciechkabat.hotchilli.controllers

import org.springframework.web.bind.annotation.*
import pl.wojciechkabat.hotchilli.dtos.VoteDto
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.services.VoteService
import java.util.*
import kotlin.collections.ArrayList

@RestController
@CrossOrigin
class VotingController(
        val voteService: VoteService
) {
    @PostMapping("/voting")
    fun postVote(@RequestBody voteDto: VoteDto) {
        val currentUser = User(Random().nextLong(), "currentUser", 23, ArrayList())
        voteService.persistVote(voteDto, currentUser)
    }

}