package org.zkoss.zkspringboot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.zkoss.web.util.resource.ClassWebResource;

/**
 * Configures a view resolver such that for instance "sample" maps to "/zul/sample.zul".
 * This is an adapation, the original is at
 * https://github.com/zkoss/zkspringboot/blob/master/zkspringboot-autoconfig/src/main/java/org/zkoss/zkspringboot/ZkWebMvcConfiguration.java
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({ZkProperties.class})
public class ZkWebMvcConfiguration implements WebMvcConfigurer {
    private final ZkProperties zkProperties;

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        if (zkProperties.isZulViewResolverEnabled()) {
            String prefix = getPrefix();
            final String suffix = zkProperties.getZulViewResolverSuffix();
            log.info("ZK-Springboot: InternalViewResolver enabled - e.g. resolving view 'example' to '{}example{}'", prefix, suffix);
            InternalResourceViewResolver resolver = new InternalResourceViewResolver(prefix, suffix);
            resolver.setOrder(Ordered.LOWEST_PRECEDENCE);
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