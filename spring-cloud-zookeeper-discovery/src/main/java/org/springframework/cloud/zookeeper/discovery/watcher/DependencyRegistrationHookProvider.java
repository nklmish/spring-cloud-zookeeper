package org.springframework.cloud.zookeeper.discovery.watcher;

import java.io.IOException;

public interface DependencyRegistrationHookProvider {

    /**
     * Register hooks upon dependencies registration
     *
     * @throws Exception
     */
    void registerDependencyRegistrationHooks() throws Exception;

    /**
     * Unregister hooks upon dependencies registration
     *
     * @throws IOException
     */
    void clearDependencyRegistrationHooks() throws IOException;

}
