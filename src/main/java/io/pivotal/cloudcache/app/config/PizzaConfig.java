package io.pivotal.cloudcache.app.config;

import org.apache.geode.cache.client.SocketFactory;
import org.apache.geode.cache.client.proxy.SniProxySocketFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

public class PizzaConfig {


    @Profile("app-foundation")
    @Bean("mySocketFactory")
    SocketFactory getSocketFactoryBean(@Value("${sni.hostname:tcp.foundation.cf-app.com}") String hostname,
                                       @Value("${sni.port:8888}") int port) {
        return  new SniProxySocketFactory(hostname, port);
    }
}
