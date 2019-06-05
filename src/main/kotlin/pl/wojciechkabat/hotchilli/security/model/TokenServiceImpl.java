package pl.wojciechkabat.hotchilli.security.model;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.wojciechkabat.hotchilli.entities.GuestUser;
import pl.wojciechkabat.hotchilli.entities.User;
import pl.wojciechkabat.hotchilli.exceptions.NoGuestUserAssociatedToDeviceIdException;
import pl.wojciechkabat.hotchilli.repositories.GuestUserRepository;
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
    private final GuestUserRepository guestUserRepository;

    public TokenServiceImpl(JwtTokenFactory tokenFactory, RefreshTokenService refreshTokenService, UserRepository userRepository, GuestUserRepository guestUserRepository) {
        this.tokenFactory = tokenFactory;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.guestUserRepository = guestUserRepository;
    }

    public Map<String, String> getTokens(Authentication auth, boolean isGuest) {
        UserSecurityContext userSecurityContext = UserSecurityContext.create(auth.getName(), auth.getAuthorities());
        AccessJwtToken accessToken = tokenFactory.createAccessJwtToken(userSecurityContext);

        UUID refreshTokenId = getRefreshToken(auth, userSecurityContext, isGuest);

        SecurityContextHolder.getContext().setAuthentication(auth);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken.getToken());
        tokenMap.put("refreshTokenId", refreshTokenId.toString());
        return tokenMap;
    }

    private UUID getRefreshToken(Authentication auth, UserSecurityContext userSecurityContext, boolean isGuest) {
        if (!isGuest) {
            final User user = userRepository.findByEmail(auth.getName()).orElseThrow(NoUserWithGivenEmailException::new);
            refreshTokenService.deleteByUserAndDeviceId(user, (String) auth.getDetails());
            return saveAndGetIdOfRefreshTokenForUser(user, (String) auth.getDetails(), userSecurityContext);
        } else {
            GuestUser guestUser = guestUserRepository.findByDeviceId(auth.getName()).orElseThrow(NoGuestUserAssociatedToDeviceIdException::new);
            refreshTokenService.deleteByGuestUser(guestUser);
            return saveAndGetIdOfRefreshTokenForGuest(guestUser, userSecurityContext);
        }

    }

    private UUID saveAndGetIdOfRefreshTokenForUser(User user, String deviceId, UserSecurityContext userSecurityContext) {
        JwtToken refreshToken = tokenFactory.createRefreshToken(userSecurityContext);
        RefreshToken token = RefreshToken.anRefreshToken()
                .id(UUID.randomUUID())
                .refreshToken(refreshToken.getToken())
                .user(user)
                .deviceId(deviceId)
                .build();
        return refreshTokenService.save(token);
    }

    private UUID saveAndGetIdOfRefreshTokenForGuest(GuestUser guestUser, UserSecurityContext userSecurityContext) {
        JwtToken refreshToken = tokenFactory.createRefreshToken(userSecurityContext);
        RefreshToken token = RefreshToken.anRefreshToken()
                .id(UUID.randomUUID())
                .refreshToken(refreshToken.getToken())
                .guestUser(guestUser)
                .deviceId(guestUser.getDeviceId())
                .build();
        return refreshTokenService.save(token);
    }
}
