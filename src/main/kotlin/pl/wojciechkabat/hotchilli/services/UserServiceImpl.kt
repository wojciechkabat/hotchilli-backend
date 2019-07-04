package pl.wojciechkabat.hotchilli.services

import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.dtos.UpdateUserDto
import pl.wojciechkabat.hotchilli.dtos.UserDto
import pl.wojciechkabat.hotchilli.dtos.VoteData
import pl.wojciechkabat.hotchilli.entities.GenderDisplayOption
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.repositories.UserRepository
import pl.wojciechkabat.hotchilli.security.exceptions.NoUserWithGivenEmailException
import pl.wojciechkabat.hotchilli.utils.PictureMapper
import java.util.stream.Collectors.toList as toList
import java.time.Period
import java.time.LocalDate
import java.util.*
import java.util.stream.Collectors.toSet
import javax.transaction.Transactional

@Service
class UserServiceImpl(val userRepository: UserRepository, val voteService: VoteService, val random: Random) : UserService {
    private final val RANDOM_USERS_BATCH_SIZE = 20L

    override fun provideRandomUsers(genderDisplayOption: GenderDisplayOption, requestingUserIdentifier: String): List<UserDto> {
        val idsToFetch = getRandomUserIdsNotVotedForBefore(requestingUserIdentifier)
        val randomUsers = userRepository.findUsersByIdIn(idsToFetch)

        val usersToReturn = randomUsers.stream()
                .filter{genderDisplayOption == GenderDisplayOption.ALL || it.gender.name == genderDisplayOption.name}
                .limit(RANDOM_USERS_BATCH_SIZE)
                .collect(toList())

        val voteDataForUsers: Map<Long, VoteData> = voteService.findVoteDataForUsers(
                usersToReturn.stream().map { it.id!! }.collect(toList())
        ).associateBy({ it.userId }, { it })

        return usersToReturn.stream()
                .map {
                    val voteData: VoteData = voteDataForUsers[it.id] ?: VoteData(it.id!!, 0.0, 0)
                    UserDto(
                            it.id,
                            it.username,
                            calculateAge(it.dateOfBirth),
                            it.dateOfBirth,
                            PictureMapper.mapToDto(it.pictures),
                            voteData.averageRating,
                            voteData.voteCount,
                            it.gender)
                }
                .collect(toList())
    }

    override fun getUserDataFor(user: User): UserDto {
        val voteDataForUser = voteService.findVoteDataForUser(user.id!!)
        return UserDto(
                user.id,
                user.username,
                calculateAge(user.dateOfBirth),
                user.dateOfBirth,
                PictureMapper.mapToDto(user.pictures),
                voteDataForUser.averageRating,
                voteDataForUser.voteCount,
                user.gender
        )
    }

    @Transactional
    override fun updateUserDataFor(user: User, updateUserDto: UpdateUserDto): UserDto {
        user.username = updateUserDto.username
        user.gender = updateUserDto.gender
        user.dateOfBirth = updateUserDto.dateOfBirth

        return getUserDataFor(user)
    }

    override fun findByEmail(email: String): User {
        return userRepository.findByEmail(email).orElseThrow(({ NoUserWithGivenEmailException() }))
    }

    private fun calculateAge(birthDate: LocalDate): Int {
        return Period.between(birthDate, LocalDate.now()).years
    }

    private fun getRandomUserIdsNotVotedForBefore(requestingUserIdentifier: String): Set<Long> {
        val maxUserId = userRepository.getMaxId()
        var excludedIds = voteService.findIdsOfUsersVotedFor(requestingUserIdentifier)

        val activeUserId = requestingUserIdentifier.toLongOrNull()
        if(activeUserId != null) { //if is logged in
            excludedIds = excludedIds.plus(activeUserId)
        }

        return random.longs(200L + excludedIds.size, 1L, maxUserId + 1)
                .distinct()
                .boxed()
                .filter {!excludedIds.contains(it)}
                .limit(100)
                .collect(toSet())
    }
}