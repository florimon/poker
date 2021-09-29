package org.zkoss.zkspringboot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zkoss.zk.au.http.DHtmlResourceServlet;
import org.zkoss.zk.au.http.DHtmlUpdateServlet;
import org.zkoss.zk.ui.http.DHtmlLayoutServlet;
import org.zkoss.zk.ui.http.HttpSessionListener;
import org.zkoss.zk.ui.http.RichletFilter;
import org.zkoss.zk.ui.http.WebManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.util.Arrays;
import java.util.Collections;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;

/**
 * This is an adaptation, the original is at
 * https://github.com/zkoss/zkspringboot/blob/master/zkspringboot-autoconfig/src/main/java/org/zkoss/zkspringboot/ZkWebMvcConfiguration.java
 *
 */
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
    public ServletRegistrationBean<DHtmlLayoutServlet> dHtmlLayoutServlet() {
        final String[] mappings = {"*.zul", "*.zhtml"};
        ServletRegistrationBean<DHtmlLayoutServlet> servletRegistrationBean = new ServletRegistrationBean<>(new DHtmlLayoutServlet(), mappings);
        servletRegistrationBean.setInitParameters(singletonMap("update-uri", zkProperties.getUpdateUri()));
        if (zkProperties.getResourceUri() != null) {
            servletRegistrationBean.setInitParameters(singletonMap("resource-uri", zkProperties.getResourceUri()));
        }
        servletRegistrationBean.setLoadOnStartup(0);
        log.info("ZK-Springboot: ServletRegistrationBean for DHtmlLayoutServlet with url pattern {}", asList(mappings));
        return servletRegistrationBean;
    }

    @Bean
    @ConditionalOnProperty(prefix = "zk", name = "resource-uri")
    public ServletRegistrationBean<DHtmlResourceServlet> dHtmlResourceServlet() {
        final String resourceUri = zkProperties.getResourceUri();
        log.info("ZK-Springboot: ServletRegistrationBean for DHtmlResourceServlet with path {}", resourceUri);
        return new ServletRegistrationBean<>(new DHtmlResourceServlet(), resourceUri + "/*");
    }

    @Bean
    @ConditionalOnProperty(prefix = "zk", name = "richlet-filter-mapping")
    public FilterRegistrationBean<RichletFilter> richletFilter() {
        FilterRegistrationBean<RichletFilter> filterRegistrationBean = new FilterRegistrationBean<>(new RichletFilter());
        final String richletFilterMapping = zkProperties.getRichletFilterMapping();
        filterRegistrationBean.addUrlPatterns(richletFilterMapping);
        log.info("ZK-Springboot: FilterRegistrationBean for RichletFilter with url pattern {}", richletFilterMapping);
        return filterRegistrationBean;
    }

    @Bean
    @ConditionalOnMissingClass("org.zkoss.zats.mimic.Zats")     // Only allow custom update URI outside Zats testcases.
    public ServletRegistrationBean<DHtmlUpdateServlet> customizableDHtmlUpdateServlet() {
        final String updateUri = zkProperties.getUpdateUri();
        log.info("ZK-Springboot: ServletRegistrationBean for DHtmlUpdateServlet with path {}", updateUri);
        return new ServletRegistrationBean<>(new DHtmlUpdateServlet(), updateUri + "/*");
    }

    @Bean
    @ConditionalOnClass(name = "org.zkoss.zats.mimic.Zats") // Zats doesn't support custom update URI.
    public ServletRegistrationBean<DHtmlUpdateServlet> defaultDHtmlUpdateServlet() {
        return new ServletRegistrationBean<>(new DHtmlUpdateServlet(), "/zkau/*");
    }

    /**
     * With Zats the listener needs to be configured in web.xml.(custom update URI isn't supported by Zats anyway).
     * Zats runs with its own embedded Jetty.
     */
    @Bean
    @ConditionalOnMissingClass("org.zkoss.zats.mimic.Zats")     // Obsolete when using Zats
    public HttpSessionListener httpSessionListener() {
        if (zkProperties.isWar()) {
            return new HttpSessionListener();
        } else {
            return new CustomHttpSessionListener(zkProperties.getUpdateUri(), zkProperties.getResourceUri());
        }
    }

    @RequiredArgsConstructor
    private class CustomHttpSessionListener extends HttpSessionListener {
        private final String updateUri;
        private final String resourceUri;
        private WebManager webManager;

        @Override
        public void contextInitialized(ServletContextEvent servletContextEvent) {
            webManager = getWebManager(servletContextEvent.getServletContext());
        }

        private WebManager getWebManager(ServletContext servletContext) {
            if (WebManager.getWebManagerIfAny(servletContext) == null) {
                if (zkProperties.getResourceUri() == null) {
                    return new WebManager(servletContext, updateUri);
                } else {
                    return new WebManager(servletContext, updateUri, resourceUri);
                }
            } else {
                throw new IllegalStateException("ZK WebManager already exists. Could not initialize via Spring Boot configuration.");
            }
        }

        @Override
        public void contextDestroyed(ServletContextEvent servletContextEvent) {
            if (webManager != null) {
                webManager.destroy();
            }
        }
    }
}
