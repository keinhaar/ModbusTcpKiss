package de.exware.modbus.solaredge;
import java.io.IOException;

import de.exware.modbus.AbstractModbusTCPClient;
import de.exware.modbus.ModbusDataHandler;
import de.exware.modbus.ModbusException;

public class SolarEdgeBatteryHandler implements ModbusDataHandler
{
    private String manufacturer;
    private String model;
    private String serialnumber;
    private int modbusId;
    private String version;
    private float loadedPercent;
    private double ratedEnergy;
    private double maxEnergy;
    private double availableEnergy;
    private double maxTemp;
    private double currentVoltage;
    private double currentAmpere;
    private double lifeTimeExport;
    private double lifeTimeImport;
    private int status;
    private float currentPower;
    
    public SolarEdgeBatteryHandler()
    {
    }
    
    @Override
    public void afterRead()
    {
        System.out.println("");
        System.out.println("Battery Data");
        System.out.println("Manufacturer: " + manufacturer);
        System.out.println("Model: " + model);
        System.out.println("Serialnumber: " + serialnumber);
        System.out.println("Version: " + version);
        System.out.println("Percent: " + loadedPercent);
        System.out.println("Rated Energy: " + ratedEnergy);
        System.out.println("Max Energy: " + maxEnergy);
        System.out.println("Max Temparature: " + maxTemp);
        System.out.println("Current Voltage: " + currentVoltage);
        System.out.println("Current Ampere: " + currentAmpere);
        System.out.println("Current Power: " + currentPower);
        System.out.println("Available Energy: " + availableEnergy);
        System.out.println("Lifetime Export: " + lifeTimeExport);
        System.out.println("Lifetime Import: " + lifeTimeImport);
        System.out.println("Status: " + status);
    }
    
    @Override
    public void readData(AbstractModbusTCPClient client) throws ModbusException, IOException
    {
        manufacturer = client.readString(57600, 16);
        serialnumber = client.readString(57648, 16);
        model = client.readString(57616, 16);
        version = client.readString(57632, 16);
        modbusId = client.readUInt16(57664);
        loadedPercent = client.readFloat32(62852);
        ratedEnergy = client.readFloat32(57666);
        maxEnergy = client.readFloat32(57726);
        maxTemp = client.readFloat32(57710);
        currentVoltage = client.readFloat32(57712);
        currentAmpere = client.readFloat32(57714);
        currentPower = client.readFloat32(57716);
        availableEnergy = client.readFloat32(57728);
        lifeTimeExport = client.readUInt64(57718).doubleValue();
        lifeTimeImport = client.readUInt64(57722).doubleValue();
        status = (int) client.readUInt32(57734);
    }

    public long getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
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

    public double getRatedEnergy()
    {
        return ratedEnergy;
    }

    public void setRatedEnergy(double ratedEnergy)
    {
        this.ratedEnergy = ratedEnergy;
    }

    public double getMaxEnergy()
    {
        return maxEnergy;
    }

    public void setMaxEnergy(double maxEnergy)
    {
        this.maxEnergy = maxEnergy;
    }

    public double getMaxTemp()
    {
        return maxTemp;
    }

    public void setMaxTemp(double maxTemp)
    {
        this.maxTemp = maxTemp;
    }

    public double getCurrentVoltage()
    {
        return currentVoltage;
    }

    public void setCurrentVoltage(double currentVoltage)
    {
        this.currentVoltage = currentVoltage;
    }

    public double getCurrentAmpere()
    {
        return currentAmpere;
    }

    public void setCurrentAmpere(double currentAmpere)
    {
        this.currentAmpere = currentAmpere;
    }

    public double getAvailableEnergy()
    {
        return availableEnergy;
    }

    public void setAvailableEnergy(double availableEnergy)
    {
        this.availableEnergy = availableEnergy;
    }

    public double getLifeTimeExport()
    {
        return lifeTimeExport;
    }

    public void setLifeTimeExport(double lifeTimeExport)
    {
        this.lifeTimeExport = lifeTimeExport;
    }

    public double getLifeTimeImport()
    {
        return lifeTimeImport;
    }

    public void setLifeTimeImport(double lifeTimeImport)
    {
        this.lifeTimeImport = lifeTimeImport;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public int getModbusId()
    {
        return modbusId;
    }

    public void setModbusId(int modbusId)
    {
        this.modbusId = modbusId;
    }

    public float getLoadedPercent()
    {
        return loadedPercent;
    }

    public void setLoadedPercent(float loadedPercent)
    {
        this.loadedPercent = loadedPercent;
    }

    public float getCurrentPower()
    {
        return currentPower;
    }

    public void setCurrentPower(float currentPower)
    {
        this.currentPower = currentPower;
    }
}
