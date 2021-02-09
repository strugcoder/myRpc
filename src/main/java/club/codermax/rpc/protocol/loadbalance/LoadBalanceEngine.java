package club.codermax.rpc.protocol.loadbalance;

import club.codermax.rpc.protocol.loadbalance.impl.HashLoadStrategy;
import club.codermax.rpc.protocol.loadbalance.impl.PollLoadStrategy;
import club.codermax.rpc.protocol.loadbalance.impl.RandomLoadStrategy;

import java.util.HashMap;
import java.util.Map;


/**
 * 使用到的是策略模式
 * 将if-else分支语句优化掉，这更像是一种策略工厂类，使用Map；来缓存策略，
 * 根据指定的loadStrategy直接重缓存中获取对应的策略，从未避免if-else分支判断逻辑（类似算法中的查表法）
 *
 * 运行时动态确定
 */
public class LoadBalanceEngine {

    private static final Map<LoadBalanceEnum, LoadStrategy> loadBalanceMap = new HashMap<>();

    static {
        loadBalanceMap.put(LoadBalanceEnum.Random, new RandomLoadStrategy());
        loadBalanceMap.put(LoadBalanceEnum.Hash, new HashLoadStrategy());
        loadBalanceMap.put(LoadBalanceEnum.Polling, new PollLoadStrategy());
    }


    public static LoadStrategy queryLoadStrategy(String loadStrategy) {
        LoadBalanceEnum loadBalanceEnum = LoadBalanceEnum.queryByCode(loadStrategy);
        if (loadBalanceEnum == null) {
            // 默认选择随机算法
            return new RandomLoadStrategy();
        }
        return loadBalanceMap.get(loadBalanceEnum);
    }
}
