package pl.wojciechkabat.hotchilli.security.token;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import pl.wojciechkabat.hotchilli.security.exceptions.JwtExpiredTokenException;

public class RawAccessJwtToken implements JwtToken {
    private static Logger logger = LoggerFactory.getLogger(RawAccessJwtToken.class);

    private String token;

    public RawAccessJwtToken(String token) {
        this.token = token;
    }

    public Jws<Claims> parseClaims(String signingKey) {
        try {
            return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(this.token);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
            logger.error("Invalid JWT Token");
            throw new BadCredentialsException("Invalid JWT token");
        } catch (ExpiredJwtException expiredEx) {
            logger.info("JWT Token is expired");
            throw new JwtExpiredTokenException();
        }
    }

    @Override
    public String getToken() {
        return token;
    }
}
