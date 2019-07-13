package pl.wojciechkabat.hotchilli.services

interface TranslationService {
    fun getTranslation(key: String, languageCode: String): String
}