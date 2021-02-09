package club.codermax.rpc.register.redis;

import club.codermax.rpc.framework.ServiceProvider;
import club.codermax.rpc.register.RegisterCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class RedisCenter implements RegisterCenter {


    private static final Logger logger = LoggerFactory.getLogger(RedisCenter.class);


    private static RedisCenter redisCenter = new RedisCenter();

    private RedisCenter() {
    }

    public static RedisCenter getInstance() {
        return redisCenter;
    }

    private final static String ROOT = "rpc:";
    private final static String PROVIDER = "provider:";


    @Override
    public void registerProvider(List<ServiceProvider> serviceList) {
        if (serviceList == null || serviceList.size() == 0) {
            return;
        }
        RedisClient redisClient = new RedisClient();

        // 连接redis，注册服务，加锁，
        synchronized (RedisCenter.class) {
            for (ServiceProvider provider : serviceList) {
                //获取接口名称
                String serviceName = provider.getProvider().getName();
                String redisKey = ROOT + PROVIDER + serviceName;

                String entry = provider.getProvider().getName();

                //创建当前服务器节点，这里是注册时使用，一个接口对应的ServiceProvider 只有一个
                int serverPort = provider.getPort();
                InetAddress addr = null;
                try {
                    addr = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                String ip = addr.getHostAddress();
                String impl = (String) provider.getServiceObject();

                String redisValue = ip + "|" + serverPort + "|" + impl;

                redisClient.add2Set(redisKey, redisValue);
            }
        }
    }

    @Override
    public List<String> getService(String consumerName) {
        if (consumerName == null) {
            return null;
        }
        RedisClient redisClient = new RedisClient();
        List<String> valueList = new ArrayList<>();
        // 连接redis，获取服务，加锁
        synchronized (RedisCenter.class) {

            String redisKey = ROOT + PROVIDER + consumerName;
            Set<String> key = redisClient.getValues(redisKey);
            Iterator<String> iterator = key.iterator();
            String[] values = new String[key.size()];
            for (int i = 0; i < key.size(); i++) {
                values[i] = iterator.next();
            }

            for (int i = 0; i < values.length; i++) {
                valueList.add(values[i]);
            }
        }
        return valueList;
    }

    @Override
    public List<String> getAllService() { // 模糊查询
        RedisClient redisClient = new RedisClient();
        Set<String> keyBring = redisClient.getKeyBring(ROOT + PROVIDER);
        System.out.println(keyBring.toString());
        Iterator<String> iterator = keyBring.iterator();
        List<String> res = new ArrayList<>();
        for (int i = 0; i < keyBring.size(); i++) {
            synchronized (RedisCenter.class) {
                String redisKey = iterator.next();
                Set<String> key = redisClient.getValues(redisKey);
                Iterator<String> keyIterator = key.iterator();
                String[] values = new String[key.size()];
                for (int j = 0; j < key.size(); j++) {
                    values[j] = keyIterator.next();
                }
                for (int j = 0; j < values.length; j++) {
                    res.add(values[j]);
                }
            }
        }
        return res;
    }
}
