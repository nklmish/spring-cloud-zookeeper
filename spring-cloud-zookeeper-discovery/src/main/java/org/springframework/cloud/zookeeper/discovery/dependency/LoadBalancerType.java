package org.springframework.cloud.zookeeper.discovery.dependency;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.Arrays;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.defaultIfEmpty;


public enum LoadBalancerType {
	STICKY, RANDOM, ROUND_ROBIN;

	public static LoadBalancerType fromName(final String strategyName) {
		LoadBalancerType loadBalancerType = (LoadBalancerType) CollectionUtils.find(Arrays.asList(values()), new Predicate() {
			@Override
			public boolean evaluate(Object o) {
				LoadBalancerType input = (LoadBalancerType) o;
				return input.name().equals(defaultIfEmpty(strategyName, EMPTY).toUpperCase());
			}
		});
		if (loadBalancerType == null) {
			return ROUND_ROBIN;
		}
		return loadBalancerType;
	}

}
