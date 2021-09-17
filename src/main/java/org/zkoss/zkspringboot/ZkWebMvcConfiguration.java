package org.zkoss.zkspringboot;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.zkoss.web.util.resource.ClassWebResource;

@Slf4j
@Configuration
@EnableConfigurationProperties({ZkProperties.class})
public class ZkWebMvcConfiguration implements WebMvcConfigurer {

    private final ZkProperties zkProperties;

    public ZkWebMvcConfiguration(ZkProperties zkProperties) {
        this.zkProperties = zkProperties;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        if (zkProperties.isZulViewResolverEnabled()) {
            String prefix = getPrefix();
            final String suffix = zkProperties.getZulViewResolverSuffix();
            log.info("ZK-Springboot: InternalViewResolver enabled - e.g. resolving view 'example' to '{}example{}'", prefix, suffix);
            InternalResourceViewResolver resolver = new InternalResourceViewResolver(prefix, suffix);
            resolver.setOrder(InternalResourceViewResolver.LOWEST_PRECEDENCE);
            registry.viewResolver(resolver);
        }
    }

    private String getPrefix() {
        if (zkProperties.isWar()) {
            return zkProperties.getZulViewResolverPrefix() + "/";
        } else {
            return zkProperties.getUpdateUri() + ClassWebResource.PATH_PREFIX + zkProperties.getZulViewResolverPrefix() + "/";
        }
    }
}