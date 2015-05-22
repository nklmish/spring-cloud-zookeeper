package org.springframework.cloud.zookeeper.discovery.watcher;

import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryClient;
import org.springframework.cloud.zookeeper.discovery.watcher.presence.DependencyPresenceOnStartupVerifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultDependencyWatcher implements DependencyWatcher {

    private final ServiceDiscovery serviceDiscovery;
    private final Map<String, ServiceCache> dependencyRegistry = new HashMap<>();
    private final List<DependencyWatcherListener> listeners = new ArrayList<>();
    private final DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier;
    private final ZookeeperDiscoveryClient zookeeperDiscoveryClient;

    public DefaultDependencyWatcher(ServiceDiscovery serviceDiscovery,
                                    DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier,
                                    ZookeeperDiscoveryClient zookeeperDiscoveryClient) {
        this.serviceDiscovery = serviceDiscovery;
        this.dependencyPresenceOnStartupVerifier = dependencyPresenceOnStartupVerifier;
        this.zookeeperDiscoveryClient = zookeeperDiscoveryClient;
    }

    @Override
    public void registerDependencyRegistrationHooks() throws Exception {
        for (String dependencyPath : getListOfServiceDependencies(zookeeperDiscoveryClient)) {
            ServiceCache serviceCache = serviceDiscovery.serviceCacheBuilder().name(dependencyPath).build();
            serviceCache.start();
            dependencyPresenceOnStartupVerifier.verifyDependencyPresence(dependencyPath, serviceCache, retrieveFromMetadataIfServiceIsRequired(dependencyPath));
            dependencyRegistry.put(dependencyPath, serviceCache);
            serviceCache.addListener(new DependencyStateChangeListenerRegistry(listeners, dependencyPath, serviceCache));
        }
    }

    private List<String> getListOfServiceDependencies(ZookeeperDiscoveryClient zookeeperDiscoveryClient) {
        // TODO: Think of how to solve this
        return zookeeperDiscoveryClient.getServices();
    }

    private boolean retrieveFromMetadataIfServiceIsRequired(String dependencyPath) {
        // TODO: Think of how to solve this
        return false;
    }

    @Override
    public void clearDependencyRegistrationHooks() throws IOException {
        listeners.clear();
        for (ServiceCache cache : dependencyRegistry.values()) {
            cache.close();
        }
    }

    @Override
    public void registerDependencyStateChangeListener(DependencyWatcherListener listener) {
        listeners.add(listener);
    }

    @Override
    public void clearDependencyStateChangeListener(DependencyWatcherListener listener) {
        listeners.remove(listener);
    }

}