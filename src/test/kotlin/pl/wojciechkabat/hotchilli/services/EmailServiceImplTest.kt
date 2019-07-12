package pl.wojciechkabat.hotchilli.services

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.mail.javamail.JavaMailSender
import org.thymeleaf.ITemplateEngine
import org.thymeleaf.context.IContext
import javax.mail.internet.MimeMessage

import org.assertj.core.api.Assertions.assertThat as assertThat

@RunWith(MockitoJUnitRunner::class)
class EmailServiceImplTest {
    @Mock
    lateinit var javaMailSender: JavaMailSender
    @Mock
    lateinit var  templateEngine: ITemplateEngine
    @Mock
    lateinit var  translationService: TranslationService

    @Captor
    lateinit var  templateNameCaptor: ArgumentCaptor<String>

    @InjectMocks
    lateinit var emailService: EmailServiceImpl

    @Test
    fun shouldUsePolishEmailTemplateIfLanguageIsPolish() {
        `when`<MimeMessage>(javaMailSender.createMimeMessage()).thenReturn(mock<MimeMessage>(MimeMessage::class.java))
        `when`<String>(templateEngine.process(anyString(), any<IContext>())).thenReturn("Some email content")
        `when`(translationService.getTranslation("EMAIL_CONFIRMATION_PIN_TITLE", "pl")).thenReturn("Polish title")
        emailService.sendAccountConfirmationEmail("someemail", "pl", "1234")

        verify<ITemplateEngine>(templateEngine, times(1)).process(templateNameCaptor.capture(), any<IContext>())
        assertThat(templateNameCaptor.value).isEqualTo("pin-email-pl")
    }

    @Test
    fun shouldUseEnglishEmailTemplateIfCountryHasNoOwnTranslation() {
        `when`<MimeMessage>(javaMailSender.createMimeMessage()).thenReturn(mock<MimeMessage>(MimeMessage::class.java))
        `when`<String>(templateEngine.process(anyString(), any<IContext>())).thenReturn("Some email content")
        `when`(translationService.getTranslation("EMAIL_CONFIRMATION_PIN_TITLE", "HU")).thenReturn("English title")
        emailService.sendAccountConfirmationEmail("someemail", "HU", "1234")

        verify<ITemplateEngine>(templateEngine, times(1)).process(templateNameCaptor.capture(), any<IContext>())
        assertThat(templateNameCaptor.value).isEqualTo("pin-email-en")
    }

    @Test
    fun shouldUseEnglishEmailTemplateIfLanguageIsEnglish() {
        `when`<MimeMessage>(javaMailSender.createMimeMessage()).thenReturn(mock<MimeMessage>(MimeMessage::class.java))
        `when`<String>(templateEngine.process(anyString(), any<IContext>())).thenReturn("Some email content")
        `when`(translationService.getTranslation("EMAIL_CONFIRMATION_PIN_TITLE", "en")).thenReturn("English title")
        emailService.sendAccountConfirmationEmail("someemail", "en", "1234")

        verify<ITemplateEngine>(templateEngine, times(1)).process(templateNameCaptor.capture(), any<IContext>())
        assertThat(templateNameCaptor.value).isEqualTo("pin-email-en")
    }
}