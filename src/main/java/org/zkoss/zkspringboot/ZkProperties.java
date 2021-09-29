package org.zkoss.zkspringboot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This is an adaptation, the original is at
 * https://github.com/zkoss/zkspringboot/blob/master/zkspringboot-autoconfig/src/main/java/org/zkoss/zkspringboot/ZkProperties.java
 */
@Data
@ConfigurationProperties(prefix = "zk")
public class ZkProperties {
    private String springbootPackaging = "jar";

    private String updateUri = "/zkau";
    private String resourceUri;

    private String homepage;

    private boolean zulViewResolverEnabled = true;
    private String zulViewResolverPrefix = "";
    private String zulViewResolverSuffix = ".zul";

    private String richletFilterMapping;

    public boolean isWar() {
        return "war".equals(getSpringbootPackaging());
    }
}