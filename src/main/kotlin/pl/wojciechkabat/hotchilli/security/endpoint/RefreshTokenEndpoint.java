package pl.wojciechkabat.hotchilli.security.endpoint;

import org.springframework.web.bind.annotation.*;
import pl.wojciechkabat.hotchilli.entities.GuestUser;
import pl.wojciechkabat.hotchilli.entities.Role;
import pl.wojciechkabat.hotchilli.entities.User;
import pl.wojciechkabat.hotchilli.security.common.RoleEnum;
import pl.wojciechkabat.hotchilli.security.common.UserSecurityContext;
import pl.wojciechkabat.hotchilli.security.exceptions.InvalidJwtTokenException;
import pl.wojciechkabat.hotchilli.security.exceptions.RefreshTokenExpiredException;
import pl.wojciechkabat.hotchilli.security.model.RefreshTokenService;
import pl.wojciechkabat.hotchilli.security.token.*;
import pl.wojciechkabat.hotchilli.services.GuestUserService;
import pl.wojciechkabat.hotchilli.services.UserService;
import pl.wojciechkabat.hotchilli.utils.EmailValidator;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/auth/token")
@CrossOrigin
public class RefreshTokenEndpoint {
    private final JwtTokenFactory tokenFactory;
    private final UserService userService;
    private final GuestUserService guestUserService;
    private final RefreshTokenService refreshTokenService;
    private final JwtSettings jwtSettings;

    public RefreshTokenEndpoint(JwtTokenFactory tokenFactory,
                                UserService userService,
                                GuestUserService guestUserService,
                                RefreshTokenService refreshTokenService,
                                JwtSettings jwtSettings) {
        this.tokenFactory = tokenFactory;
        this.userService = userService;
        this.guestUserService = guestUserService;
        this.refreshTokenService = refreshTokenService;
        this.jwtSettings = jwtSettings;
    }

    @GetMapping(value = "/{refreshTokenId}", produces = APPLICATION_JSON_VALUE)
    @Transactional
    public JwtToken refreshToken(@PathVariable("refreshTokenId") String refreshTokenId) {
        final String token = refreshTokenService.findById(UUID.fromString(refreshTokenId)).getRefreshToken();

        RawAccessJwtToken rawToken = new RawAccessJwtToken(token);
        RefreshToken refreshToken = RefreshToken
                .create(rawToken, jwtSettings.getTokenSigningKeyEncoded())
                .orElseThrow(InvalidJwtTokenException::new);

        validateRefreshTokenExpiration(refreshToken);

        final String subject = refreshToken.getSubject();
        UserSecurityContext userContext;

        if (EmailValidator.EmailValidator.validate(subject)) {
            final User user = userService.findByEmail(subject);
            userContext = UserSecurityContext.create(
                    user.getEmail(),
                    UserSecurityContext.convertUserRoles(user.getRoles().stream()
                            .map(Role::getValue)
                            .collect(toList()))
            );
        } else {
            //if it doesn't have a structure of an email it must be a guest user (device id)
            final GuestUser guestUser = guestUserService.findByDeviceId(subject);
            userContext = UserSecurityContext.create(
                    guestUser.getDeviceId(),
                    UserSecurityContext.convertUserRoles(Arrays.asList(RoleEnum.GUEST))
            );
        }

        return tokenFactory.createAccessJwtToken(userContext);
    }

    private void validateRefreshTokenExpiration(RefreshToken refreshToken) {
        long exp = Long.parseLong(String.valueOf(refreshToken.getClaims().getBody().get("exp")));
        long now = new Date().getTime() / 1000;

        if (now >= exp) {
            throw new RefreshTokenExpiredException();
        }
    }
}
