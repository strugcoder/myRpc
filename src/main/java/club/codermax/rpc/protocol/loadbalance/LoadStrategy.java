package club.codermax.rpc.protocol.loadbalance;


import club.codermax.rpc.framework.ServiceProvider;

import java.util.List;

/**
 * 负载均衡算法接口
 */
public interface LoadStrategy {
    String select(List<String> providers);
}
