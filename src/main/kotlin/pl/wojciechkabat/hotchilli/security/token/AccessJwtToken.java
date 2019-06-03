package pl.wojciechkabat.hotchilli.security.token;

public final class AccessJwtToken implements JwtToken {
    private final String rawToken;

    protected AccessJwtToken(final String token) {
        this.rawToken = token;
    }

    public String getToken() {
        return this.rawToken;
    }

}
