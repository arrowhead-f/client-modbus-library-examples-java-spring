package de.twt.client.modbus.plcDemoProductionWP1;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import de.twt.client.modbus.common.cache.ModbusDataCacheManager;
import de.twt.client.modbus.common.constants.PackageConstants;
import de.twt.client.modbus.consumer.Consumer;
import de.twt.client.modbus.slave.SlaveTCP;
import de.twt.client.modbus.slave.SlaveTCPConfig;
import eu.arrowhead.common.CommonConstants;

@SpringBootApplication
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = {
		CommonConstants.BASE_PACKAGE, 
		PackageConstants.BASE_PACKAGE_COMMON, 
		PackageConstants.BASE_PACKAGE_PROVIDER,
		PackageConstants.BASE_PACKAGE_CONSUMER,
		PackageConstants.BASE_PACKAGE_SLAVE
		})
public class AppPLCProductionDemo implements ApplicationRunner {
	
	// private MasterTest master;
	
	@Autowired
	@Qualifier("slavePLC")
	private SlaveTCP slavePLC;
	
	@Bean
	public SlaveTCP slavePLC(@Qualifier("slaveConfig") SlaveTCPConfig slaveTCPConfig) {
		return new SlaveTCP(slaveTCPConfig);
	}
	
	@Bean
	@ConfigurationProperties(prefix="slave")
	public SlaveTCPConfig slaveConfig() {
		return new SlaveTCPConfig();
	}

	@Autowired
	private Consumer consumer;
	
	private boolean flagOpcua = false;
	
	private final Logger logger = LogManager.getLogger(AppPLCProductionDemo.class);
	
	public static void main(final String[] args) {
		SpringApplication.run(AppPLCProductionDemo.class, args);
	}

	@Override
	public void run(final ApplicationArguments args) throws Exception {
		logger.info("App started...");
		ModbusDataCacheManager.setDiscreteInput("127.0.0.1", 0, false);
		slavePLC.startSlave();
		consumer.sendDataToOPCUA();
		int registers[] = {1, 1, 1, 1};
		ModbusDataCacheManager.setHoldingRegisters("127.0.0.1", 10, registers);
//		ModbusDataCacheManager.setHoldingRegister("127.0.0.1", 0, 12);
//		ModbusDataCacheManager.setHoldingRegister("127.0.0.1", 1, 1);
    	
		consumer.sendModbusDataToDataManager();
		
		while(true) {
			TimeUnit.SECONDS.sleep(10);
			//consumer.sendDataToOPCUA();
//			System.out.println(Utilities.toJson(ModbusDataCacheManager.getDiscreteInputs("127.0.0.1")));
//			System.out.println(Utilities.toJson(ModbusDataCacheManager.getHoldingRegisters("127.0.0.1")));
			if(ModbusDataCacheManager.getHoldingRegisters("127.0.0.1").containsKey(1)) {
				if(ModbusDataCacheManager.getHoldingRegisters("127.0.0.1").get(1) != 1){
					flagOpcua = true;
				} else if(flagOpcua) {
					consumer.sendDataToOPCUA();
					flagOpcua = false;
				}
			}
			// ModbusDataCacheManager.setDiscreteInput("127.0.0.1", 0, false);
		}
		
	}
}
