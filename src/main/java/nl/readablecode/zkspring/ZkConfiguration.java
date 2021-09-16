package nl.readablecode.zkspring;

import nl.readablecode.zkspring.scopes.ZkScopesConfigurer;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
public class ZkConfiguration {

    @Bean
    public static CustomScopeConfigurer zkScopesConfigurer() {
        return new ZkScopesConfigurer();
    }

    @Bean
    public SpringBeanLocator springBeanLocator(ApplicationContext applicationContext) {
        return new SpringBeanLocator(applicationContext);
    }
}
