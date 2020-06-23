package de.twt.client.modbus.remoteIO;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.data.DataHolder;
import com.intelligt.modbus.jlibmodbus.data.ModbusCoils;
import com.intelligt.modbus.jlibmodbus.data.ModbusHoldingRegisters;
import com.intelligt.modbus.jlibmodbus.exception.IllegalDataAddressException;
import com.intelligt.modbus.jlibmodbus.exception.IllegalDataValueException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.slave.ModbusSlave;
import com.intelligt.modbus.jlibmodbus.slave.ModbusSlaveFactory;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;

@Component
public class SlaveTest {
	private int range = 600;
	
	private ModbusSlave slave;
	private TcpParameters tcpParameters = new TcpParameters();
	private ModbusCoils hc;
	private ModbusCoils hcd;
	private ModbusHoldingRegisters hr;
	private ModbusHoldingRegisters hri;
	private final MyOwnDataHolder dh = new MyOwnDataHolder();
	
	private final Logger logger = LogManager.getLogger(SlaveTest.class);
	
	public int getRange(){
		return range;
	}
	
	public void setData() {	
		try {
			boolean[] diescretInputs = {true, true, true, false, false, false, true, true, true, false, false, false, true};
			hcd.setRange(0, diescretInputs);
			boolean[] coils = {false, true, true, false, false, false, true, true, true, false, false};
			hc.setRange(512, coils);
		} catch (IllegalDataAddressException | IllegalDataValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@PostConstruct
	public void init(){
		logger.debug("init slave tcp...");
		initModbusDataCache();
		try {
			setSlave();
		} catch (IllegalDataAddressException | IllegalDataValueException
				| UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
 	public void startSlave(){
 		logger.debug("slave starts...");
		try {
			slave.listen();
		} catch (ModbusIOException e) {
			e.printStackTrace();
		}
	}
 	
 	private void initModbusDataCache(){
		hc = new ModbusCoils(range);
		hcd = new ModbusCoils(range);
		hr = new ModbusHoldingRegisters(range); 
		hri = new ModbusHoldingRegisters(range);
	}
 	
	private void setSlave() throws IllegalDataAddressException, IllegalDataValueException, UnknownHostException{
		logger.debug("set slave parameters...");
		setTCPConnection();
		slave = ModbusSlaveFactory.createModbusSlaveTCP(tcpParameters);
		slave.setServerAddress(Modbus.TCP_DEFAULT_ID);
        slave.setBroadcastEnabled(true);
        slave.setReadTimeout(1000);
        Modbus.setLogLevel(Modbus.LogLevel.LEVEL_DEBUG);
        setDataHolder();
        slave.setServerAddress(1);
	}
	
	private void setTCPConnection() throws UnknownHostException{
	    tcpParameters.setHost(InetAddress.getLocalHost());
	    tcpParameters.setKeepAlive(true);
	    tcpParameters.setPort(502);
	}

	private void setDataHolder() throws IllegalDataAddressException, IllegalDataValueException{
		dh.addEventListener(new ModbusEventListener() {
            @Override
			public void onReadMultipleCoils(int address, int quantity) {
				System.out.print("onReadMultipleCoils: address " + address + ", quantity " + quantity + ", range "  + hc.getQuantity() + "\n");
			}
            
            @Override
            public void onReadMultipeDiscreteInputs(int address, int quantity){
            	System.out.print("onReadMultipeDiscreteInputs: address " + address + ", quantity " + quantity + "\n");
            }
            
            @Override
            public void onReadSingleHoldingResgister(int address) {
            	onReadMultipleHoldingRegisters(address, 1);
            }
            
            @Override
            public void onReadMultipleHoldingRegisters(int address, int quantity) {
            	System.out.print("onReadMultipleHoldingRegisters: address " + address + ", quantity " + quantity + "\n");
            }
            
            @Override
            public void onReadMultipleInputRegisters(int address, int quantity){
            	System.out.print("onReadMultipleInputRegisters: address " + address + ", quantity " + quantity + "\n");
            }
            
            @Override
            public void onWriteToSingleCoil(int address, boolean value) {
				System.out.print("onWriteToSingleCoil: address " + address + ", value " + value + "\n");;
            }
            
            @Override
            public void onWriteToMultipleCoils(int address, int quantity, boolean[] values) {
                System.out.print("onWriteToMultipleCoils: address " + address + ", quantity " + quantity + "\n");
            }

            @Override
            public void onWriteToSingleHoldingRegister(int address, int value) {
                System.out.print("onWriteToSingleHoldingRegister: address " + address + ", value " + value + "\n");
            }

            @Override
            public void onWriteToMultipleHoldingRegisters(int address, int quantity, int[] values) {
                System.out.print("onWriteToMultipleHoldingRegisters: address " + address + ", quantity " + quantity + "\n");
            }
        });
        slave.setDataHolder(dh);
        slave.getDataHolder().setCoils(hc);
        slave.getDataHolder().setDiscreteInputs(hcd);
        slave.getDataHolder().setHoldingRegisters(hr);
        slave.getDataHolder().setInputRegisters(hri);
	}
	
	public interface ModbusEventListener {
        void onReadMultipleCoils(int address, int quantity);
        
        void onReadMultipeDiscreteInputs(int address, int quantity);
        
        void onReadSingleHoldingResgister(int address);
        
        void onReadMultipleHoldingRegisters(int address, int quantity);
        
        void onReadMultipleInputRegisters(int address, int quantity);

        void onWriteToSingleCoil(int address, boolean value);
        
        void onWriteToMultipleCoils(int address, int quantity, boolean[] values);

        void onWriteToSingleHoldingRegister(int address, int value);

        void onWriteToMultipleHoldingRegisters(int address, int quantity, int[] values);
    }

    public static class MyOwnDataHolder extends DataHolder {

        final List<ModbusEventListener> modbusEventListenerList = new ArrayList<ModbusEventListener>();

        public MyOwnDataHolder() {
        }

        public void addEventListener(ModbusEventListener listener) {
            modbusEventListenerList.add(listener);
        }

        public boolean removeEventListener(ModbusEventListener listener) {
            return modbusEventListenerList.remove(listener);
        }

        @Override
        public void writeHoldingRegister(int offset, int value) throws IllegalDataAddressException, IllegalDataValueException {
            for (ModbusEventListener l : modbusEventListenerList) {
                l.onWriteToSingleHoldingRegister(offset, value);
            }
            super.writeHoldingRegister(offset, value);
        }

        @Override
        public void writeHoldingRegisterRange(int offset, int[] range) throws IllegalDataAddressException, IllegalDataValueException {
            for (ModbusEventListener l : modbusEventListenerList) {
                l.onWriteToMultipleHoldingRegisters(offset, range.length, range);
            }
            super.writeHoldingRegisterRange(offset, range);
        }

        @Override
        public void writeCoil(int offset, boolean value) throws IllegalDataAddressException, IllegalDataValueException {
            for (ModbusEventListener l : modbusEventListenerList) {
                l.onWriteToSingleCoil(offset, value);
            }
            super.writeCoil(offset, value);
        }

        @Override
        public void writeCoilRange(int offset, boolean[] range) throws IllegalDataAddressException, IllegalDataValueException {
            for (ModbusEventListener l : modbusEventListenerList) {
                l.onWriteToMultipleCoils(offset, range.length, range);
            }
            super.writeCoilRange(offset, range);
        }
        
        @Override
        public boolean[] readCoilRange(int offset, int quantity) throws IllegalDataAddressException, IllegalDataValueException{
        	for (ModbusEventListener l : modbusEventListenerList) {
                l.onReadMultipleCoils(offset, quantity);
            }
        	boolean[] values = super.readCoilRange(offset, quantity);
            return values;
        }
        
        @Override
        public boolean[] readDiscreteInputRange(int offset, int quantity) throws IllegalDataAddressException, IllegalDataValueException {
        	for (ModbusEventListener l : modbusEventListenerList) {
                l.onReadMultipeDiscreteInputs(offset, quantity);
            }
        	boolean[] values = super.readDiscreteInputRange(offset, quantity);
        	return values;
        }
        
        @Override
        public int readHoldingRegister(int offset) throws IllegalDataAddressException {
        	for (ModbusEventListener l : modbusEventListenerList) {
                l.onReadSingleHoldingResgister(offset);
            }
        	int value = super.readHoldingRegister(offset);
        	return value;
        }
        
        @Override
        public int[] readHoldingRegisterRange(int offset, int quantity) throws IllegalDataAddressException {
        	for (ModbusEventListener l : modbusEventListenerList) {
                l.onReadMultipleHoldingRegisters(offset, quantity);
            }
        	int[] values = super.readHoldingRegisterRange(offset, quantity);
        	return values;
        }
        
        @Override
        public int[] readInputRegisterRange(int offset, int quantity) throws IllegalDataAddressException {
        	for (ModbusEventListener l : modbusEventListenerList) {
                l.onReadMultipleInputRegisters(offset, quantity);
            }
        	int[] values = super.readInputRegisterRange(offset, quantity);
        	return values;
        }
    }
	
}

