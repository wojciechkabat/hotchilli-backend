package pl.wojciechkabat.hotchilli.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.wojciechkabat.hotchilli.entities.User;

import javax.persistence.*;
import java.util.UUID;

@Table(name = "refresh_tokens")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "anRefreshToken")
public class RefreshToken {
    @Id
    private UUID id;
    private String refreshToken;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String deviceId;
}
