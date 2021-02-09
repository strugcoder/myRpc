package club.codermax.rpc.register;

import club.codermax.rpc.framework.ServiceProvider;

import java.util.List;
import java.util.Map;

public interface RegisterCenter4Provider {


    /**
     * 服务端将服务注册到相应节点中
     */
    void registerProvider(final List<ServiceProvider> serviceList);


    /**
     * 服务端获取服务提供者信息
     */
    Map<String, List<ServiceProvider>> getProviderService();
}
