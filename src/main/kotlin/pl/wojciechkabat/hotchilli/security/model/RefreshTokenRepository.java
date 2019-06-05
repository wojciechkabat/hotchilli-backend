package pl.wojciechkabat.hotchilli.security.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wojciechkabat.hotchilli.entities.GuestUser;
import pl.wojciechkabat.hotchilli.entities.User;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    void deleteByUserAndDeviceId(User user, String deviceId);
    void deleteByGuestUser(GuestUser guestUser);
}
