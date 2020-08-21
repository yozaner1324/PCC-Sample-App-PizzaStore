/*
 * Copyright (C) 2018-Present Pivotal Software, Inc. All rights reserved.
 * This program and the accompanying materials are made available under
 * the terms of the under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.cloudcache.app;

import java.util.Collections;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.client.SocketFactory;
import org.apache.geode.cache.client.proxy.ProxySocketFactories;
import org.apache.geode.cache.client.proxy.SniProxySocketFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.*;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.geode.config.annotation.EnableDurableClient;
import org.springframework.geode.config.annotation.EnableClusterAware;


/**
 * This class runs the sample pizza store application.
 * This is a Pivotal Cloud Cache (PCC) client application which interacts with a PCC service instance
 * or a GemFire cluster.
 *
 * The configuration classes are defined under the config package.
 * The apis exposed by the client application are defined in AppController class.
 *
 * This application can talk to a TLS enabled PCC service instance or a non-TLS PCC service instance (SI).
 * Staring this application with spring.profiles.active=tls will enable TLS communication (needs TLS enabled
 * service instance).
 *
 */
@SpringBootApplication
@EnableDurableClient(id = "pizza-store")
public class CloudcachePizzaStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudcachePizzaStoreApplication.class, args);
    }


}
