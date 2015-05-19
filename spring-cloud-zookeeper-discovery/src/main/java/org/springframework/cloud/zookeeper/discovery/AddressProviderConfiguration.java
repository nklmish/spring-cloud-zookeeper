package org.springframework.cloud.zookeeper.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

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
        return getIpAddress();
    }

    /**
     * Return a non loopback IPv4 address for the machine running this process.
     * If the machine has multiple network interfaces, the IP address for the
     * first interface returned by {@link java.net.NetworkInterface#getNetworkInterfaces}
     * is returned.
     *
     * @return non loopback IPv4 address for the machine running this process
     *
     * @see java.net.NetworkInterface#getNetworkInterfaces
     * @see java.net.NetworkInterface#getInetAddresses
     */
    public static String getIpAddress() {
        try {
            for(Enumeration<NetworkInterface> enumNic = NetworkInterface.getNetworkInterfaces();
                enumNic.hasMoreElements();) {
                NetworkInterface ifc = enumNic.nextElement();
                if (ifc.isUp()) {
                    for (Enumeration<InetAddress> enumAddr = ifc.getInetAddresses();
                         enumAddr.hasMoreElements(); ) {
                        InetAddress address = enumAddr.nextElement();
                        if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                            return address.getHostAddress();
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            // ignore
        }

        return "unknown";
    }

}
