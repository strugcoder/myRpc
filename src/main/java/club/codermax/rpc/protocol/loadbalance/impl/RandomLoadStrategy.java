package club.codermax.rpc.protocol.loadbalance.impl;

import club.codermax.rpc.framework.ServiceProvider;
import club.codermax.rpc.protocol.loadbalance.LoadStrategy;

import java.util.List;
import java.util.Random;

public class RandomLoadStrategy implements LoadStrategy {
    @Override
    public String select(List<String> providers) {
        int m = providers.size();
        Random r = new Random();
        int index = r.nextInt(m);
        return providers.get(index);
    }
}
