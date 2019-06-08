package pl.wojciechkabat.hotchilli.services

import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.dtos.UserDto
import pl.wojciechkabat.hotchilli.dtos.VoteData
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.repositories.UserRepository
import pl.wojciechkabat.hotchilli.security.exceptions.NoUserWithGivenEmailException
import pl.wojciechkabat.hotchilli.utils.PictureMapper
import java.util.stream.Collectors.toList as toList
import java.time.Period
import java.time.LocalDate



@Service
class UserServiceImpl(val userRepository: UserRepository, val voteService: VoteService) : UserService {
    override fun provideRandomUsers(number: Int): List<UserDto> {
        val randomUsers = userRepository.findRandomUsers(number)
        val voteDataForUsers: Map<Long, VoteData> = voteService.findVoteDataForUsers(randomUsers.stream().map { it.id!! }.collect(toList()))
                .associateBy({ it.userId }, { it })

        return randomUsers.stream()
                .map {
                    val voteData: VoteData = voteDataForUsers[it.id] ?: VoteData(it.id!!, 0.0, 0)
                    UserDto(
                            it.id,
                            it.username,
                            calculateAge(it.dateOfBirth),
                            PictureMapper.mapToDto(it.pictures),
                            voteData.averageRating,
                            voteData.voteCount)
                }
                .collect(toList())
    }

    override fun getUserDataFor(activeUser: User): UserDto {
        val voteDataForUser = voteService.findVoteDataForUser(activeUser.id!!);
        return UserDto(
                activeUser.id,
                activeUser.username,
                calculateAge(activeUser.dateOfBirth),
                PictureMapper.mapToDto(activeUser.pictures),
                voteDataForUser.averageRating,
                voteDataForUser.voteCount
        )
    }

    override fun findByEmail(email: String): User {
        return userRepository.findByEmail(email).orElseThrow(({ NoUserWithGivenEmailException() }))
    }

    fun calculateAge(birthDate: LocalDate): Int {
        return Period.between(birthDate, LocalDate.now()).years
    }
}