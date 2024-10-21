package org.project_kessel.clients;

import io.grpc.stub.AbstractAsyncStub;
import io.grpc.stub.AbstractBlockingStub;

import java.util.concurrent.TimeUnit;

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
        /*
         * Add default grpc deadline if none is specified.
         */
        final int DEFAULT_GRPC_DEADLINE_MS = 500;

        if (asyncStub.getCallOptions().getDeadline() == null) {
            this.asyncStub = asyncStub.withDeadlineAfter(DEFAULT_GRPC_DEADLINE_MS, TimeUnit.MILLISECONDS);
        } else {
            this.asyncStub = asyncStub;
        }

        if (blockingStub.getCallOptions().getDeadline() == null) {
            this.blockingStub = blockingStub.withDeadlineAfter(DEFAULT_GRPC_DEADLINE_MS, TimeUnit.MILLISECONDS);
        } else {
            this.blockingStub = blockingStub;
        }
    }


}
