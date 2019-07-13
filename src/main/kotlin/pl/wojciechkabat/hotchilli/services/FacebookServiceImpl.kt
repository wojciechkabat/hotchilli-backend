package pl.wojciechkabat.hotchilli.services

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import pl.wojciechkabat.hotchilli.FacebookConstants.FACEBOOK_ME_BASE_URL
import pl.wojciechkabat.hotchilli.utils.facebookModels.FacebookUser

@Service
class FacebookServiceImpl(private val restTemplate: RestTemplate) : FacebookService {
    override fun getCurrentFacebookUser(accessToken: String): FacebookUser {
        val builder = UriComponentsBuilder.fromUriString(FACEBOOK_ME_BASE_URL)
                .queryParam("fields", "id, first_name, last_name, email, picture.width(250).height(250).as(picture), birthday, gender")
                .queryParam("access_token", accessToken)
        return restTemplate.getForObject(builder.build().encode().toUri(), FacebookUser::class.java)!!
    }
}