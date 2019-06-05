package pl.wojciechkabat.hotchilli.security.model;

import org.springframework.security.core.Authentication;

import java.util.Map;

public interface TokenService {
    Map<String, String> getTokens(Authentication auth);
}
