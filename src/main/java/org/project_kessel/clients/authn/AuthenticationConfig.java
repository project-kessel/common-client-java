package org.project_kessel.clients.authn;

public class AuthenticationConfig {
    private AuthMode authMode;

    public AuthMode mode() {
        return authMode;
    }

    public void setMode(AuthMode authMode) {
        this.authMode = authMode;
    }

    public enum AuthMode {
        DISABLED,
        OIDC_CLIENT_CREDENTIALS
    }
}
