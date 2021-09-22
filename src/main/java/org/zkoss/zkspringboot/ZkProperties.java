package org.zkoss.zkspringboot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

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