package pl.wojciechkabat.hotchilli.services

import org.springframework.stereotype.Service
import java.util.*

@Service
class TranslationServiceImpl : TranslationService {
    private val resourceBundleMap = HashMap<String, ResourceBundle>()

    init {
        resourceBundleMap["pl"] = ResourceBundle.getBundle("i18n.Notifications", Locale("pl"))
        resourceBundleMap["en"] = ResourceBundle.getBundle("i18n.Notifications", Locale("en"))

    }

    override fun getTranslation(key: String, languageCode: String): String {
        var resourceBundle: ResourceBundle? = resourceBundleMap[languageCode.toLowerCase()]
        if (resourceBundle == null) {
            resourceBundle = resourceBundleMap["en"]
        }
        return resourceBundle!!.getString(key)
    }
}