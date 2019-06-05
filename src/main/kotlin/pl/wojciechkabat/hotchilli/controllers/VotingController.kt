package pl.wojciechkabat.hotchilli.controllers

import org.springframework.web.bind.annotation.*
import pl.wojciechkabat.hotchilli.dtos.VoteDto
import pl.wojciechkabat.hotchilli.services.SecurityService
import pl.wojciechkabat.hotchilli.services.VoteService
import java.security.Principal

@RestController
@CrossOrigin
class VotingController(
        val voteService: VoteService,
        val securityService: SecurityService
) {
    @PostMapping("/voting")
    fun postVote(@RequestBody voteDto: VoteDto, principal: Principal) {
        if(!securityService.isGuest(principal)) {
            val activeUser = securityService.retrieveActiveUser(principal)
            voteService.persistVote(voteDto, activeUser)
        } else {
            val activeUser = securityService.retrieveGuestUser(principal)
            voteService.persistVoteAsGuest(voteDto, activeUser)
        }
    }

}