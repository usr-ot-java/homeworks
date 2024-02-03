package ru.otus.balancer;

import lombok.Getter;
import ru.otus.common.NodeInfo;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LoadBalancerMeta {
    @Getter
    private final List<NodeInfo> nodes;
    @Getter
    private final Set<NodeInfo> nodesSet;
    private final ThreadLocal<Integer> counter = ThreadLocal.withInitial(() -> -1);

    public LoadBalancerMeta() {
        this.nodes = Collections.emptyList();
        this.nodesSet = Collections.emptySet();
    }

    public LoadBalancerMeta(List<NodeInfo> nodes) {
        this.nodes = Collections.unmodifiableList(nodes);
        this.nodesSet = nodes.stream().collect(Collectors.toUnmodifiableSet());
    }

    public NodeInfo nextNode() {
        if (nodes.isEmpty()) {
            return null;
        }

        int val = counter.get() + 1;
        if (val >= nodes.size()) {
            counter.set(0);
            val = 0;
        } else {
            counter.set(val);
        }
        return nodes.get(val);
    }

    public boolean containsNode(NodeInfo nodeInfo) {
        return nodesSet.contains(nodeInfo);
    }
}
