package pl.wojciechkabat.hotchilli.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "aLoginUserDto")
public class LoginUserDto implements Serializable {
    private String login; // needs to be called login instead of email because of JWT configuration
    private String password;
    private String deviceId;
}
