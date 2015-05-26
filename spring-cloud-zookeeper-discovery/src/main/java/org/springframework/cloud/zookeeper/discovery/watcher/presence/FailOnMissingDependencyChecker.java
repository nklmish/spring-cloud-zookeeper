package org.springframework.cloud.zookeeper.discovery.watcher.presence;

import org.apache.curator.x.discovery.ServiceInstance;
import org.springframework.cloud.zookeeper.discovery.watcher.presence.NoInstancesRunningException;
import org.springframework.cloud.zookeeper.discovery.watcher.presence.PresenceChecker;

import java.util.List;

public class FailOnMissingDependencyChecker implements PresenceChecker {
	@Override
	public void checkPresence(String dependencyName, List<ServiceInstance> serviceInstances) {
		if (serviceInstances.isEmpty()) {
			throw new NoInstancesRunningException(dependencyName);
		}
	}

}
