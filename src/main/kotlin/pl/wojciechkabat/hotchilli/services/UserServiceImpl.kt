package pl.wojciechkabat.hotchilli.services

import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.dtos.UserDto
import pl.wojciechkabat.hotchilli.repositories.UserRepository
import pl.wojciechkabat.hotchilli.utils.PictureMapper
import java.util.stream.Collectors.toList as toList

@Service
class UserServiceImpl(val userRepository: UserRepository, val voteService: VoteService) : UserService {
    override fun provideRandomUsers(number: Int): List<UserDto> {
        val randomUsers = userRepository.findRandomUsers(20)
        return randomUsers.stream()
                .map {
                    val voteData = voteService.findVoteDataForUser(it.id!!)
                    UserDto(
                            it.id,
                            it.username,
                            it.age,
                            PictureMapper.mapToDto(it.pictures),
                            voteData.averageRating ?: 0.0,
                            voteData.voteCount)
                }
                .collect(toList())
    }
}