package org.springframework.cloud.zookeeper.discovery.watcher;

public interface DependencyStateChangeListener {

    /**
     * Register a listener for a dependency
     *
     * @param listener
     */
    void registerDependencyStateChangeListener(DependencyWatcherListener listener);

    /**
     * Unregister a listener for a dependency
     *
     * @param listener
     */
    void clearDependencyStateChangeListener(DependencyWatcherListener listener);
}
