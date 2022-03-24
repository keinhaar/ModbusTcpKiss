package de.exware.modbus.solaredge;
import java.io.IOException;

import de.exware.modbus.AbstractModbusTCPClient;
import de.exware.modbus.ModbusDataHandler;
import de.exware.modbus.ModbusException;

public class SolarEdgeBattery implements ModbusDataHandler
{
    private float percent;
    private String manufacturer;
    
    public SolarEdgeBattery()
    {
    }
    
    @Override
    public void afterRead()
    {
        System.out.println("Battery Data");
        System.out.println("Manufacturer: " + manufacturer);
        System.out.println("Percent: " + percent);
    }
    
    @Override
    public void readData(AbstractModbusTCPClient client) throws ModbusException, IOException
    {
        manufacturer = client.readString(62720, 16);
//        percent = client.readFloat(62852, 2);
//        
//        data = client.ReadHoldingRegisters(40099, 1);
//        dcVoltageScale = data[0];
//        data = client.ReadHoldingRegisters(40098, 1);
//        dcVoltage = data[0] * Math.pow(10,dcVoltageScale);
//        data = client.ReadHoldingRegisters(40099, 1);
//        dcAmpereScale = data[0];
//        data = client.ReadHoldingRegisters(40098, 1);
//        dcAmpere = data[0] * Math.pow(10,dcAmpereScale);
//
//        data = client.ReadHoldingRegisters(40084, 1);
//        acPowerScale = data[0];
//        data = client.ReadHoldingRegisters(40083, 1);
//        acPower = data[0] * Math.pow(10,acPowerScale);
//        data = client.ReadHoldingRegisters(40086, 1);
//        acFrequenceScale = data[0];
//        data = client.ReadHoldingRegisters(40085, 1);
//        acFrequence = data[0] * Math.pow(10,acFrequenceScale);
//
//        data = client.ReadHoldingRegisters(40095, 1);
//        lifeTimePowerScale = data[0];
//        data = client.ReadHoldingRegisters(40093, 2);
//        lifeTimePower = convertToLong(data[0], data[1]) * Math.pow(10,lifeTimePowerScale);    
//
//        //BATTERY
//        data = client.ReadHoldingRegisters(62720, 16);
//        batteryManufacturer = convertToString(data);
//        data = client.ReadHoldingRegisters(62852, 2);
//        batteryPercent = client.ConvertRegistersToFloat(data, RegisterOrder.HighLow);    
    }
}
