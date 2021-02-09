package club.codermax.rpc.register;

import club.codermax.rpc.framework.ServiceConsumer;
import club.codermax.rpc.framework.ServiceProvider;

import java.util.List;
import java.util.Map;

public interface RegisterCenter4Consumer {

    /**
     * 消息端初始化提供者信息加入本地缓存
     */
    void initProviderMap();

    /**
     * 消息端获取服务提供者信息
     */
    List<ServiceProvider> getServiceMetaDataMap4Consumer(String serviceKey);
}
