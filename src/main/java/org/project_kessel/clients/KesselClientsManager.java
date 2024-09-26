package org.project_kessel.clients;

import io.grpc.Channel;

public abstract class KesselClientsManager {
    protected Channel channel;

    /***
     * No args constructor to support synthetic creation of no-args constructor in CDI in impls for normal scope bean
     * proxying. Supports producers (@Produces) of @ApplicationScoped beans in containers like Quarkus.
     * (https://github.com/quarkusio/quarkus/issues/22669#issuecomment-1006147659)
     */
    protected KesselClientsManager() {

    }

    protected KesselClientsManager(Channel channel) {
        this.channel = channel;
    }
}
