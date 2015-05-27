package org.springframework.cloud.zookeeper.discovery
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.test.TestingServer
import org.apache.curator.x.discovery.ServiceDiscovery
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder
import org.apache.curator.x.discovery.ServiceInstance
import org.apache.curator.x.discovery.UriSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.zookeeper.ZookeeperProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.SocketUtils
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*

@ContextConfiguration(classes = Config, loader = SpringApplicationContextLoader)
@ActiveProfiles('ribbon')
@WebIntegrationTest("server.port=8080")
class ZookeeperRibbonISpec extends Specification {

	@Autowired TestRibbonClient testRibbonClient
	@Autowired WireMockServer wiremockServer
	WireMock wireMock

	def setup() {
		wireMock = new WireMock('localhost', wiremockServer.port())
		wireMock.register(get(urlEqualTo('/ping')).willReturn(aResponse().withBody('pong')))
	}

	def 'should find a collaborator via Ribbon'() {
		expect:
			'pong' == testRibbonClient.ping()
	}

	def 'should find the app by its name via Ribbon'() {
		expect:
			'{"status":"UP"}' == testRibbonClient.thisHealthCheck()
	}

	@Configuration
	@EnableAutoConfiguration
	@EnableDiscoveryClient
	static class Config {

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

		@Bean
		TestRibbonClient testRibbonClient(@LoadBalanced RestTemplate restTemplate,
										  @Value('${spring.application.name}') String springAppName) {
			return new TestRibbonClient(restTemplate, springAppName)
		}

		@Bean ZookeeperProperties zookeeperProperties() {
			return new ZookeeperProperties(connectString: "localhost:${testingServer().port}")
		}

	}

	static class TestServiceRegistrar {

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

	static class TestRibbonClient {

		private final RestTemplate restTemplate;
		private final String thisAppName

		TestRibbonClient(RestTemplate restTemplate, String thisAppName) {
			this.restTemplate = restTemplate
			this.thisAppName = thisAppName
		}

		String ping() {
			return restTemplate.getForObject('http://testInstance/ping', String)
		}

		String thisHealthCheck() {
			return restTemplate.getForObject("http://$thisAppName/health", String)
		}

	}
}
