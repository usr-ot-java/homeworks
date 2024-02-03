package ru.otus.balancer;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import ru.otus.api.*;
import ru.otus.common.NodeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class LoadBalancer extends LoadBalancerGrpc.LoadBalancerImplBase {
    private final ThreadLocal<HashMap<NodeInfo, NodeGrpc.NodeFutureStub>> nodeClients = ThreadLocal.withInitial(HashMap::new);
    private final Lock updateMetaLock = new ReentrantLock();
    private AtomicReference<LoadBalancerMeta> atomicRefMeta = new AtomicReference<>(new LoadBalancerMeta());


    @Override
    public void registerNode(RegisterNodeRequest request,
                                          StreamObserver<RegisterNodeReply> responseObserver) {
        updateMetaLock.lock();

        try {
            LoadBalancerMeta oldMeta = atomicRefMeta.get();
            List<NodeInfo> newNodeList = new ArrayList<>(oldMeta.getNodes());
            String nodeAddr = request.getAddress();
            int nodePort = request.getPort();
            NodeInfo newNode = new NodeInfo(nodeAddr, nodePort);

            if (!oldMeta.containsNode(newNode)) {
                newNodeList.add(new NodeInfo(nodeAddr, nodePort));
                atomicRefMeta = new AtomicReference<>(new LoadBalancerMeta(newNodeList));
                log.info("Registered new node: {}:{}", nodeAddr, nodePort);
            } else {
                log.warn("Node {}:{} is already present in the meta, no need to register again", nodeAddr, nodePort);
            }
        } finally {
            updateMetaLock.unlock();
        }

        RegisterNodeReply reply = RegisterNodeReply.newBuilder().setStatus(true).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void helloRequest(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        LoadBalancerMeta loadBalancerMeta = atomicRefMeta.get();
        NodeInfo node = loadBalancerMeta.nextNode();
        if (node == null) {
            log.warn("No nodes registered to accept the helloRequest: {}", request);
            responseObserver.onError(new RuntimeException("Cannot balance since no registered nodes"));
            return;
        }

        log.info("Sending helloRequest to node {}", node);
        NodeGrpc.NodeFutureStub client = nodeClients.get().computeIfAbsent(node, this::createNodeChannel);

        try {
            HelloResponse helloResponse = client.helloRequest(request).get();
            log.info("Got successfully helloResponse from node {}", node);
            responseObserver.onNext(helloResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Node {} failed to respond to helloRequest:", node, e);
            responseObserver.onError(new RuntimeException(
                    String.format("Node %s failed to respond:", node)
            ));
            deregisterNode(node);
        }
    }

    private void deregisterNode(NodeInfo node) {
        updateMetaLock.lock();

        try {
            LoadBalancerMeta currentMeta = atomicRefMeta.get();
            if (currentMeta.containsNode(node)) {
                List<NodeInfo> updatedNodesList = currentMeta.getNodes()
                        .stream()
                        .filter(e -> !e.equals(node))
                        .toList();
                LoadBalancerMeta loadBalancerMeta = new LoadBalancerMeta(updatedNodesList);
                atomicRefMeta.set(loadBalancerMeta);
                log.info("Node {} is deregistered", node.toString());
            }
        } finally {
            updateMetaLock.unlock();
        }
    }

    private NodeGrpc.NodeFutureStub createNodeChannel(NodeInfo nodeInfo) {
        ManagedChannel channel = ManagedChannelBuilder.
                forAddress(nodeInfo.getAddr(), nodeInfo.getPort())
                .usePlaintext()
                .build();
        return NodeGrpc.newFutureStub(channel);
    }
}
