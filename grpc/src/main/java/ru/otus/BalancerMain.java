package ru.otus;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import ru.otus.balancer.LoadBalancer;

import java.io.IOException;

@Slf4j
public class BalancerMain {
    public static void main(String[] args) throws InterruptedException, IOException {
        int port = Integer.parseInt(System.getProperty("server.port", "8080"));

        Server server = ServerBuilder.forPort(port)
                .addService(new LoadBalancer())
                .build();
        server.start();

        log.info("GRPC server started");
        server.awaitTermination();
    }
}
