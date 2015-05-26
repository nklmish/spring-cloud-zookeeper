package org.springframework.cloud.zookeeper.discovery.watcher;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

class DependenciesPassedCondition extends SpringBootCondition {

	private static final String ZOOKEEPER_DEPENDENCIES_PROP = "zookeeper.dependencies";

	@Override
	public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
		Map<String, Object> subProperties = new RelaxedPropertyResolver(context.getEnvironment()).getSubProperties(ZOOKEEPER_DEPENDENCIES_PROP);
		return subProperties.isEmpty() ?
				ConditionOutcome.noMatch("No dependencies have been passed for the service") :
				ConditionOutcome.match();
	}

}
