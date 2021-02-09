package club.codermax.rpc.protocol.loadbalance.impl;

import club.codermax.rpc.framework.ServiceProvider;
import club.codermax.rpc.protocol.loadbalance.LoadStrategy;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 轮询算法
 */
public class PollLoadStrategy implements LoadStrategy {


    // 轮询算法

    // 计数器
    private int index = 0;
    private Lock lock = new ReentrantLock();


    @Override
    public String select(List<String> providers) {
        String service = null;
        try {
            try {
                // 尝试拿锁
                lock.tryLock(10, TimeUnit.MILLISECONDS);

                // 若计数大于服务提供者个数，计数器归0
                if (index >= providers.size()) {
                    index = 0;
                }
                service = providers.get(index++);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } finally {
            lock.unlock();
        }


        // 兜底，为保证程序的健壮性，如果没有尝试获取到服务，则直接取第一个
        if (service == null) {
            service = providers.get(0);
        }
        return service;
    }
}
