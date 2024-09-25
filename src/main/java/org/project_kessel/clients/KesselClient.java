package org.project_kessel.clients;

import io.grpc.stub.AbstractAsyncStub;
import io.grpc.stub.AbstractBlockingStub;

public abstract class KesselClient<A extends AbstractAsyncStub<A>, B extends AbstractBlockingStub<B>> {
    protected A asyncStub;
    protected B blockingStub;

    /***
     * No args constructor to support synthetic creation of no-args constructor in CDI in impls for normal scope bean
     * proxying. Supports producers (@Produces) of @ApplicationScoped beans in containers like Quarkus.
     * (https://github.com/quarkusio/quarkus/issues/22669#issuecomment-1006147659)
     */
    protected KesselClient() {

    }

    protected KesselClient(A asyncStub, B blockingStub) {
        this.asyncStub = asyncStub;
        this.blockingStub = blockingStub;
    }


}
