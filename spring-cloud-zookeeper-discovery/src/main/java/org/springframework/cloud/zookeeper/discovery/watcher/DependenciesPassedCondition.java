package org.springframework.cloud.zookeeper.discovery.watcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.invoke.MethodHandles;
import java.util.Map;

class DependenciesPassedCondition extends SpringBootCondition {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        try {
            Map<String, Object> subProperties = new RelaxedPropertyResolver(context.getEnvironment()).getSubProperties("zookeeper.dependencies");
            return subProperties.isEmpty() ?
                    ConditionOutcome.noMatch("No dependencies have been passed for the service") :
                    ConditionOutcome.match();
        } catch (BeansException beansException) {
            log.debug("Exception occurred while trying to find matching ZookeeperDependencies", beansException);
            return ConditionOutcome.noMatch("None or more than one ZookeeperDependency found");
        }
    }

}
