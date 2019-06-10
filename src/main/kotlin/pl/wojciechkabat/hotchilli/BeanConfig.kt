package pl.wojciechkabat.hotchilli

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.Clock
import java.time.Clock.systemDefaultZone



@Configuration
class BeanConfig {
    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun clock(): Clock {
        return Clock.systemDefaultZone()
    }
}
