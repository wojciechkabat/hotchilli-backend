package pl.wojciechkabat.hotchilli.services

import pl.wojciechkabat.hotchilli.utils.facebookModels.FacebookUser

interface FacebookService {
    fun getCurrentFacebookUser(accessToken: String): FacebookUser
}