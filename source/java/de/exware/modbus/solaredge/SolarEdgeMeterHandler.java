package de.exware.modbus.solaredge;
import java.io.IOException;

import de.exware.modbus.AbstractModbusTCPClient;
import de.exware.modbus.ModbusDataHandler;
import de.exware.modbus.ModbusException;

public class SolarEdgeMeterHandler implements ModbusDataHandler
{
    private int meterNumber;
    private String manufacturer;
    private String model;
    private String serialnumber;
    private int modbusId;
    private String version;
    private double totalPower;
    
    public SolarEdgeMeterHandler(int meterNumber)
    {
        this.meterNumber = meterNumber;
    }
    
    @Override
    public void afterRead()
    {
        System.out.println("");
        System.out.println("Meter Data " + meterNumber);
        System.out.println("Manufacturer: " + manufacturer);
        System.out.println("Model: " + model);
        System.out.println("Serialnumber: " + serialnumber);
        System.out.println("Version: " + version);
        System.out.println("Total Power: " + totalPower);
    }
    
    @Override
    public void readData(AbstractModbusTCPClient client) throws ModbusException, IOException
    {
        int offset = (meterNumber - 1) * 174;
        manufacturer = client.readString(40123 + offset, 16);
        serialnumber = client.readString(40171 + offset, 16);
        model = client.readString(40139 + offset, 16);
        version = client.readString(40163 + offset, 8);
        modbusId = client.readUInt16(40187 + offset);
        int totalScale = client.readInt16(40210);
        totalPower = client.readInt16(40206) * Math.pow(10,totalScale);
    }

    public int getMeterNumber()
    {
        return meterNumber;
    }

    public void setMeterNumber(int meterNumber)
    {
        this.meterNumber = meterNumber;
    }

    public String getManufacturer()
    {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer)
    {
        this.manufacturer = manufacturer;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public String getSerialnumber()
    {
        return serialnumber;
    }

    public void setSerialnumber(String serialnumber)
    {
        this.serialnumber = serialnumber;
    }

    public int getModbusId()
    {
        return modbusId;
    }

    public void setModbusId(int modbusId)
    {
        this.modbusId = modbusId;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public double getTotalPower()
    {
        return totalPower;
    }

    public void setTotalPower(double totalPower)
    {
        this.totalPower = totalPower;
    }
}
