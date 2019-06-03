package pl.wojciechkabat.hotchilli.security.common;

public enum TokenScopes {
    REFRESH_TOKEN;

    public String authority() {
        return "ROLE_" + this.name();
    }
}
