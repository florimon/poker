package nl.readablecode.zk;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZkConfiguration {

    @Bean
    public CustomScopeConfigurer zkScopesConfigurer() {
        return new ZkScopesConfigurer();
    }

    @Bean
    public ZkSpringUtil zkSpringUtil(ApplicationContext applicationContext) {
        return new ZkSpringUtil(applicationContext);
    }
}
