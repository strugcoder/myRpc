package club.codermax.rpc.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Proxy;

public class ProxyFactory<T> implements FactoryBean<T> {

    private Class<?> interfaceClass;
    private ApplicationContext ctx;

    public ProxyFactory(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public T getObject() throws Exception {
        // 之后会在Handler中进行代理对应的类，并增强目标类的其功能，然后在通过远程代理（动态代理，采用netty网络）
        // 通过远程代理将网络通信、编解码等细节隐藏起来
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new Handler(interfaceClass));
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }
}
