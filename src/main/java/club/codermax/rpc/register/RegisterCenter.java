package club.codermax.rpc.register;

import club.codermax.rpc.framework.ServiceConsumer;
import club.codermax.rpc.framework.ServiceProvider;

import java.util.List;
import java.util.Map;

public interface RegisterCenter {
    /**
     * 服务端将服务注册到相应节点中
     */
    void registerProvider(final List<ServiceProvider> serviceList);

    /**
     * 客户端从注册中心获取信息
     */
    List<String> getService(String consumerName);


    List<String> getAllService();
}
