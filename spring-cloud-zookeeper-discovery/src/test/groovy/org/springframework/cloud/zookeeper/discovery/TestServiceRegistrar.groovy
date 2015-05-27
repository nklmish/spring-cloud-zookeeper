package org.springframework.cloud.zookeeper.discovery
import groovy.transform.CompileStatic
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.UriSpec

@CompileStatic
class TestServiceRegistrar {

	private final int wiremockServerPort
	private final CuratorFramework curatorFramework
	private final ServiceDiscovery serviceDiscovery

	TestServiceRegistrar(int wiremockServerPort, CuratorFramework curatorFramework) {
		this.wiremockServerPort = wiremockServerPort
		this.curatorFramework = curatorFramework
		this.serviceDiscovery = serviceDiscovery()
	}

	void start() {
		serviceDiscovery.start()
	}

	ServiceInstance serviceInstance() {
		return ServiceInstance.builder().uriSpec(new UriSpec("{scheme}://{address}:{port}/"))
				.address('localhost')
				.port(wiremockServerPort)
				.name('testInstance')
				.build()
	}

	ServiceDiscovery serviceDiscovery() {
		return ServiceDiscoveryBuilder
				.builder(Void)
				.basePath('/services')
				.client(curatorFramework)
				.thisInstance(serviceInstance())
				.build()
	}


	void stop() {
		serviceDiscovery.close()
	}
}
