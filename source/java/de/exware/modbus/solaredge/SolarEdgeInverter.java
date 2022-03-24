package de.exware.modbus.solaredge;
import java.io.IOException;

import de.exware.modbus.AbstractModbusTCPClient;
import de.exware.modbus.ModbusDataHandler;
import de.exware.modbus.ModbusException;

public class SolarEdgeInverter implements ModbusDataHandler
{
    private String manufacturer;
    private String model;
    private String version;
    private String serialnumber;
    private int modbusId;
    private InverterStatus status;
    private int dcVoltageScale;
    private double dcVoltage;
    private int dcAmpereScale;
    private double dcAmpere;
    private int acPowerScale;
    private double acPower;
    private int acFrequenceScale;
    private double acFrequence;
    private int lifeTimePowerScale;
    private double lifeTimePower;
    
    public SolarEdgeInverter()
    {
    }
    
    @Override
    public void afterRead()
    {
        System.out.println("Inverter Data");
        System.out.println("Manufacturer: " + manufacturer);
        System.out.println("Model: " + model);
        System.out.println("Version: " + version);
        System.out.println("Serialnumber: " + serialnumber);
        System.out.println("ModbusId: " + modbusId);
        System.out.println("Status: " + status);
        System.out.println("Power: " + acPower / 1000 + " KW");
        System.out.println("Lifetime Power: " + lifeTimePower / 1000 + " KWh");
        System.out.println("Frequence: " + acFrequence);
        System.out.println("DC Voltage: " + dcVoltage);
        System.out.println("DC Ampere: " + dcAmpere);
    }
    
    @Override
    public void readData(AbstractModbusTCPClient client) throws ModbusException, IOException
    {
        manufacturer = client.readString(40004, 16);
        model = client.readString(40020, 16);
        version = client.readString(40044, 8);
        serialnumber = client.readString(40052, 16);
        modbusId = client.readUInt16(40068);
        status = InverterStatus.getStatus(client.readUInt16(40107));
        dcVoltageScale = client.readInt16(40099);
        dcVoltage = client.readUInt16(40098) * Math.pow(10,dcVoltageScale);
        dcAmpereScale = client.readInt16(40097);
        dcAmpere = client.readInt16(40096) * Math.pow(10,dcAmpereScale);
        acPowerScale = client.readInt16(40084);
        acPower = client.readInt16(40083) * Math.pow(10,acPowerScale);
        acFrequenceScale = client.readInt16(40086);
        acFrequence = client.readUInt16(40085) * Math.pow(10,acFrequenceScale);
        lifeTimePowerScale = client.readInt16(40095);
        lifeTimePower = client.readInt32(40093) * Math.pow(10,lifeTimePowerScale);
    }
    
    public enum InverterStatus
    {
        Unknown
        , Off
        , Sleeping
        , WakeUp
        , ON_Power
        , Production
        , ShuttingDown
        , Fault
        , Maintenance
        ;
        
        public static InverterStatus getStatus(int ordinal)
        {
            InverterStatus status = Unknown;
            for(InverterStatus st : values())
            {
                if(st.ordinal() == ordinal)
                {
                    status = st;
                    break;
                }
            }
            return status;
        }
    }

    public String getManufacturer()
    {
        return manufacturer;
    }

    public String getModel()
    {
        return model;
    }

    public String getVersion()
    {
        return version;
    }

    public String getSerialnumber()
    {
        return serialnumber;
    }

    public int getModbusId()
    {
        return modbusId;
    }

    public InverterStatus getStatus()
    {
        return status;
    }

    public int getDcVoltageScale()
    {
        return dcVoltageScale;
    }

    public double getDcVoltage()
    {
        return dcVoltage;
    }

    public int getDcAmpereScale()
    {
        return dcAmpereScale;
    }

    public double getDcAmpere()
    {
        return dcAmpere;
    }

    public int getAcPowerScale()
    {
        return acPowerScale;
    }

    public double getAcPower()
    {
        return acPower;
    }

    public int getAcFrequenceScale()
    {
        return acFrequenceScale;
    }

    public double getAcFrequence()
    {
        return acFrequence;
    }

    public int getLifeTimePowerScale()
    {
        return lifeTimePowerScale;
    }

    public double getLifeTimePower()
    {
        return lifeTimePower;
    }
}
