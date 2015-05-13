package org.springframework.cloud.zookeeper.discovery.watcher.presence;

import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.zookeeper.discovery.watcher.presence.PresenceChecker;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class LogMissingDependencyChecker implements PresenceChecker {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void checkPresence(String dependencyName, List<ServiceInstance> serviceInstances) {
        if (serviceInstances.isEmpty()) {
            log.warn("Microservice dependency with name [" + dependencyName + "] is missing.");
        }

    }

}
