package org.springframework.cloud.zookeeper.discovery.watcher.presence;

public class NoInstancesRunningException extends RuntimeException {
	public NoInstancesRunningException(String dependencyName) {
		super("Required microservice dependency with name [" + dependencyName + "] is missing");
	}
}
