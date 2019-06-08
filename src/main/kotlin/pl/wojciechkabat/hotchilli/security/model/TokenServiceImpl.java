package pl.wojciechkabat.hotchilli.security.model;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.wojciechkabat.hotchilli.entities.User;
import pl.wojciechkabat.hotchilli.repositories.UserRepository;
import pl.wojciechkabat.hotchilli.security.common.UserSecurityContext;
import pl.wojciechkabat.hotchilli.security.exceptions.NoUserWithGivenEmailException;
import pl.wojciechkabat.hotchilli.security.token.AccessJwtToken;
import pl.wojciechkabat.hotchilli.security.token.JwtToken;
import pl.wojciechkabat.hotchilli.security.token.JwtTokenFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {
    private final JwtTokenFactory tokenFactory;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public TokenServiceImpl(JwtTokenFactory tokenFactory, RefreshTokenService refreshTokenService, UserRepository userRepository) {
        this.tokenFactory = tokenFactory;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    public Map<String, String> getTokens(Authentication auth) {
        UserSecurityContext userSecurityContext = UserSecurityContext.create(auth.getName(), auth.getAuthorities());
        AccessJwtToken accessToken = tokenFactory.createAccessJwtToken(userSecurityContext);

        UUID refreshTokenId = getRefreshToken(auth, userSecurityContext);

        SecurityContextHolder.getContext().setAuthentication(auth);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken.getToken());
        tokenMap.put("refreshTokenId", refreshTokenId.toString());
        return tokenMap;
    }

    private UUID getRefreshToken(Authentication auth, UserSecurityContext userSecurityContext) {
        final User user = userRepository.findByEmail(auth.getName()).orElseThrow(NoUserWithGivenEmailException::new);
        removeOldRefreshToken((String) auth.getDetails(), user);
        return saveAndGetIdOfRefreshToken(user, (String) auth.getDetails(), userSecurityContext);
    }

    private void removeOldRefreshToken(String deviceId, User user) {
        refreshTokenService.deleteByUserAndDeviceId(user, deviceId);
    }

    private UUID saveAndGetIdOfRefreshToken(User user, String deviceId, UserSecurityContext userSecurityContext) {
        JwtToken refreshToken = tokenFactory.createRefreshToken(userSecurityContext);
        RefreshToken token = RefreshToken.anRefreshToken()
                .id(UUID.randomUUID())
                .refreshToken(refreshToken.getToken())
                .user(user)
                .deviceId(deviceId)
                .build();
        return refreshTokenService.save(token);
    }
}
