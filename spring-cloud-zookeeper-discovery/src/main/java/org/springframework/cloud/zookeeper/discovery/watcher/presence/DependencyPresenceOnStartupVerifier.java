package org.springframework.cloud.zookeeper.discovery.watcher.presence;

import org.apache.curator.x.discovery.ServiceCache;

@SuppressWarnings("unchecked")
public abstract class DependencyPresenceOnStartupVerifier {
    private static final PresenceChecker MANDATORY_DEPENDENCY_CHECKER = new FailOnMissingDependencyChecker();
    private final PresenceChecker optionalDependencyChecker;

    public DependencyPresenceOnStartupVerifier(PresenceChecker optionalDependencyChecker) {
        this.optionalDependencyChecker = optionalDependencyChecker;
    }

    public void verifyDependencyPresence(String dependencyName, ServiceCache serviceCache, boolean required) {
        if (required) {
            MANDATORY_DEPENDENCY_CHECKER.checkPresence(dependencyName, serviceCache.getInstances());
        } else {
            optionalDependencyChecker.checkPresence(dependencyName, serviceCache.getInstances());
        }
    }
}
