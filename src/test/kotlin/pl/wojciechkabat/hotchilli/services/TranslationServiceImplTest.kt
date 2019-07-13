package pl.wojciechkabat.hotchilli.services

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

import org.assertj.core.api.Assertions.assertThat as assertThat

@RunWith(MockitoJUnitRunner::class)
class TranslationServiceImplTest {
    @InjectMocks
    lateinit var translationService: TranslationServiceImpl

    @Test
    fun shouldRetrieveEnglishTranslation() {
        val translationGreeting = translationService.getTranslation("greeting", "en")
        val translationGoodbye = translationService.getTranslation("goodbye", "en")
        assertThat(translationGreeting).isEqualTo("Hello Test")
        assertThat(translationGoodbye).isEqualTo("Bye Test")
    }

    @Test
    fun shouldRetrievePolishTranslation() {
        val translationGreeting = translationService.getTranslation("greeting", "pl")
        val translationGoodbye = translationService.getTranslation("goodbye", "pl")
        assertThat(translationGreeting).isEqualTo("Czesc Test")
        assertThat(translationGoodbye).isEqualTo("Na razie Test")
    }

    @Test
    fun shouldGiveEnglishTranslationIfNoLanguageTranslation() {
        val translationGreeting = translationService.getTranslation("greeting", "de")
        assertThat(translationGreeting).isEqualTo("Hello Test")
    }
}