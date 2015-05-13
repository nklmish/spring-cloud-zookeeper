package org.springframework.cloud.zookeeper.discovery.watcher.presence;

public class DefaultDependencyPresenceOnStartupVerifier extends DependencyPresenceOnStartupVerifier {
    public DefaultDependencyPresenceOnStartupVerifier() {
        super(new LogMissingDependencyChecker());
    }
}
