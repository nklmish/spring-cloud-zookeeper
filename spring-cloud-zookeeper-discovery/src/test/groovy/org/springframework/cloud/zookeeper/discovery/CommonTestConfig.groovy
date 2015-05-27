package org.springframework.cloud.zookeeper.discovery
import com.github.tomakehurst.wiremock.WireMockServer
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.test.TestingServer
import org.springframework.cloud.zookeeper.ZookeeperProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.SocketUtils

@PackageScope
@CompileStatic
@Configuration
class CommonTestConfig {
	@Bean(destroyMethod = 'close')
	TestingServer testingServer() {
		return new TestingServer(SocketUtils.findAvailableTcpPort())
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
	TestServiceRegistrar testServiceRegistrar(CuratorFramework curatorFramework) {
		return new TestServiceRegistrar(wiremockServer().port(), curatorFramework)
	}

	@Bean(initMethod = "start", destroyMethod = "shutdown") WireMockServer wiremockServer() {
		return new WireMockServer(SocketUtils.findAvailableTcpPort())
	}

	@Bean ZookeeperProperties zookeeperProperties() {
		return new ZookeeperProperties(connectString: "localhost:${testingServer().port}")
	}
}
