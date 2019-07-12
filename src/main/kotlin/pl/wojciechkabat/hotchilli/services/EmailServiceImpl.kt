package pl.wojciechkabat.hotchilli.services

import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.ITemplateEngine
import org.thymeleaf.context.Context
import pl.wojciechkabat.hotchilli.dtos.EmailContentDto
import pl.wojciechkabat.hotchilli.exceptions.EmailCreationException
import java.util.HashMap
import javax.mail.MessagingException
import javax.mail.internet.MimeMessage

@Service
class EmailServiceImpl(
        val javaMailSender: JavaMailSender,
        val templateEngine: ITemplateEngine,
        val translationService: TranslationService
) : EmailService {
    private val logger = LoggerFactory.getLogger(EmailService::class.java)
    private val pinConfirmationEmailTemplates = HashMap<String, String>()

    init {
        pinConfirmationEmailTemplates["pl"] = "pin-email-pl"
        pinConfirmationEmailTemplates["en"] = "pin-email-en"
    }

    @Async
    override fun sendAccountConfirmationEmail(email: String, languageCode: String, pin: String) {
        val context = Context()
        context.setVariable("pin", pin)

        val body = templateEngine.process(resolvePINEmailTemplateNameFor(languageCode.toLowerCase()), context)
        val emailContentDto = EmailContentDto(
                email,
                translationService.getTranslation("EMAIL_CONFIRMATION_PIN_TITLE", languageCode),
                body
        )
        logger.info("Sending account confirmation email to: $email")
        send(emailContentDto)
        logger.info("Email was successfully sent to: $email")
    }

    private fun send(emailContentDto: EmailContentDto) {
        val mimeMessage = prepareEmail(emailContentDto)
        javaMailSender.send(mimeMessage)
    }

    private fun prepareEmail(contentDto: EmailContentDto): MimeMessage {
        val mail = javaMailSender.createMimeMessage()
        try {
            val helper = MimeMessageHelper(mail, true)
            helper.setFrom("HotChilli<help@hotchilliapplication.com>")
            helper.setTo(contentDto.receiverAddress)
            helper.setSubject(contentDto.subject)
            helper.setText(contentDto.content, true)
            return mail
        } catch (e: MessagingException) {
            logger.error("Error while creating email content ", e)
            throw EmailCreationException()
        }

    }

    private fun resolvePINEmailTemplateNameFor(languageCode: String): String {
        return pinConfirmationEmailTemplates[languageCode] ?: pinConfirmationEmailTemplates["en"]!!
    }
}