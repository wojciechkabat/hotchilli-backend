package pl.wojciechkabat.hotchilli.security.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.wojciechkabat.hotchilli.security.common.TokenScopes;
import pl.wojciechkabat.hotchilli.security.common.UserSecurityContext;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtTokenFactory {
    private final JwtSettings jwtSettings;

    @Autowired
    public JwtTokenFactory(JwtSettings jwtSettings) {
        this.jwtSettings = jwtSettings;
    }

    public AccessJwtToken createAccessJwtToken(UserSecurityContext securityContext) {
        if (StringUtils.isBlank(securityContext.getUsername()))
            throw new IllegalArgumentException("Cannot create JWT Token without username");

        if (Collections.isEmpty(securityContext.getAuthorities()))
            throw new IllegalArgumentException("User doesn't have any privileges");

        Claims claims = Jwts.claims().setSubject(securityContext.getUsername());
        claims.put("scopes", securityContext.getAuthorities().stream().map(Object::toString).collect(Collectors.toList()));

        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(jwtSettings.getTokenExpirationTime());

        final String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, jwtSettings.getTokenSigningKeyEncoded())
                .compact();

        return new AccessJwtToken(token);
    }

    public JwtToken createRefreshToken(UserSecurityContext securityContext) {
        if (StringUtils.isBlank(securityContext.getUsername())) {
            throw new IllegalArgumentException("Cannot create JWT Token without username");
        }

        Claims claims = Jwts.claims().setSubject(securityContext.getUsername());
        claims.put("scopes", java.util.Collections.singletonList(TokenScopes.REFRESH_TOKEN.authority()));

        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(jwtSettings.getRefreshTokenExpTime());

        final String token = Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, jwtSettings.getTokenSigningKeyEncoded())
                .compact();

        return new AccessJwtToken(token);
    }
}
