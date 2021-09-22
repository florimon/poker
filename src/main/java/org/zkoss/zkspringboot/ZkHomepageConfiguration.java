package org.zkoss.zkspringboot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@Configuration
@ConditionalOnProperty(prefix="zk", name="homepage")
@EnableConfigurationProperties({ZkProperties.class})
public class ZkHomepageConfiguration {
    private final String homepage;

    public ZkHomepageConfiguration(ZkProperties zkProperties) {
        homepage = zkProperties.getHomepage();
        log.info("ZK-Springboot: ZkHomepageConfiguration enabled - mapping the root path '/' to '{}'", homepage);
    }

    @GetMapping("/")
    public String home() {
        return homepage;
    }
}