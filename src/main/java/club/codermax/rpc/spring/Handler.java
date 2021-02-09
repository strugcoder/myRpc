package club.codermax.rpc.spring;

import club.codermax.rpc.framework.Configuration;
import club.codermax.rpc.framework.RpcRequest;
import club.codermax.rpc.framework.ServiceProvider;
import club.codermax.rpc.framework.URL;
import club.codermax.rpc.protocol.Procotol;
import club.codermax.rpc.protocol.loadbalance.LoadBalanceEngine;
import club.codermax.rpc.protocol.loadbalance.LoadStrategy;
import club.codermax.rpc.protocol.netty.NettyProcotol;
import club.codermax.rpc.register.RegisterCenter4Consumer;
import club.codermax.rpc.register.redis.RedisCenter;
import club.codermax.rpc.register.redis.RedisRegisterCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

public class Handler<T> implements InvocationHandler {

    private final static Logger logger = LoggerFactory.getLogger(Handler.class);

    private Class<T> interfaceClass;

    public Handler(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Configuration configuration = Configuration.getInstance();
        Procotol procotol = null;
        if ("Netty".equalsIgnoreCase(configuration.getProcotol())) {
            procotol = new NettyProcotol();
        } else if ("Http".equalsIgnoreCase(configuration.getProcotol())) {
            logger.warn("Http 未实现");
        } else if ("Socket".equalsIgnoreCase(configuration.getProcotol())) {
            logger.warn("socket 未实现");
        } else {
            procotol = new NettyProcotol();
        }

        // 服务接口名称
        String serviceKey = interfaceClass.getName();
        // 获取服务接口对应的服务提供者列表，因为一个接口可能对应放多个实现
        List<String> providerServices = RedisCenter.getInstance().getServiceProvide(serviceKey);

        // 根据负载策略，从服务提供者列表选取本次调用的服务提供者
        String stragety = configuration.getStragety();
        if (stragety == null || stragety.equals("")) {
            stragety = "random";
        }
        logger.info("选择负载均衡策略是：" + stragety);
        LoadStrategy loadStrategyService = LoadBalanceEngine.queryLoadStrategy(stragety);
        String[] serviceProvider = loadStrategyService.select(providerServices).split("\\|");
        URL url = new URL(serviceProvider[0], Integer.parseInt(serviceProvider[1]));
        String impl = serviceProvider[2];
        int timeout = 20000;
        RpcRequest invocation = new RpcRequest(UUID.randomUUID().toString(), interfaceClass.getName(), method.getName(), args, method.getParameterTypes(), impl, timeout);

        Object res = procotol.send(url, invocation);
        return res;
    }
}
