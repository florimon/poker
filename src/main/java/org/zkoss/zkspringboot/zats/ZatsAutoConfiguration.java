package org.zkoss.zkspringboot.zats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zkoss.zk.au.http.DHtmlUpdateServlet;
import org.zkoss.zk.ui.http.HttpSessionListener;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zkspringboot.ZkProperties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

@Slf4j
@Configuration
@EnableConfigurationProperties({ZkProperties.class})
public class ZatsAutoConfiguration {

    private final ZkProperties zkProperties;

    public ZatsAutoConfiguration(ZkProperties zkProperties) {
        this.zkProperties = zkProperties;
    }

    @Bean
    @ConditionalOnClass(name = "org.zkoss.zats.mimic.Zats") //Zats doesn't support custom update URI.
    public ServletRegistrationBean defaultDHtmlUpdateServlet() {
        return new ServletRegistrationBean(new DHtmlUpdateServlet(), "/zkau/*");
    }

    @Bean
    @ConditionalOnMissingClass("org.zkoss.zats.mimic.Zats") //only allow custom update URI outside Zats testcases.
    public ServletRegistrationBean customizableDHtmlUpdateServlet() {
        final String updateUri = zkProperties.getUpdateUri();
        log.info("ZK-Springboot: ServletRegistrationBean for DHtmlUpdateServlet with path " + updateUri);
        return new ServletRegistrationBean(new DHtmlUpdateServlet(), updateUri + "/*");
    }

    /**
     * With Zats the listener needs to be configured in web.xml.(custom update URI isn't supported by Zats anyway).
     * Zats runs with its own embedded Jetty.
     */
    @Bean
    @ConditionalOnMissingClass("org.zkoss.zats.mimic.Zats") //Obsolete when using Zats
    public HttpSessionListener httpSessionListener() {
        if (zkProperties.isWar()) {
            return new HttpSessionListener();
        }
        return new HttpSessionListener() {
            private WebManager webManager;

            @Override
            public void contextInitialized(ServletContextEvent sce) {
                final ServletContext ctx = sce.getServletContext();
                if (WebManager.getWebManagerIfAny(ctx) == null) {
                    if(zkProperties.getResourceUri() == null) {
                        webManager = new WebManager(ctx, zkProperties.getUpdateUri());
                    } else {
                        webManager = new WebManager(ctx, zkProperties.getUpdateUri(), zkProperties.getResourceUri());
                    }
                } else {
                    throw new IllegalStateException("ZK WebManager already exists. Could not initialize via Spring Boot configuration.");
                }
            }

            @Override
            public void contextDestroyed(ServletContextEvent sce) {
                if (webManager != null) {
                    webManager.destroy();
                }
            }
        };
    }
}
