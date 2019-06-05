package pl.wojciechkabat.hotchilli.security.model;

import org.springframework.stereotype.Service;
import pl.wojciechkabat.hotchilli.entities.GuestUser;
import pl.wojciechkabat.hotchilli.entities.User;
import pl.wojciechkabat.hotchilli.security.exceptions.NoRefreshTokenFound;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository tokenRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public UUID save(RefreshToken refreshToken) {
        RefreshToken token = tokenRepository.save(refreshToken);
        return token.getId();
    }

    public RefreshToken findById(UUID id) {
        return tokenRepository.findById(id).orElseThrow(NoRefreshTokenFound::new);
    }

    @Transactional
    public void deleteByUserAndDeviceId(User user, String deviceId) {
        tokenRepository.deleteByUserAndDeviceId(user, deviceId);
        tokenRepository.flush();
    }

    @Transactional
    public void deleteByGuestUser(GuestUser guestUser) {
        tokenRepository.deleteByGuestUser(guestUser);
        tokenRepository.flush();
    }
}
