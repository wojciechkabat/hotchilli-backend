package pl.wojciechkabat.hotchilli.controllers

import org.springframework.web.bind.annotation.*
import pl.wojciechkabat.hotchilli.dtos.VoteDto
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.services.VotingService

@RestController
@CrossOrigin
class VotingController(
        val votingService: VotingService
) {
    @PostMapping("/voting")
    fun postVote(@RequestBody voteDto: VoteDto) {
        val currentUser = User(1L, "currentUser", 23, ArrayList())
        votingService.persistVote(voteDto, currentUser)
    }

}