package org.springframework.cloud.zookeeper.discovery.watcher;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryClient;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryClientConfiguration;
import org.springframework.cloud.zookeeper.discovery.watcher.presence.DefaultDependencyPresenceOnStartupVerifier;
import org.springframework.cloud.zookeeper.discovery.watcher.presence.DependencyPresenceOnStartupVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties
@AutoConfigureAfter(ZookeeperDiscoveryClientConfiguration.class)
public class DependencyWatcherAutoConfiguration {

    @Autowired(required = false)
    private List<DependencyWatcherListener> dependencyWatcherListeners = new ArrayList<>();

    @Bean
    @ConditionalOnMissingBean
    public DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier() {
        return new DefaultDependencyPresenceOnStartupVerifier();
    }

    @Bean(initMethod = "registerDependencyRegistrationHooks", destroyMethod = "clearDependencyRegistrationHooks")
    @ConditionalOnMissingBean
    public DependencyRegistrationHookProvider dependencyWatcher(ServiceDiscovery serviceDiscovery,
                                        DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier,
                                        ZookeeperDiscoveryClient zookeeperDiscoveryClient) {
        return new DefaultDependencyWatcher(serviceDiscovery,
                dependencyPresenceOnStartupVerifier,
                zookeeperDiscoveryClient,
                dependencyWatcherListeners);
    }
}
