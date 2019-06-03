package pl.wojciechkabat.hotchilli.security.token;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("classpath:/security-config.properties")
public class JwtSettings {
    @Value("${jwt.security.tokenExpirationTime}")
    private int tokenExpirationTime;
    @Value("${jwt.security.refreshTokenExpTime}")
    private int refreshTokenExpTime;
    @Value("${jwt.security.tokenSigningKey}")
    private String tokenSigningKey;
    @Value("${jwt.security.tokenSigningKeyEncoded}")
    private String tokenSigningKeyEncoded;
}