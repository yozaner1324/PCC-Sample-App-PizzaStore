package io.pivotal.cloudcache.app.config;

import org.apache.geode.cache.client.SocketFactory;
import org.apache.geode.cache.client.proxy.SniProxySocketFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.config.annotation.EnableClusterConfiguration;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnableSsl;

@Profile("off-platform")
@EnableEntityDefinedRegions(basePackages = "io.pivotal.cloudcache.app")
@EnableClusterConfiguration(useHttp = true)
//@EnableSsl
@Configuration
class OffPlatformConfiguration {

    @Bean("mySocketFactory")
    SocketFactory getSocketFactoryBean(@Value("${service-gateway.hostname}") String hostname,
                                       @Value("${service-gateway.port}") int port) {
        return new SniProxySocketFactory(hostname, port);
    }
}
