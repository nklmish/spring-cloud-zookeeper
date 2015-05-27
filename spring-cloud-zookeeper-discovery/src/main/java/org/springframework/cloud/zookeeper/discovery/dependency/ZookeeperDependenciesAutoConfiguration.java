package org.springframework.cloud.zookeeper.discovery.dependency;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.zookeeper.ZookeeperAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Provides autoconfiguration for Zookeeper dependency set up in properties.
 *
 * @see ZookeeperDependencies
 */
@Configuration
@EnableConfigurationProperties
@Conditional(DependenciesPassedCondition.class)
@ConditionalOnProperty(value = "zookeeper.dependencies.enabled", matchIfMissing = true)
@AutoConfigureAfter(ZookeeperAutoConfiguration.class)
public class ZookeeperDependenciesAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ZookeeperDependencies zookeeperDependencies() {
		return new ZookeeperDependencies();
	}

}
