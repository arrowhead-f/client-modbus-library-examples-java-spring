package de.twt.client.modbus.plcRobot;

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

import de.twt.client.modbus.common.ModbusSystem;
import de.twt.client.modbus.common.cache.ModbusDataCacheManager;
import de.twt.client.modbus.common.constants.PackageConstants;
import de.twt.client.modbus.dataWriter.ModbusDataWriter;
import de.twt.client.modbus.publisher.EventModbusData;
import de.twt.client.modbus.publisher.Publisher;
import de.twt.client.modbus.slave.SlaveTCP;
import de.twt.client.modbus.slave.SlaveTCPConfig;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;

@SpringBootApplication
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = {PackageConstants.BASE_PACKAGE_SLAVE, 
		CommonConstants.BASE_PACKAGE, 
		PackageConstants.BASE_PACKAGE_COMMON, 
		//PackageConstants.BASE_PACKAGE_PUBLISHER,
		PackageConstants.BASE_PACKAGE_SUBSCRIBER,
		PackageConstants.BASE_PACKAGE_DATAWRITER
		})
public class AppPLCRobot implements ApplicationRunner {
	/*
	private MasterTest master;
	*/
	
	@Autowired
	private ModbusDataWriter modbusDataWriter;
	
	@Autowired
	@Qualifier("slavePLCRobot")
	private SlaveTCP slavePLCRobot;
	
	@Bean
	public SlaveTCP slavePLCRobot(@Qualifier("slavePLCRobotConfig") SlaveTCPConfig slaveTCPConfig) {
		return new SlaveTCP(slaveTCPConfig);
	}
	
	@Bean
	@ConfigurationProperties(prefix="slave.robot")
	public SlaveTCPConfig slavePLCRobotConfig() {
		return new SlaveTCPConfig();
	}
	/*
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
	
	@Autowired
	private ModbusSystem modbusSystem;
	*/
	
	private final Logger logger = LogManager.getLogger(AppPLCRobot.class);
	
	public static void main(final String[] args) {
		SpringApplication.run(AppPLCRobot.class, args);
	}

	@Override
	public void run(final ApplicationArguments args) throws Exception {
		logger.info("App started...");
		slavePLCRobot.startSlave();
		modbusDataWriter.startRecord();
		// master.setupModbusMaster();
		// boolean[] coils = { true };
		// master.writeCoils(12, 1, coils);
		// logger.info(Utilities.toJson(ModbusDataCacheManager.getDiscreteInputs("10.12.90.10")));
		// ModbusDataCacheManager.createModbusData("127.0.0.1");
		// ModbusDataCacheManager.setCoil("127.0.0.1", 12, true);
		
		// publisher.publishOntology(configModule);
		// publisher.publishModbusDataOnce(configModbusData);
			
	}
}
