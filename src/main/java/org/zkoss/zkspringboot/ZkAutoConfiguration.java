package org.zkoss.zkspringboot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zkoss.zk.au.http.DHtmlResourceServlet;
import org.zkoss.zk.ui.http.DHtmlLayoutServlet;
import org.zkoss.zk.ui.http.RichletFilter;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Configuration
@EnableConfigurationProperties({ZkProperties.class})
public class ZkAutoConfiguration {

    private final ZkProperties zkProperties;

    public ZkAutoConfiguration(ZkProperties zkProperties) {
        this.zkProperties = zkProperties;
    }

    // original zk layout servlet (only for war files)
    @Bean
    @ConditionalOnProperty(prefix = "zk", name = "springboot-packaging", havingValue = "war")
    public ServletRegistrationBean dHtmlLayoutServlet() {
        final String[] mappings = {"*.zul", "*.zhtml"};
        ServletRegistrationBean reg = new ServletRegistrationBean(new DHtmlLayoutServlet(), mappings);
        reg.setInitParameters(Collections.singletonMap("update-uri", zkProperties.getUpdateUri()));
        if (zkProperties.getResourceUri() != null) {
            reg.setInitParameters(Collections.singletonMap("resource-uri", zkProperties.getResourceUri()));
        }
        reg.setLoadOnStartup(0);
        log.info("ZK-Springboot: ServletRegistrationBean for DHtmlLayoutServlet with url pattern " + Arrays.asList(mappings));
        return reg;
    }

    @Bean
    @ConditionalOnProperty(prefix = "zk", name = "richlet-filter-mapping")
    public FilterRegistrationBean richletFilter() {
        final String richletFilterMapping = zkProperties.getRichletFilterMapping();
        FilterRegistrationBean reg = new FilterRegistrationBean(new RichletFilter());
        reg.addUrlPatterns(richletFilterMapping);
        log.info("ZK-Springboot: FilterRegistrationBean for RichletFilter with url pattern " + richletFilterMapping);
        return reg;
    }

    @Bean
    @ConditionalOnProperty(prefix = "zk", name = "resource-uri")
    public ServletRegistrationBean dHtmlResourceServlet() {
        final String resourceUri = zkProperties.getResourceUri();
        log.info("ZK-Springboot: ServletRegistrationBean for DHtmlResourceServlet with path " + resourceUri);
        return new ServletRegistrationBean(new DHtmlResourceServlet(), resourceUri + "/*");
    }
}
