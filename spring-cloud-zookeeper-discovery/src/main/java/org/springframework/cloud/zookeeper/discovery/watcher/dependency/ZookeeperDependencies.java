package org.springframework.cloud.zookeeper.discovery.watcher.dependency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Data
@ConfigurationProperties("zookeeper")
public class ZookeeperDependencies {

    private String prefix = "";

    private Map<String, ZookeeperDependency> dependencies = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        for (Map.Entry<String, ZookeeperDependency> entry : this.dependencies.entrySet()) {
            ZookeeperDependency value = entry.getValue();
            if (StringUtils.hasText(prefix)) {
                value.path = prefix + value.path;
            }
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ZookeeperDependency {

        private String id;

        private String path;

        private LoadBalancerType loadBalancerType;

        private String contentTypeTemplate;

        private String version;

        private Map<String, String> headers;

        private boolean required;
    }

    public Collection<ZookeeperDependency> getDependencyConfigurations() {
        return dependencies.values();
    }

    public boolean hasDependencies() {
        return !dependencies.isEmpty();
    }
}
