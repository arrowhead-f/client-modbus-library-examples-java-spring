package de.twt.client.modbus.remoteIO;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import de.twt.client.modbus.common.cache.ModbusDataCacheManager;
import de.twt.client.modbus.common.constants.PackageConstants;
import de.twt.client.modbus.master.MasterTCP;
import de.twt.client.modbus.master.MasterTCPConfig;
import de.twt.client.modbus.publisher.Publisher;
import de.twt.client.modbus.publisher.EventModbusData;
import eu.arrowhead.common.CommonConstants;

@SpringBootApplication
@EnableConfigurationProperties({MasterTCPConfig.class, EventModbusData.class})
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, 
		PackageConstants.BASE_PACKAGE_MASTER,
		PackageConstants.BASE_PACKAGE_COMMON, 
		PackageConstants.BASE_PACKAGE_PROVIDER,
		PackageConstants.BASE_PACKAGE_PUBLISHER
		})
@PropertySource("classpath:application.properties")
public class MasterApp implements ApplicationRunner {
	
	@Autowired
	private SlaveTest slave;
	
	@Autowired
	@Qualifier("master")
	private MasterTCP master;
	
	@Bean
	public MasterTCP master(@Qualifier("masterTCPConfig") MasterTCPConfig masterTCPConfig) {
		return new MasterTCP(masterTCPConfig);
	}
	
	@Bean
	@ConfigurationProperties(prefix="master")
	public MasterTCPConfig masterTCPConfig() {
		return new MasterTCPConfig();
	}
	
	@Autowired 
	private Publisher publisher;
	
	@Autowired
	@Qualifier("configModbusData")
	private EventModbusData configModbusData;
	
	@Bean
	@ConfigurationProperties(prefix="event.modbusdata")
	public EventModbusData configModbusData() {
		return new EventModbusData();
	}
	
	private final Logger logger = LogManager.getLogger(MasterApp.class);
	
	public static void main( final String[] args ) {
		SpringApplication.run(MasterApp.class, args);
    }
	
	@Override
	public void run(final ApplicationArguments args) throws Exception {
		logger.info("start running...");
		ModbusDataCacheManager.createModbusData("127.0.0.1");
		slave.setData();
		slave.startSlave();
		master.init();
		master.readDataForEvent();
		//master.readDataThreadForEvent();
		// master.writeDataThread();
		TimeUnit.MILLISECONDS.sleep(2000);
		// publisher.publishModbusData(configModbusData);
		publisher.publishOntology();
		
	}
}
