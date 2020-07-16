# Arrowhead Client Modbus Library Examples (Java Spring-Boot)
##### The project provides client modbus library examples for the Arrowhead Framework 4.1.3

Arrowhead Client Modbus Library contains the dependencies "Arrowhead Client Modbus Library" to provide the data transfer between modbus tcp components through arrowhead core system. There are two clients here. One of them is connected with remote IO to provide services and publish events. The other is connected with PLC to transfer the PLC commands to the first client.

### Requirements

The project has the following dependencies:
* JRE/JDK 11 [Download from here](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
* Maven 3.5+ [Download from here](http://maven.apache.org/download.cgi) | [Install guide](https://www.baeldung.com/install-maven-on-windows-linux-mac)
* GitHub Packages [Configuring Maven for GitHub Packages](https://help.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-apache-maven-for-use-with-github-packages)


##### client-modbus-remote-io
There are three basic packages from arrowhead modbus client library here in this package, namely modbus master, service provider and event publisher. The *application.properties* and application file needs to be adjusted to these three packages from the modbus client library
* *application.properties*: The definition of the application properties is declared in the [client modbus library repositoy](https://github.com/arrowhead-f/client-modbus-library-java-spring). Location: `client-modbus-remote-io/src/main/resources`
* application file: Location: `client-modbus-remote-io/src/main/java/de/twt/client/modbus/remoteIO/MasterApp.java`.
    * add packages to *@ComponentScan*
    * declare master, provider and publisher beans based on the name from *application.property*
    * the provider bean wird automatisch gestartet
    * start master bean: initial the master and then start. Based on *application.property* to decide in which mode the master bean should run.
    * start publisher: based on *application.property*, select one event to publish *publishModbusData()/publishOntology()*

##### client-modbus-plc

There are three basic packages from arrowhead modbus client library here in this package as well, namely modbus slave, service consumer and event subscriber. The *application.properties* and application file needs to be adjusted to these three packages from the modbus client library
* *application.properties*: The definition of the application properties is declared in the [client modbus library repositoy](https://github.com/arrowhead-f/client-modbus-library-java-spring). Location: `client-modbus-plc/src/main/resources`
* application file: Location: `client-modbus-plc/src/main/java/de/twt/client/modbus/remoteIO/SlaveApp.java`.
    * add packages to *@ComponentScan*
    * declare slave, consumer and subscriber beans based on the name from *application.property*
    * the subscriber bean wird automatisch gestartet
    * the consumer bean wird automatisch gestartet
    * start slave bean: *startSlave()*