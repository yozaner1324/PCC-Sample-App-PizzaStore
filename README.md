# Example Spring Boot Application for VMware Tanzu GemFire For VMs

This branch demonstrates deployment scenarios for an app,
and how the app's location affects communication with Tanzu GemFire For VMs
service instance.

## About the App

This Spring Boot app uses Spring Boot for Apache Geode & Pivotal GemFire (SBDG).
The app talks to a Tanzu GemFire service instance.
The app does create, read, and delete CRUD operations on data held
in a region within service instance.

The app exposes these endpoints:  
    
-  `https://APP-URL/preheatOven`  
        
    Creates three pre-defined pizzas, which adds them to the region.
        
-  `https://APP-URL/pizzas` 
    
    Gets all pizzas from the region.
        
-  `https://APP-URL/pizzas/{name}`
    
    Gets details of a pizza specified by its name.
         
-  `https://APP-URL/pizzas/order/{name}`
    
    Orders a given pizza, which does a create operation. 
    For example: `https://APP-URL/pizzas/order/myCustomPizza?sauce=MARINARA&toppings=CHEESE,PEPPERONI,MUSHROOM` orders a pizza named `myCustomPizza`,
    which has `MARINARA` sauce and three toppings. 
   
-  `https://APP-URL/cleanSlate` 
        
    Deletes all pizzas from the region.

## Try the App in a Local Environment

This Spring Boot app can run locally, 
without having a Geode or Tanzu GemFire service instance.
The SBDG annotation [`@EnableClusterAware`](https://docs.spring.io/spring-boot-data-geode-build/current/reference/html5/#geode-configuration-declarative-annotations-productivity-enableclusteraware)
redirects cache operations operations to `LOCAL` regions
when there is no service instance to talk to.
It implements an embedded cache on the client.  

To run the app in the local environment,
 
```
mvn spring-boot:run
```
Ignore the `ConnectException: Connection refused`
from the root of this repository.
Use a web browser to talk to the app at `http://localhost:8080`.

## App Location

For an app that is talking to a TGF4VMs service instance, depending on where the app is running, it should fall under one of the below category.

1. **Services Foundation App**

    These are apps running on the same foundation where the Tanzu GemFire service instance is running.
    
2. **Application Foundation App**

    These are apps where the service instance and the app are running on different foundations.
    Such apps are typically running on a foundation which is dedicated for applications alone and services run in a services foundation.

3. **Off-Platform App**

    These are apps which are not running on a Tanzu Platform i.e they are not running on any Cloud Foundry Foundation.
    These apps are typically running on a standalone VM or someone's desktop and talking to a TGF4VMs service instance. 
     
 
This repo demonstrates all the above 3 by use of spring profiles.

We pick each of the scenarios mentioned [above](#categories-of-app) and demonstrate them.

## 1. When your app is running in the same foundation as the service instance (Services Foundation app)

When your app and the service instance are in the same foundation, SBDG (Spring Boot Data Geode) with CF bind experience alleviates 
the need to do any security configuration. Credentials and TLS configurations are auto applied for such application. 

##### Steps:

1. Create a TLS enabled service instance by running `cf create-service p-cloudcache <PLAN_NAME> <SERVICE_INSTANCE> -c '{"tls":true}'`. 
Optionally, you can skip the `-c '{"tls":true}` if you dont want TLS.

2. Modify the [manifest.yml](manifest.yml#L9) and provide the name of the service instance used in the above step.

3. Build the app `mvn clean install`.

4. cf push the app from the root of the project by running `cf push`.

5. Hit the api's exposed by the app. You can get the route on which app is available by looking at the output of the previous step or by running `cf app cloudcache-pizza-store`.

6. Verify by connecting to Gemfire Service instance using the GemFire CLI `gfsh`.

As the app is bound to the service instance (via the declaration in manifest.yml or by running `cf bind`)
SBDG was able to interospect the app container to get the connection details to the service instance. SBDG
then auto configured the app to talk to the service instance. Nothing extra needs to be done to talk to a TLS
enabled service instance.  

## 2. When your app and the service instance are on different foundations (Application Foundation app)

Typically apps run in Application Foundation and service instances run in Services Foundation.
To enable such apps to talk to the service instance, the service instance should have to have a
service gateway enabled so that the service instance is reachable from outside the foundation.

As a prerequisite make sure services gateway setup is done as described in [Setting up service gateway](#setting-up-service-gateway).

#### Steps:

1. Create a **[Service Gateway enabled Service instance](#sge-si)**.

2. Follow steps to **[Create Truststore for TLS communication](#create-truststore-for-tls-communication)**.

4. Copy the truststore created in the above step and place it under the `resources` directory.

5. Configure [application-app-foundation.properties](src/main/resources/application-app-foundation.properties). You will need details of the service key and the truststore created in step 2. 

6. Build the app by running `mvn clean install`.

7. cf push the app by running `cf push -f manifest_app_foundation.yml`. 

10. **Interact with the app** by hitting the endpoints where the app is running (`cf app cloudcache-pizzastore` will show the route)        


## 3. When your app is running <ins>off-platform</ins>

This is the case where your app is not running on a Cloud Foundary Foundation. It could be running on your local machine 
or in a VM in the cloud.

As a prerequisite make sure services gateway setup is done as described in [Setting up service gateway](#setting-up-service-gateway). 

#### Steps:

1. Create a [Service Gateway enabled Service instance](#sge-si).

2. Follow steps to **[Create Truststore for TLS communication](#create-truststore-for-tls-communication)**.
    
3. Copy the truststore created in the above step and place it under the `resources` directory or to one of the locations SBDG expects the truststore to be in one of the 3 well known locations. Details are in SBDG [docs](https://docs.spring.io/autorepo/docs/spring-boot-data-geode-build/1.3.2.RELEASE/reference/html5/#geode-security-ssl).
      
3. **Configure the app to talk to the service instance**
     By configuring details in `application-off-platform.properties` file.  

4. **Build** the app by running `mvn clean install`.

5. **Run the app** by running `mvn spring-boot:run -Dspring-boot.run.profiles=off-platform -Dspring-boot.run.jvmArguments="-Djavax.net.ssl.trustStore=/tmp/mytruststore1.jks -Djavax.net.ssl.trustStorePassword=123456"`.

6. **Interact with the app** by hitting the endpoints at http://localhost:8080           
   
   
### [Setting up service gateway](#settingup-service-gateway)

Service Gateway is a Tanzu GemFire For VMs feature which lets you connect to the service instance
from outside the foundation where the service instance is running. 
To setup below steps have to be performed.

##### Enable TCP Routing

Refer to Refer TAS [docs](https://docs.pivotal.io/application-service/2-10/adminguide/enabling-tcp-routing.html) for details,
at a high level you will have to 

1.a. _Enable TCP routing in the TAS tile of OpsMan_ under _Networking_ tab.
   
1.b. _Enable Services Gateway on the TGF4VMs Tile_ under _Settings_ tab.

1.c. Hit `Apply Changes` in Ops manager.

1.d. _Create a TCP CF route_ as mentioned in [TAS docs](https://docs.pivotal.io/application-service/2-10/adminguide/enabling-tcp-routing.html).
   
   
    cf create-shared-domain tcp.${BOSH_ENV_NAME}.cf-app.com --router-group default-tcp
    cf quotas
    cf update-quota default --reserved-route-ports 10
    cf quotas
    
### [Creating a Service Gateway enabled Service Instance](#sge-si)

When you want to access the service instance from an app that running outside the foundation where the service instance is running,
you enable the service gateway on the service instance. This can be acheived by creating a service gateway enabled service instance.

##### Steps

1. Login to Cloud Foundary CLI (CF CLI) where you want to create the service instance and target (`cf target`) to the org/space where you want to start the service.

2. Create a Service Instance which has a **Service Gateway**:

   By running `cf create-service p-cloudcache <PLAN> <SERVICE_INSTANCE_NAME> -c {"tls":true, "service_gateway":true}`.
   The flg `tls:true` is mandatory when you want to use service gateway feature.
   The flg `service_gateway:true` the service instance to be accessible from outside the foundation.
      
3. Create a **service key**:
   Run `cf service-key <SERVICE_INSTANCE_NAME> <KEY_NAME>`
 
### [Create Truststore for TLS communication](#create-truststore-for-tls-communication)

Truststore is needed for apps to establish TLS connections with the service instance. 
when the service instance is TLS enabled, app has to be able to establish a TLS connection with the service instance. For this purpose the app has to have a truststore with 2 CAs in it and below is how one can get them.
   
   1. Get `services/tls_ca`from credhub by running `credhub get --name="/services/tls_ca" -k certificate > services_ca.crt`.
   
   2. Get the CA from where your TLS termination occurs and store it in a `.crt` file. If your TLS terminates at gorouter then you can get the CA from `OpsManager`-> `Settings`-> `Advanced Options` -> `Download Root CA Cert`.
   
   3. Create a truststore which has both the above CAs
    `keytool -importcert -file services_ca.crt -keystore mytruststore.jks -storetype JKS`
    `keytool -importcert -alias root_ca -file root_ca_certificate -keystore mytruststore.jks -storetype JKS`.
    
   4. Move the truststore to resources directory. SBDG expects the truststore to be in one of the 3 well known locations. Details are in SBDG [docs](https://docs.spring.io/autorepo/docs/spring-boot-data-geode-build/1.3.2.RELEASE/reference/html5/#geode-security-ssl).
 
