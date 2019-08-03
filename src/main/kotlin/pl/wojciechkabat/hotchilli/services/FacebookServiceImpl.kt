package pl.wojciechkabat.hotchilli.services

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import pl.wojciechkabat.hotchilli.FacebookConstants.*
import pl.wojciechkabat.hotchilli.utils.facebookModels.FacebookAlbum
import pl.wojciechkabat.hotchilli.utils.facebookModels.FacebookPhoto
import pl.wojciechkabat.hotchilli.utils.facebookModels.FacebookUser
import pl.wojciechkabat.hotchilli.utils.facebookModels.responseModels.FacebookAlbumsResponse
import pl.wojciechkabat.hotchilli.utils.facebookModels.responseModels.FacebookPhotosResponse

@Service
class FacebookServiceImpl(private val restTemplate: RestTemplate) : FacebookService {
    override fun getCurrentUserFacebookPhotos(accessToken: String): List<FacebookPhoto> {
        val photoAlbums = getPhotoAlbums(accessToken)
        val profilePicAlbum = photoAlbums.stream().filter { "profile" == it.type }.findFirst()
        var profilePictures: List<FacebookPhoto> = emptyList()
        if(profilePicAlbum.isPresent) {
            profilePictures = getPhotosFromAlbum(accessToken, profilePicAlbum.get())
        }
        val facebookPhotos = getFacebookPhotos(accessToken)
        return profilePictures + facebookPhotos
    }

    override fun getCurrentFacebookUser(accessToken: String): FacebookUser {
        val builder = UriComponentsBuilder.fromUriString(FACEBOOK_ME_BASE_URL)
                .queryParam("fields", "id, first_name, last_name, email, picture.width(250).height(250).as(picture), birthday, gender")
                .queryParam("locale", "en_GB")
                .queryParam("access_token", accessToken)
        return restTemplate.getForObject(builder.build().encode().toUri(), FacebookUser::class.java)!!
    }

    private fun getFacebookPhotos(accessToken: String): List<FacebookPhoto> {
        val builder = UriComponentsBuilder.fromUriString(FACEBOOK_PHOTOS_LINK)
                .queryParam("fields", "id, source")
                .queryParam("locale", "en_GB")
                .queryParam("limit", "50")
                .queryParam("access_token", accessToken)
        return restTemplate.getForObject(builder.build().encode().toUri(), FacebookPhotosResponse::class.java)!!.facebookPhotos
    }

    private fun getPhotoAlbums(accessToken: String): List<FacebookAlbum> {
        val builder = UriComponentsBuilder.fromUriString(FACEBOOK_ALBUMS_LINK)
                .queryParam("fields", "id, name, type")
                .queryParam("locale", "en_GB")
                .queryParam("limit", "50")
                .queryParam("access_token", accessToken)
        return restTemplate.getForObject(builder.build().encode().toUri(), FacebookAlbumsResponse::class.java)!!.facebookAlbums
    }

    private fun getPhotosFromAlbum(accessToken: String, album: FacebookAlbum): List<FacebookPhoto> {
        val builder = UriComponentsBuilder.fromUriString("$FACEBOOK_BASE_URL/${album.id}/photos")
                .queryParam("fields", "id, source")
                .queryParam("locale", "en_GB")
                .queryParam("limit", "50")
                .queryParam("access_token", accessToken)
        return restTemplate.getForObject(builder.build().encode().toUri(), FacebookPhotosResponse::class.java)!!.facebookPhotos
    }
}