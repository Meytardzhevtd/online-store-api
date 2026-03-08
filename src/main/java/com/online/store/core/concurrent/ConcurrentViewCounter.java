package com.online.store.core.concurrent;

import java.util.concurrent.ConcurrentHashMap;

import lombok.*;

@Data
@AllArgsConstructor
class Node {
    private Long count;
    private Long lastAccess;
}

public class ConcurrentViewCounter {
    ConcurrentHashMap<Long, Node> map;
}
