package ru.otus;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import ru.otus.api.HelloRequest;
import ru.otus.api.HelloResponse;
import ru.otus.api.LoadBalancerGrpc;

import java.util.Objects;
import java.util.UUID;

@Slf4j
public class SimpleClientMain {
    public static void main(String[] args) {
        String balancerAddr = Objects.requireNonNull(System.getProperty("balancer.host"));
        int balancerPort = Integer.parseInt(Objects.requireNonNull(System.getProperty("balancer.port")));

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(balancerAddr, balancerPort)
                .usePlaintext()
                .build();

        try {
            LoadBalancerGrpc.LoadBalancerBlockingStub balancerBlockingStub = LoadBalancerGrpc.newBlockingStub(channel);
            String requestId = UUID.randomUUID().toString();
            HelloRequest helloRequest = HelloRequest.newBuilder().setRequestId(requestId).build();

            log.info("Sending helloRequest: {}", helloRequest);
            HelloResponse helloResponse = balancerBlockingStub.helloRequest(helloRequest);
            log.info("Successfully got answer: {}", helloResponse);
        } finally {
            channel.shutdown();
        }
    }
}
