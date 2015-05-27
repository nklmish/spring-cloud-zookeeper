package org.springframework.cloud.zookeeper.discovery

import groovy.transform.PackageScope
import groovy.transform.CompileStatic
import org.springframework.web.client.RestTemplate

@PackageScope
@CompileStatic
class TestServiceRestClient {

	final RestTemplate restTemplate;

	TestServiceRestClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate
	}

	String pingService(String alias) {
		return restTemplate.getForObject("http://$alias/ping", String)
	}

	String pingOnUrl(String url) {
		return new RestTemplate().getForObject("http://$url/ping", String)
	}
}
