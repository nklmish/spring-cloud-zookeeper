package org.springframework.cloud.zookeeper.discovery.watcher;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryClient;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryClientConfiguration;
import org.springframework.cloud.zookeeper.discovery.watcher.presence.DefaultDependencyPresenceOnStartupVerifier;
import org.springframework.cloud.zookeeper.discovery.watcher.presence.DependencyPresenceOnStartupVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@AutoConfigureAfter(ZookeeperDiscoveryClientConfiguration.class)
public class DependencyWatcherAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier() {
        return new DefaultDependencyPresenceOnStartupVerifier();
    }

    @Bean(initMethod = "registerDependencies", destroyMethod = "unregisterDependencies")
    @ConditionalOnMissingBean
    DependencyWatcher dependencyWatcher(ServiceDiscovery serviceDiscovery,
                                        DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier,
                                        ZookeeperDiscoveryClient zookeeperDiscoveryClient) {
        return new DefaultDependencyWatcher(serviceDiscovery, dependencyPresenceOnStartupVerifier, zookeeperDiscoveryClient);
    }
}
