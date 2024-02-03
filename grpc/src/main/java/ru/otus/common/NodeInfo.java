package ru.otus.common;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class NodeInfo {
    private final String addr;
    private final int port;

    @Override
    public String toString() {
        return addr + ":" + port;
    }
}
