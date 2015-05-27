package org.springframework.cloud.zookeeper.discovery
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*

@ContextConfiguration(classes = Config, loader = SpringApplicationContextLoader)
@ActiveProfiles('ribbon')
@WebIntegrationTest
class ZookeeperDiscoveryISpec extends Specification {

	public static final String TEST_INSTANCE_NAME = 'testInstance'

	@Autowired TestRibbonClient testRibbonClient
	@Autowired WireMockServer wiremockServer
	@Autowired DiscoveryClient discoveryClient
	WireMock wireMock

	def setup() {
		wireMock = new WireMock('localhost', wiremockServer.port())
		wireMock.register(get(urlEqualTo('/ping')).willReturn(aResponse().withBody('pong')))
	}

	def 'should find a collaborator via Ribbon'() {
		expect:
			'pong' == testRibbonClient.pingService(TEST_INSTANCE_NAME)
	}

	def 'should find the app by its name via Ribbon'() {
		expect:
			'{"status":"UP"}' == testRibbonClient.thisHealthCheck()
	}

	def 'should find a collaborator via discovery client'() {
		given:
			List<ServiceInstance> instances = discoveryClient.getInstances(TEST_INSTANCE_NAME)
			ServiceInstance instance = instances.first()
		expect:
			'pong' == testRibbonClient.pingOnUrl("${instance.host}:${instance.port}")
	}

	def 'should properly find local instance'() {
		expect:
			AddressProviderConfiguration.ipAddress == discoveryClient.localServiceInstance.host
	}

	@Configuration
	@EnableAutoConfiguration
	@Import(CommonTestConfig)
	@EnableDiscoveryClient
	static class Config {

		@Bean
		TestRibbonClient testRibbonClient(@LoadBalanced RestTemplate restTemplate,
										  @Value('${spring.application.name}') String springAppName) {
			return new TestRibbonClient(restTemplate, springAppName)
		}

	}

	static class TestRibbonClient extends TestServiceRestClient {

		private final String thisAppName

		TestRibbonClient(RestTemplate restTemplate, String thisAppName) {
			super(restTemplate)
			this.thisAppName = thisAppName
		}

		String thisHealthCheck() {
			return restTemplate.getForObject("http://$thisAppName/health", String)
		}

	}
}
