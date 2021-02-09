package club.codermax.rpc.register.redis;

import club.codermax.rpc.framework.ServiceProvider;
import club.codermax.rpc.register.RegisterCenter4Consumer;
import club.codermax.rpc.register.RegisterCenter4Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RedisRegisterCenter implements RegisterCenter4Provider, RegisterCenter4Consumer {

    private static final Logger logger = LoggerFactory.getLogger(RedisRegisterCenter.class);


    // 服务提供者列表
    private static final Map<String, List<ServiceProvider>> providerServiceMap = new ConcurrentHashMap<>();
    // 服务端元信息
    private static final Map<String, List<ServiceProvider>> serviceData4Consumer = new ConcurrentHashMap<>();

    private final static String ROOT = "rpc:";
    private final static String PROVIDER = "provider:";


    private RedisRegisterCenter() {
    }

    private static RedisRegisterCenter redisRegisterCenter = new RedisRegisterCenter();

    public static RedisRegisterCenter getInstance() {
        return redisRegisterCenter;
    }

    private RedisClient redisClient = null;

    @Override
    public void initProviderMap() {
        if (serviceData4Consumer.isEmpty()) {
            serviceData4Consumer.putAll(fetchOrUpdateServiceMetaData());
        }
    }

    @Override
    public List<ServiceProvider> getServiceMetaDataMap4Consumer(String serviceKey) {
        if (serviceKey == null || serviceKey == "") {
            return null;
        }

        // 连接redis
        synchronized (RedisRegisterCenter.class) {
            if (redisClient == null) {
                redisClient = new RedisClient();
            }
        }
        return serviceData4Consumer.get(serviceKey);
    }


    @Override
    public void registerProvider(List<ServiceProvider> seriviceList) {
        if (seriviceList == null || seriviceList.size() == 0) {
            return;
        }
        // 连接redis，注册服务，加锁，将所有需要注册的服务放到providerServiceMap里面
        synchronized (RedisRegisterCenter.class) {
            for (ServiceProvider provider : seriviceList) {
                //获取接口名称
                String serviceItfKey = provider.getProvider().getName();
                //先从当前服务提供者的集合里面获取
                List<ServiceProvider> providers = providerServiceMap.get(serviceItfKey);
                if (providers == null) {
                    providers = new ArrayList<>();
                }
                providers.add(provider);
                providerServiceMap.put(serviceItfKey, providers);
            }
            if (redisClient == null) {
                redisClient = new RedisClient();
            }

            for (Map.Entry<String, List<ServiceProvider>> entry : providerServiceMap.entrySet()) {
                // 这个set是对应的接口
                String serviceNode = entry.getKey();
                String redisKey = ROOT + PROVIDER + serviceNode;

                //创建当前服务器节点，这里是注册时使用，一个接口对应的ServiceProvider 只有一个
                int serverPort = entry.getValue().get(0).getPort();
                InetAddress addr = null;
                try {
                    addr = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                String ip = addr.getHostAddress();
                String impl = (String) entry.getValue().get(0).getServiceObject();

                String redisValue = ip + "|" + serverPort + "|" + impl;

                redisClient.add2Set(redisKey, redisValue);

            }
        }
    }

    @Override
    public Map<String, List<ServiceProvider>> getProviderService() {
        return providerServiceMap;
    }

    private Map<String, List<ServiceProvider>> fetchOrUpdateServiceMetaData() {
        final Map<String, List<ServiceProvider>> providerServiceMap = new ConcurrentHashMap<>();
        // 连接redis
        synchronized (RedisRegisterCenter.class) {
            if (redisClient == null) {
                redisClient = new RedisClient();
            }
        }

        String providePath = ROOT + PROVIDER;
        logger.info("redis 连接成功，服务提供者前缀：" + providePath);
        Set<String> providerServices = redisClient.getValues(providePath);
        System.out.println("服务提供的所有服务：" + providerServices.toString());

        return providerServiceMap;
    }
}
