package de.twt.client.modbus.plcDemoProductionWP1;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;

import de.twt.client.modbus.common.constants.ModbusConstants;
import eu.arrowhead.common.Utilities;

public class MasterTest {
	
	private static final Logger logger = LoggerFactory.getLogger(MasterTest.class);
	
	private static ModbusMaster master;
	private static String slaveAddress = "127.0.0.1";
	private static int slaveId = 1;
	private static int port = 505;
	
	public static void main(String [] args) {
		setupModbusMaster();
		try {
			master.writeSingleCoil(slaveId, 1, false);
			/*
			master.writeSingleCoil(slaveId, 0, true);
			master.writeSingleCoil(slaveId, 1, true);
			System.out.println("master start 0");
			//while (master.readCoils(slaveId, 0, 1)[0]) 
			{
				System.out.println("master start 1");
				TimeUnit.SECONDS.sleep(10);
				System.out.println("master start 2");
				if (!master.isConnected()){
					System.out.println("master not connected");
					master.connect();
				}
				master.writeSingleCoil(slaveId, 0, false);
				System.out.println("master start 3");
				master.writeSingleCoil(slaveId, 1, false);
				System.out.println("master start 3");
			}
			*/
			
		} catch (ModbusProtocolException | ModbusNumberException | ModbusIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void readData(ModbusConstants.MODBUS_DATA_TYPE type, int offset, int quantity) 
			throws ModbusProtocolException, ModbusNumberException, ModbusIOException {
		if (!master.isConnected()){
			master.connect();
		}
		switch(type) {
		case coil: 
			master.readCoils(slaveId, offset, quantity); break;
		case discreteInput: 
			master.readDiscreteInputs(slaveId, offset, quantity); break;
		case holdingRegister:
			master.readHoldingRegisters(slaveId, offset, quantity); break;
		case inputRegister: 
			master.readInputRegisters(slaveId, offset, quantity);break;
		default: break;
		}
	}

	public void writeCoils(int address, int quantity, boolean[] coils) 
			throws ModbusProtocolException, ModbusNumberException, ModbusIOException{
		if (!master.isConnected()){
			master.connect();
		}
		boolean[] coilsWrite = new boolean[quantity];
		for (int idx = 0; idx < quantity; idx++){
			coilsWrite[idx] = coils[idx];
		}
		master.writeMultipleCoils(slaveId, address, coilsWrite);
	
	}
	
	public void writeHoldingRegisters(int address, int quantity, int[] registers) 
			throws ModbusProtocolException, ModbusNumberException, ModbusIOException{	
		if (!master.isConnected()){
			master.connect();
		}
		int[] registersWrite = new int[quantity];
		for (int idx = 0; idx < quantity; idx++){
			registersWrite[idx] = registers[idx];
		}
		master.writeMultipleRegisters(slaveId, address, registersWrite);
	}
	
	private static TcpParameters setTCPParameters(){
		TcpParameters tcpParameters = new TcpParameters();
		String[] nums = slaveAddress.split("\\.");
		byte[] ip = {0, 0, 0, 0};
		if (nums.length == 4){
			for (int idx = 0; idx < nums.length ; idx++)
				ip[idx] = (byte) Integer.parseInt(nums[idx]);
		} else {
			logger.error("MasterTCP: the slave address in properties file is not set correctly!");
		}
		try {
			tcpParameters.setHost(InetAddress.getByAddress(ip));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			logger.error("MasterTCP: the slave address in properties file is not set correctly!");
		}
        tcpParameters.setKeepAlive(true);
        tcpParameters.setPort(port);
        return tcpParameters;
	}
	
	public static void setupModbusMaster(){
		TcpParameters tcpParameters = setTCPParameters();
		master = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
        Modbus.setAutoIncrementTransactionId(true);
        try {
	        if (!master.isConnected()) {
	        	master.connect();
	        }
	        System.out.println("MasterTCP.setModbusMaster: master is connected with slave.");
	        logger.info("MasterTCP.setModbusMaster: master is connected with slave ({}).", slaveAddress);
        } catch (ModbusIOException e) {
			// TODO Auto-generated catch block
			logger.error("MasterTCP.setModbusMaster: master cannot be connected with slave ({}).", slaveAddress);
			e.printStackTrace();
		}
	}
	
	
}
