package ru.otus;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import ru.otus.api.LoadBalancerGrpc;
import ru.otus.api.RegisterNodeReply;
import ru.otus.api.RegisterNodeRequest;
import ru.otus.common.NodeInfo;
import ru.otus.node.Node;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class NodeMain {
    public static void main(String[] args) throws InterruptedException, IOException {
        String selfHost = System.getProperty("node.host", "127.0.0.1");
        int selfPort = Integer.parseInt(Objects.requireNonNull(System.getProperty("node.port")));
        NodeInfo selfNode = new NodeInfo(selfHost, selfPort);

        Server server = ServerBuilder.forPort(selfPort)
                .addService(new Node(selfNode))
                .build();

        String balancerAddr = Objects.requireNonNull(System.getProperty("balancer.host"));
        int balancerPort = Integer.parseInt(Objects.requireNonNull(System.getProperty("balancer.port")));

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(balancerAddr, balancerPort)
                .usePlaintext()
                .build();

        try {
            LoadBalancerGrpc.LoadBalancerBlockingStub balancerBlockingStub = LoadBalancerGrpc.newBlockingStub(channel);
            RegisterNodeReply response = balancerBlockingStub.registerNode(RegisterNodeRequest.newBuilder()
                    .setAddress(selfNode.getAddr())
                    .setPort(selfNode.getPort())
                    .build());

            if (!response.getStatus()) {
                throw new RuntimeException("Failed to register the current node");
            }
            log.info("The node is successfully registered by the balancer");
        } finally {
            channel.shutdown();
        }

        server.start();
        log.info("The server is started {}", selfNode);
        server.awaitTermination();
    }
}
