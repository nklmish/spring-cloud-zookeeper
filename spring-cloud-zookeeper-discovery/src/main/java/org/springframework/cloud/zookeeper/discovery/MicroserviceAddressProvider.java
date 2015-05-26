package org.springframework.cloud.zookeeper.discovery;

/**
 * Holder for microservice's host and port
 */
public class MicroserviceAddressProvider {
	private final String host;
	private final int port;

	public MicroserviceAddressProvider(String microserviceHost, int microservicePort) {
		this.host = microserviceHost;
		this.port = microservicePort;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
}