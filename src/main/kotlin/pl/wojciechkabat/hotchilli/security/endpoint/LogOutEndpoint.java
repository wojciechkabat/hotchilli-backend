package pl.wojciechkabat.hotchilli.security.endpoint;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.wojciechkabat.hotchilli.entities.User;
import pl.wojciechkabat.hotchilli.repositories.UserRepository;
import pl.wojciechkabat.hotchilli.security.exceptions.NoUserWithGivenEmailException;
import pl.wojciechkabat.hotchilli.security.model.RefreshTokenService;

import java.security.Principal;

@RestController
@RequestMapping(value = "/api/auth/log-out")
@CrossOrigin
public class LogOutEndpoint {
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public LogOutEndpoint(RefreshTokenService refreshTokenService, UserRepository userRepository) {
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/{deviceId}")
    public void logout(Principal principal, @PathVariable("deviceId") String deviceId) {
        final String userEmail = principal.getName();
        final User user = userRepository.findByEmail(userEmail).orElseThrow(NoUserWithGivenEmailException::new);
        refreshTokenService.deleteByUserAndDeviceId(user, deviceId);
        SecurityContextHolder.clearContext();
    }
}
