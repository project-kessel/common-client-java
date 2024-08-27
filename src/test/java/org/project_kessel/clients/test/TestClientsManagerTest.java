package org.project_kessel.clients.test;

import io.grpc.Metadata;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.project_kessel.api.common.v1.TestRequest;
import org.project_kessel.clients.authn.AuthenticationConfig;
import org.project_kessel.clients.authn.oidc.client.OIDCClientCredentialsAuthenticationConfig;
import org.project_kessel.clients.fake.GrpcServerSpy;

import java.util.Optional;

import static io.smallrye.common.constraint.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.project_kessel.clients.util.CertUtil.addTestCACertToTrustStore;
import static org.project_kessel.clients.util.CertUtil.removeTestCACertFromKeystore;

public class TestClientsManagerTest {
    private static final Metadata.Key<String> authorizationKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    @BeforeAll
    static void testSetup() {
        /* Make sure all client managers shutdown/removed before tests */
        TestClientsManager.shutdownAll();
        /* Add self-signed cert to keystore, trust manager and SSL context for TLS testing. */
        addTestCACertToTrustStore();
    }

    @AfterEach
    void testTeardown() {
        /* Make sure all client managers shutdown/removed after each test */
        TestClientsManager.shutdownAll();
    }

    @AfterAll
    static void removeTestSetup() {
        /* Remove self-signed cert */
        removeTestCACertFromKeystore();
    }

    @Test
    void testManagersHoldIntendedCredentialsInChannel() throws Exception {
        AuthenticationConfig authnConfig = dummyAuthConfigWithGoodOIDCClientCredentials();
        var manager = TestClientsManager.forInsecureClients("localhost:7000");
        var manager2 = TestClientsManager.forInsecureClients("localhost:7001", authnConfig);
        var manager3 = TestClientsManager.forSecureClients("localhost:7002");
        var manager4 = TestClientsManager.forSecureClients("localhost:7003", authnConfig);

        var testClient = manager.getTestClient();
        var testClient2 = manager2.getTestClient();
        var testClient3 = manager3.getTestClient();
        var testClient4 = manager4.getTestClient();

        var cd1 = GrpcServerSpy.runAgainstTemporaryServerWithDummyServices(7000, () -> testClient.doRpcOne(TestRequest.getDefaultInstance()));
        var cd2 = GrpcServerSpy.runAgainstTemporaryServerWithDummyServices(7001, () -> testClient2.doRpcOne(TestRequest.getDefaultInstance()));
        var cd3 = GrpcServerSpy.runAgainstTemporaryTlsServerWithDummyServices(7002, () -> testClient3.doRpcOne(TestRequest.getDefaultInstance()));
        var cd4 = GrpcServerSpy.runAgainstTemporaryTlsServerWithDummyServices(7003, () -> testClient4.doRpcOne(TestRequest.getDefaultInstance()));

        assertNull(cd1.getMetadata().get(authorizationKey));
        assertEquals("NONE", cd1.getCall().getSecurityLevel().toString());

        assertNotNull(cd2.getMetadata().get(authorizationKey));
        assertEquals("NONE", cd2.getCall().getSecurityLevel().toString());

        assertNull(cd3.getMetadata().get(authorizationKey));
        assertEquals("PRIVACY_AND_INTEGRITY", cd3.getCall().getSecurityLevel().toString());

        assertNotNull(cd4.getMetadata().get(authorizationKey));
        assertEquals("PRIVACY_AND_INTEGRITY", cd4.getCall().getSecurityLevel().toString());
    }

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
}
