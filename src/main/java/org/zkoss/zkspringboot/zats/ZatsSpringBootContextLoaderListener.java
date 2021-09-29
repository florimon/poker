package org.zkoss.zkspringboot.zats;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * Experimental ContextLoaderListener to load a spring boot application context using ZATS' embedded jetty.
 * This is an adaptation, the original is at
 * https://github.com/zkoss/zkspringboot/blob/master/zkspringboot-autoconfig/src/main/java/org/zkoss/zkspringboot/zats/ZatsSpringBootContextLoaderListener.java
 */
public class ZatsSpringBootContextLoaderListener extends ContextLoaderListener {
    public static final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        new ZatsSpringBootServletInitializer(sce.getServletContext().getInitParameter(CONTEXT_CONFIG_LOCATION))
                .onStartup(sce.getServletContext());
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ZatsSpringBootServletInitializer extends SpringBootServletInitializer {
        private String contextConfigLocation;

        @Override
        public void onStartup(ServletContext servletContext) {
            //only initialize when created from ZatsSpringBootContextLoaderListener.contextInitialized
            if (contextConfigLocation != null) {
                createRootApplicationContext(servletContext);
            }
        }

        @Override
        protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
            try {
                return builder.sources(Class.forName(contextConfigLocation));
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("couldn't initialize contextConfigLocation");
            }
        }
    }
}