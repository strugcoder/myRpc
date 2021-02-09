package club.codermax.rpc.spring;

import club.codermax.rpc.framework.Configuration;
import club.codermax.rpc.framework.URL;
import club.codermax.rpc.protocol.Procotol;
import club.codermax.rpc.protocol.netty.NettyProcotol;
import club.codermax.rpc.protocol.netty.channelpool.NettyChannelPoolFactory;
import club.codermax.rpc.register.redis.RedisCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class ProcotolBeanDefinitionParser implements BeanDefinitionParser {

    private static final Logger logger = LoggerFactory.getLogger(ProcotolBeanDefinitionParser.class.getName());


    private final Class<?> beanClass;

    public ProcotolBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * @param element : 包装 <rpc:procotol />标签
     *        parserContext 持有一个readerContext，readerContext持有 register，也就持有BeanFactory
     */

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String pro = element.getAttribute("procotol");
        int port = Integer.parseInt(element.getAttribute("port"));
        Configuration.getInstance().setProcotol(pro);
        Configuration.getInstance().setPort(port);
        Configuration.getInstance().setSerialize(element.getAttribute("serialize"));
        Configuration.getInstance().setStragety(element.getAttribute("stragety"));
        Configuration.getInstance().setRole(element.getAttribute("role"));
        Configuration.getInstance().setAddress(element.getAttribute("address"));
        if ("provider".equals(element.getAttribute("role"))) { // 说明是服务提供者
            Procotol procotol = null;
            // 如果通信方式比较多可以使用工厂模式进行优化，或者单例与工厂模式的结合static{map...},这种方式类似策略模式
            if ("Netty".equalsIgnoreCase(pro)) {
                procotol = new NettyProcotol();
            } else if ("Http".equalsIgnoreCase(pro)) {
                logger.warn("还未实现 http");
            } else if ("Socket".equalsIgnoreCase(pro)) {
                logger.warn("还未实现 socket");
            } else {
                procotol = new NettyProcotol();
            }

            try {
                InetAddress addr = InetAddress.getLocalHost();
                String ip = addr.getHostAddress();
                if (port == 0) {
                    port = 32115;
                }
                URL url = new URL(ip, port);
                procotol.start(url);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else { // 消费者
            RedisCenter redisCenter = RedisCenter.getInstance();
            // 初始化Channel
            List<String> services = redisCenter.getAllService();

            for (int i = 0; i < services.size(); i++) {
                System.out.println(services.get(i));
            }
            if (services.isEmpty()) {
                throw new RuntimeException("service provider list is empty.");
            }
            long a1 = System.currentTimeMillis();

            NettyChannelPoolFactory.getInstance().initNettyChannelPoolFactory(services);

            logger.warn("ProcotolBeanDefinitionParser#initNettyChannelPoolFactory 耗时：" + (System.currentTimeMillis() - a1));
        }
        return null;
    }
}
