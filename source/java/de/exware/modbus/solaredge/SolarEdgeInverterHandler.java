package de.exware.modbus.solaredge;
import java.io.IOException;

import de.exware.modbus.AbstractModbusTCPClient;
import de.exware.modbus.ModbusDataHandler;
import de.exware.modbus.ModbusException;

public class SolarEdgeInverterHandler implements ModbusDataHandler
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
    private int dcPowerScale;
    private double dcPower;
    private int acFrequenceScale;
    private double acFrequence;
    private int lifeTimePowerScale;
    private double lifeTimePower;
    private int activePowerLimit;
    private double cosPhi;
    
    public SolarEdgeInverterHandler()
    {
    }
    
    @Override
    public void afterRead()
    {
        System.out.println("");
        System.out.println("Inverter Data");
        System.out.println("Manufacturer: " + manufacturer);
        System.out.println("Model: " + model);
        System.out.println("Version: " + version);
        System.out.println("Serialnumber: " + serialnumber);
        System.out.println("ModbusId: " + modbusId);
        System.out.println("Status: " + status);
        System.out.println("Power: " + acPower / 1000 + " KW");
        System.out.println("PowerScale: " + acPowerScale);
        System.out.println("Lifetime Power: " + lifeTimePower / 1000 + " KWh");
        System.out.println("Frequence: " + acFrequence);
        System.out.println("DC Voltage: " + dcVoltage);
        System.out.println("DC Voltage Scale: " + dcVoltageScale);
        System.out.println("DC Ampere: " + dcAmpere);
        System.out.println("DC Power: " + dcPower / 1000 + " KW");
        System.out.println("Active Power Limit: " + activePowerLimit);
        System.out.println("CosPhi: " + cosPhi);
    }
    
    @Override
    public void readData(AbstractModbusTCPClient client) throws ModbusException, IOException
    {
        byte[] buf = client.readRegister(40004, 64);
        manufacturer = new String(buf, 0, 32).trim(); //client.readString(40004, 16);
        model = new String(buf, 32, 32).trim(); //client.readString(40020, 16);
        version = new String(buf, 80, 16).trim(); //client.readString(40044, 8);
        serialnumber = new String(buf, 96, 32).trim(); //client.readString(40052, 16);
        modbusId = client.readUInt16(40068);
        status = InverterStatus.getStatus(client.readUInt16(40107));
        dcVoltageScale = client.readInt16(40099);
        dcVoltage = readScaledUInt16(client, 40098);
        dcAmpereScale = client.readInt16(40097);
        dcAmpere = readScaledUInt16(client, 40096);
        dcPowerScale = client.readInt16(40101);
        dcPower = readScaledInt16(client, 40100);
        acPowerScale = client.readInt16(40084);
        acPower = readScaledInt16(client, 40083);
        acFrequenceScale = client.readInt16(40086);
        acFrequence = readScaledUInt16(client, 40085);
        lifeTimePowerScale = client.readInt16(40095);
        lifeTimePower = readScaledUInt32(client, 40093);
        activePowerLimit = client.readUInt16(61441);
        cosPhi = client.readFloat32(61442);
    }
    
    private double readScaledUInt16(AbstractModbusTCPClient client, int baseadress) throws ModbusException, IOException
    {
        byte[] buf = client.readRegister(baseadress, 2);
        int value = client.convert2UInt16(buf[1], buf[0]);
        int scale = client.convert2Int16(buf[3], buf[2]);
        double ret = value * Math.pow( 10, scale);
        return ret;
    }
    
    private double readScaledInt16(AbstractModbusTCPClient client, int baseadress) throws ModbusException, IOException
    {
        byte[] buf = client.readRegister(baseadress, 2);
        int value = client.convert2Int16(buf[1], buf[0]);
        int scale = client.convert2Int16(buf[3], buf[2]);
        double ret = value * Math.pow( 10, scale);
        return ret;
    }
    
    private double readScaledUInt32(AbstractModbusTCPClient client, int baseadress) throws ModbusException, IOException
    {
        byte[] buf = client.readRegister(baseadress, 3);
        long value = client.convert2UInt32(buf[3], buf[2], buf[1], buf[0]);
        int scale = client.convert2UInt16(buf[5], buf[4]);
        double ret = value * Math.pow( 10, scale);
        return ret;
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

    public double getDcPower()
    {
        return dcPower;
    }

    public void setDcPower(double dcPower)
    {
        this.dcPower = dcPower;
    }

    public double getActivePowerLimit()
    {
        return activePowerLimit;
    }
}
