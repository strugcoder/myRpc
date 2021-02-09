package club.codermax.rpc.spring;

import club.codermax.rpc.framework.Configuration;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


/**
 * NamespaceHandler会根据schema和节点名找到某个BeanDefinitionParser，然后由 BeanDefinitionParser完成具体的解析工作
 */
public class RpcNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("procotol", new ProcotolBeanDefinitionParser(Configuration.class));
        registerBeanDefinitionParser("application", new ApplicationBeanDefinitionParser(Configuration.class));
        registerBeanDefinitionParser("provider", new ProviderBeanDefinitionParser(Configuration.class));
        registerBeanDefinitionParser("service", new ServiceBeanDefinitionParser(Configuration.class));
    }
}
