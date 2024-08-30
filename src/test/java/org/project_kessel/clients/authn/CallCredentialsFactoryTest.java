package org.project_kessel.clients.authn;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.project_kessel.clients.authn.oidc.client.OIDCClientCredentialsAuthenticationConfig;

public class CallCredentialsFactoryTest {
    public static OIDCClientCredentialsAuthenticationConfig dummyAuthConfigWithGoodOIDCClientCredentials() {
        var oidcClientCredentialsConfig = new OIDCClientCredentialsAuthenticationConfig.OIDCClientCredentialsConfig();
        oidcClientCredentialsConfig.setIssuer("http://localhost:8090");
        oidcClientCredentialsConfig.setClientId("test");
        oidcClientCredentialsConfig.setClientSecret("test");
        oidcClientCredentialsConfig.setScope(Optional.empty());
        oidcClientCredentialsConfig.setOidcClientCredentialsMinterImplementation(Optional.empty());

        var authnConfig = new OIDCClientCredentialsAuthenticationConfig();
        authnConfig.setMode(AuthenticationConfig.AuthMode.OIDC_CLIENT_CREDENTIALS);
        authnConfig.setCredentialsConfig(oidcClientCredentialsConfig);

        return authnConfig;
    }

    @Test
    void testCreateOIDCClientCallCredentials() {
        AuthenticationConfig authnConfig = dummyAuthConfigWithGoodOIDCClientCredentials();
        try {
            CallCredentialsFactory.create(authnConfig);
        } catch (CallCredentialsFactory.CallCredentialsCreationException e) {
            fail("CallCredentialsFactory creation for OIDC client "
                    + "should not throw an exception when OIDC client config is good.");
        }
    }

    @Test
    void testFailToCreateCallCredentialsWhenAuthnConfigEmpty() {
        AuthenticationConfig authnConfig = null;
        try {
            CallCredentialsFactory.create(authnConfig);
            fail("CallCredentialsFactory creation for OIDC client "
                    + "should throw an exception when OIDC client config is empty.");
        } catch (CallCredentialsFactory.CallCredentialsCreationException e) {
            // empty catch block
        }
    }

    @Test
    void testFailToCreateCallCredentialsForOIDCWhenConfigEmpty() {
        var authnConfig = new OIDCClientCredentialsAuthenticationConfig();
        authnConfig.setMode(AuthenticationConfig.AuthMode.OIDC_CLIENT_CREDENTIALS);
        try {
            CallCredentialsFactory.create(authnConfig);
            fail("CallCredentialsFactory creation for OIDC client "
                    + "should throw an exception when OIDC client config is empty.");
        } catch (CallCredentialsFactory.CallCredentialsCreationException e) {
            // empty catch block
        }
    }
}
