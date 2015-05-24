package org.springframework.cloud.zookeeper.discovery.base

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import org.apache.commons.lang.math.RandomUtils
import org.apache.tomcat.util.http.fileupload.IOUtils

@CompileStatic
@Slf4j
class AvailablePortScanner {

    private static final int MAX_RETRY_COUNT = 1000

    private final int minPortNumber
    private final int maxPortNumber
    private final int maxRetryCount

    AvailablePortScanner(int minPortNumber, int maxPortNumber, int maxRetryCount = MAX_RETRY_COUNT) {
        checkPortRanges(minPortNumber, maxPortNumber)
        this.minPortNumber = minPortNumber
        this.maxPortNumber = maxPortNumber
        this.maxRetryCount = maxRetryCount
    }

    private void checkPortRanges(int minPortNumber, int maxPortNumber) {
        if (minPortNumber >= maxPortNumber) {
            throw new InvalidPortRange(minPortNumber, maxPortNumber)
        }
    }

    public static int getNextFreePort(int minPortNumber = 8000, int maxPortNumber = 9000, int maxRetryCount = 1) {
        return new AvailablePortScanner(minPortNumber, maxPortNumber, maxRetryCount).tryToExecuteWithFreePort { int port ->
            return port
        }
    }

    protected <T> T tryToExecuteWithFreePort(Closure<T> closure) {
        for (i in (1..maxRetryCount)) {
            try {
                int numberOfPortsToBind = maxPortNumber - minPortNumber
                int portToScan = RandomUtils.nextInt(numberOfPortsToBind) + minPortNumber
                checkIfPortIsAvailable(portToScan)
                return executeLogicForAvailablePort(portToScan, closure)
            } catch (BindException exception) {
                log.debug("Failed to execute closure (try: $i/$maxRetryCount)", exception)
            }
        }
        throw new NoPortAvailableException(minPortNumber, maxPortNumber)
    }

    private static <T> T executeLogicForAvailablePort(int portToScan, Closure<T> closure) {
        log.debug("Trying to execute closure with port [$portToScan]")
        return closure(portToScan)
    }

    private static void checkIfPortIsAvailable(int portToScan) {
        ServerSocket socket = null
        try {
            socket = new ServerSocket(portToScan)
        } finally {
            IOUtils.closeQuietly(socket)
        }
    }

    static class NoPortAvailableException extends RuntimeException {
        @PackageScope
        NoPortAvailableException(int lowerBound, int upperBound) {
            super("Could not find available port in range $lowerBound:$upperBound")
        }
    }

    static class InvalidPortRange extends RuntimeException {
        @PackageScope
        InvalidPortRange(int lowerBound, int upperBound) {
            super("Invalid bounds exceptions, min port [$lowerBound] is greater or equal to max port [$upperBound]")
        }
    }
}
