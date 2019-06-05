package pl.wojciechkabat.hotchilli.security.model;

import pl.wojciechkabat.hotchilli.entities.User;

import java.util.UUID;

public interface RefreshTokenService {
    UUID save(RefreshToken refreshToken);
    RefreshToken findById(UUID id);
    void deleteByUserAndDeviceId(User user, String deviceId);
}
