package ru.otus.node;

import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.api.HelloRequest;
import ru.otus.api.HelloResponse;
import ru.otus.api.NodeGrpc;
import ru.otus.common.NodeInfo;

@Slf4j
@AllArgsConstructor
public class Node extends NodeGrpc.NodeImplBase {
    private final NodeInfo nodeInfo;

    @Override
    public void helloRequest(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        log.info("Node {} got the following hello request: {}",
                nodeInfo, request.getRequestId());

        HelloResponse response = HelloResponse.newBuilder()
                .setRequestId(request.getRequestId())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
