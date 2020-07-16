package de.twt.client.modbus.plcDemoWP1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
import org.springframework.http.HttpMethod;

import de.twt.client.modbus.common.ModbusSystem;
import de.twt.client.modbus.common.cache.ModbusDataCacheManager;
import de.twt.client.modbus.common.constants.PackageConstants;
import de.twt.client.modbus.consumer.Consumer;
import de.twt.client.modbus.dataWriter.ModbusDataRecordContent;
import de.twt.client.modbus.dataWriter.ModbusDataWriter;
import de.twt.client.modbus.master.MasterTCP;
import de.twt.client.modbus.master.MasterTCPConfig;
import de.twt.client.modbus.publisher.EventModbusData;
import de.twt.client.modbus.publisher.Publisher;
import de.twt.client.modbus.slave.SlaveTCP;
import de.twt.client.modbus.slave.SlaveTCPConfig;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.http.HttpService;

@SpringBootApplication
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = {
		CommonConstants.BASE_PACKAGE, 
		PackageConstants.BASE_PACKAGE_COMMON,
		PackageConstants.BASE_PACKAGE_CONSUMER,
		PackageConstants.BASE_PACKAGE_MASTER,
//		PackageConstants.BASE_PACKAGE_SLAVE
		})
public class AppPLCDemo implements ApplicationRunner {
	
	@Autowired
	private Consumer consumer;
	
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
	
//	@Autowired
//	@Qualifier("slavePLC")
//	private SlaveTCP slavePLC;
//	
//	@Bean
//	public SlaveTCP slavePLC(@Qualifier("slaveConfig") SlaveTCPConfig slaveTCPConfig) {
//		return new SlaveTCP(slaveTCPConfig);
//	}
//	
//	@Bean
//	@ConfigurationProperties(prefix="slave")
//	public SlaveTCPConfig slaveConfig() {
//		return new SlaveTCPConfig();
//	}
	
	private final Logger logger = LogManager.getLogger(AppPLCDemo.class);
	
	public static void main(final String[] args) {
		SpringApplication.run(AppPLCDemo.class, args);
	}

	@Override
	public void run(final ApplicationArguments args) throws Exception {
		logger.info("App started...");
		master.init();
		master.readDataThreadForEvent();
		consumer.sendIIOTDataToDataManager();
		
//		slavePLC.startSlave();
//		ModbusDataCacheManager.setDiscreteInput("127.0.0.1", 0, true);
//		int registers[] = {10, 11, 30};
//		ModbusDataCacheManager.setHoldingRegisters("127.0.0.1", 10, registers);
//		consumer.sendModbusDataToDataManager();
		
	}
}
