package club.codermax.rpc.spring;

import club.codermax.rpc.framework.Configuration;
import club.codermax.rpc.framework.ServiceProvider;
import club.codermax.rpc.register.RegisterCenter4Provider;
import club.codermax.rpc.register.redis.RedisCenter;
import club.codermax.rpc.register.redis.RedisRegisterCenter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ProviderBeanDefinitionParser implements BeanDefinitionParser {

    private final Class<?> beanClass;

    public ProviderBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String interfaces = element.getAttribute("interf");
        String impl = element.getAttribute("impl");
        int port = Configuration.getInstance().getPort();
        InetAddress addr = null;

        try {
            addr = InetAddress.getLocalHost();
            String ip = addr.getHostAddress();

            if (port == 0) {
                port = 32115;
            }
            List<ServiceProvider> providerList = new ArrayList<>();
            ServiceProvider providerService = new ServiceProvider();
            providerService.setProvider(Class.forName(interfaces));
            providerService.setServiceObject(impl);
            providerService.setIp(ip);
            providerService.setPort(port);
            providerService.setTimeout(5000);
            providerService.setServiceMethod(null);
            providerService.setApplicationName("");
            providerService.setGroupName("nettyrpc");
            providerList.add(providerService);

            // 注册到数据中心
            RedisCenter redisCenter = RedisCenter.getInstance();
            redisCenter.registerProvider(providerList);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
