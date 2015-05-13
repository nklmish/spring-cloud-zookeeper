package org.springframework.cloud.zookeeper.discovery;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Configuration that registers a bean related to microservice's address and port providing.
 *
 * @see MicroserviceAddressProvider
 */
@Configuration
public class AddressProviderConfiguration {

    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    @Autowired
    private Environment environment;

    @Bean
    MicroserviceAddressProvider microserviceAddressProvider() {
        String microserviceHost = environment.getProperty("microservice.host", resolveMicroserviceLocalhost());
        Integer microservicePort = Integer.valueOf(environment.getProperty("server.port", "8080"));
        return new MicroserviceAddressProvider(microserviceHost, microservicePort);
    }

    public static String resolveMicroserviceLocalhost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("Exception occurred while trying to retrieve localhost", e);
            return StringUtils.EMPTY;
        }
    }

}
