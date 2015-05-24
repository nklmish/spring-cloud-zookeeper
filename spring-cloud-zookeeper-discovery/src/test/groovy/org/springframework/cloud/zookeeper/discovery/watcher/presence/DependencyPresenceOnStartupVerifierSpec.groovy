package org.springframework.cloud.zookeeper.discovery.watcher.presence

import org.apache.curator.x.discovery.ServiceCache
import spock.lang.Specification

class DependencyPresenceOnStartupVerifierSpec extends Specification {

    private static final String SERVICE_NAME = 'service01'

    def 'should check optional dependency using optional dependency checker'() {
        given:
            PresenceChecker optionalDependencyChecker = Mock()
            DependencyPresenceOnStartupVerifier dependencyVerifier = new DependencyPresenceOnStartupVerifier(optionalDependencyChecker) {
            }
            ServiceCache serviceCache = Mock()
            serviceCache.instances >> []
        when:
            dependencyVerifier.verifyDependencyPresence(SERVICE_NAME, serviceCache, false)
        then:
            1 * optionalDependencyChecker.checkPresence(SERVICE_NAME, serviceCache.instances)
    }

}
