package club.codermax.rpc.spring;

import club.codermax.rpc.framework.Configuration;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class RegisterBeanDefinitionParser implements BeanDefinitionParser {

    private final Class<?> beanClass;

    public RegisterBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        Configuration.getInstance().setAddress(element.getAttribute("address"));
        return null;
    }
}
