package org.springframework.cloud.zookeeper.discovery.watcher;

import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies;
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies.ZookeeperDependency;
import org.springframework.cloud.zookeeper.discovery.watcher.presence.DependencyPresenceOnStartupVerifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultDependencyWatcher implements DependencyRegistrationHookProvider {

	private final ServiceDiscovery serviceDiscovery;
	private final Map<String, ServiceCache> dependencyRegistry = new HashMap<>();
	private final List<DependencyWatcherListener> listeners;
	private final DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier;
	private final ZookeeperDependencies zookeeperDependencies;

	public DefaultDependencyWatcher(ServiceDiscovery serviceDiscovery,
									DependencyPresenceOnStartupVerifier dependencyPresenceOnStartupVerifier,
									List<DependencyWatcherListener> dependencyWatcherListeners,
									ZookeeperDependencies zookeeperDependencies) {
		this.serviceDiscovery = serviceDiscovery;
		this.dependencyPresenceOnStartupVerifier = dependencyPresenceOnStartupVerifier;
		this.listeners = dependencyWatcherListeners;
		this.zookeeperDependencies = zookeeperDependencies;
	}

	@Override
	public void registerDependencyRegistrationHooks() throws Exception {
		for (ZookeeperDependency zookeeperDependency : zookeeperDependencies.getDependencyConfigurations()) {
			String dependencyPath = zookeeperDependency.getPath();
			ServiceCache serviceCache = serviceDiscovery.serviceCacheBuilder().name(dependencyPath).build();
			serviceCache.start();
			dependencyPresenceOnStartupVerifier.verifyDependencyPresence(dependencyPath, serviceCache, zookeeperDependency.isRequired());
			dependencyRegistry.put(dependencyPath, serviceCache);
			serviceCache.addListener(new DependencyStateChangeListenerRegistry(listeners, dependencyPath, serviceCache));
		}
	}

	@Override
	public void clearDependencyRegistrationHooks() throws IOException {
		for (ServiceCache cache : dependencyRegistry.values()) {
			cache.close();
		}
	}

}
