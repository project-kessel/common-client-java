package org.project_kessel.clients.test;

import io.grpc.Channel;
import org.project_kessel.api.common.v1.KesselTestServiceGrpc;
import org.project_kessel.api.common.v1.TestRequest;
import org.project_kessel.api.common.v1.TestResponse;
import org.project_kessel.clients.ChannelManager;
import org.project_kessel.clients.KesselClient;
import org.project_kessel.clients.KesselClientsManager;
import org.project_kessel.clients.authn.AuthenticationConfig;

public class TestClientsManager extends KesselClientsManager {
    private static final String CHANNEL_MANAGER_KEY = TestClientsManager.class.getName();

    protected TestClientsManager(Channel channel) {
        super(channel);
    }

    public static TestClientsManager forInsecureClients(String targetUrl) {
        return new TestClientsManager(ChannelManager.getInstance(CHANNEL_MANAGER_KEY).forInsecureClients(targetUrl));
    }

    public static TestClientsManager forInsecureClients(String targetUrl, AuthenticationConfig authnConfig)
            throws RuntimeException {
        return new TestClientsManager(
                ChannelManager.getInstance(CHANNEL_MANAGER_KEY).forInsecureClients(targetUrl, authnConfig));
    }

    public static TestClientsManager forSecureClients(String targetUrl) {
        return new TestClientsManager(ChannelManager.getInstance(CHANNEL_MANAGER_KEY).forSecureClients(targetUrl));
    }

    public static TestClientsManager forSecureClients(String targetUrl, AuthenticationConfig authnConfig) {
        return new TestClientsManager(
                ChannelManager.getInstance(CHANNEL_MANAGER_KEY).forSecureClients(targetUrl, authnConfig));
    }

    public static void shutdownAll() {
        ChannelManager.getInstance(CHANNEL_MANAGER_KEY).shutdownAll();
    }

    public static void shutdownManager(TestClientsManager managerToShutdown) {
        ChannelManager.getInstance(CHANNEL_MANAGER_KEY).shutdownChannel(managerToShutdown.channel);
    }

    public TestClient getTestClient() {
        return new TestClient(channel);
    }

    public static class TestClient extends KesselClient<KesselTestServiceGrpc.KesselTestServiceStub,
            KesselTestServiceGrpc.KesselTestServiceBlockingStub> {
        protected TestClient(KesselTestServiceGrpc.KesselTestServiceStub asyncStub,
                             KesselTestServiceGrpc.KesselTestServiceBlockingStub blockingStub) {
            super(asyncStub, blockingStub);
        }

        TestClient(Channel channel) {
            super(KesselTestServiceGrpc.newStub(channel), KesselTestServiceGrpc.newBlockingStub(channel));
        }

        public TestResponse doRpcOne(TestRequest request) {
            return blockingStub.doRpcOne(request);
        }
    }

}
