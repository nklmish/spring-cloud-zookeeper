/*
/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.zookeeper.discovery.dependency;

import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.SneakyThrows;
import org.apache.curator.x.discovery.ProviderStrategy;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.strategies.RandomStrategy;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import org.apache.curator.x.discovery.strategies.StickyStrategy;

/**
 * @author Marcin Grzejszczak, 4financeIT
 * @author Nakul Mishra, 4financeIT
 */
public class DependencyAwareLoadBalancer extends DynamicServerListLoadBalancer {

	private static final ProviderStrategyFactory PROVIDER_STRATEGY_FACTORY = new ProviderStrategyFactory();
	private final ZookeeperDependencies zookeeperDependencies;
	private final ServiceDiscovery serviceDiscovery;

	public DependencyAwareLoadBalancer(ZookeeperDependencies zookeeperDependencies, ServiceDiscovery serviceDiscovery) {
		this.zookeeperDependencies = zookeeperDependencies;
		this.serviceDiscovery = serviceDiscovery;
	}

	@Override
	@SneakyThrows
	public Server chooseServer(Object key) {
		ServiceProvider serviceProvider = startedServiceProvider(key);
		ServiceInstance instance = serviceProvider.getInstance();
		return new Server(instance.getAddress(), instance.getPort());
	}

	@SneakyThrows
	@SuppressWarnings("unchecked")
	private ServiceProvider startedServiceProvider(Object key) {
		String path = (String) key;
		ServiceProvider serviceProvider = serviceDiscovery.serviceProviderBuilder().serviceName(path).providerStrategy(loadBalancerStrategyFor(path)).build();
		serviceProvider.start();
		return serviceProvider;
	}

	private ProviderStrategy loadBalancerStrategyFor(String path) {
		ZookeeperDependencies.ZookeeperDependency zookeeperDependency = zookeeperDependencies.getDependencyForPath(path);
		if (zookeeperDependency == null) {
			return new RoundRobinStrategy<>();
		}
		LoadBalancerType loadBalancerType = zookeeperDependency.getLoadBalancerType();
		return PROVIDER_STRATEGY_FACTORY.createProviderStrategy(loadBalancerType);
	}

	private static class ProviderStrategyFactory {

		public ProviderStrategy createProviderStrategy(LoadBalancerType type) {
			switch (type) {
				case ROUND_ROBIN:
					return new RoundRobinStrategy<>();
				case RANDOM:
					return new RandomStrategy<>();
				case STICKY:
					return new StickyStrategy<>(new RoundRobinStrategy<>());
				default:
					throw new IllegalArgumentException("Unknown load balancer type " + type);
			}
		}
	}
}