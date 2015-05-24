package org.springframework.cloud.zookeeper.discovery.watcher.presence

import org.apache.curator.x.discovery.ServiceCache
import org.codehaus.groovy.runtime.StackTraceUtils
import spock.lang.Specification

class DefaultDependencyPresenceOnStartupVerifierSpec extends Specification {

    private static final String SERVICE_NAME = 'service01'

    def 'should throw exception if obligatory dependencies are missing'() {
        given:
            DefaultDependencyPresenceOnStartupVerifier dependencyVerifier = new DefaultDependencyPresenceOnStartupVerifier()
            ServiceCache serviceCache = Mock()
            serviceCache.instances >> []
        when:
            dependencyVerifier.verifyDependencyPresence(SERVICE_NAME, serviceCache, true)
        then:
            Throwable thrown = thrown(Throwable)
            StackTraceUtils.extractRootCause(thrown).class == NoInstancesRunningException
    }

}
