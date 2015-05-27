package org.springframework.cloud.zookeeper.discovery
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.SpringApplicationContextLoader
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
@ActiveProfiles('watcher')
class ZookeeperRibbonWithDependenciesISpec extends Specification {

	@Autowired TestRibbonClient testRibbonClient
	@Autowired WireMockServer wiremockServer
	WireMock wireMock

	def setup() {
		wireMock = new WireMock('localhost', wiremockServer.port())
		wireMock.register(get(urlEqualTo('/ping')).willReturn(aResponse().withBody('pong')))
	}

	def 'should find a collaborator via Ribbon by using its alias from dependencies'() {
		expect:
			'pong' == testRibbonClient.ping()
	}

	@Configuration
	@EnableAutoConfiguration
	@Import(CommonTestConfig)
	@EnableDiscoveryClient
	static class Config {

		@Bean
		TestRibbonClient testRibbonClient(@LoadBalanced RestTemplate restTemplate) {
			return new TestRibbonClient(restTemplate)
		}

	}

	static class TestRibbonClient {

		private final RestTemplate restTemplate;

		TestRibbonClient(RestTemplate restTemplate) {
			this.restTemplate = restTemplate
		}

		String ping() {
			return restTemplate.getForObject('http://someAlias/ping', String)
		}

	}
}