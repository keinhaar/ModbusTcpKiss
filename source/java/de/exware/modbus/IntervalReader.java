package de.exware.modbus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.exware.modbus.solaredge.SolarEdgeBatteryHandler;
import de.exware.modbus.solaredge.SolarEdgeInverterHandler;
import de.exware.modbus.solaredge.SolarEdgeMeterHandler;

/**
 * Reads Modbus data in a background Thread by calling the registered Handlers one after the other.
 * 
 * @author martin
 */
public class IntervalReader
{
    private int interval = 10;
    private AbstractModbusTCPClient client;
    private List<ModbusDataHandler> handlers = new ArrayList<>();
    private TimerTask runner;

    public IntervalReader(AbstractModbusTCPClient client)
    {
        this.client = client;
    }

    public IntervalReader(AbstractModbusTCPClient client, int interval)
    {
        this.client = client;
        this.interval = interval;
    }

    public static void main(String[] args) throws IOException, ModbusException
    {
        AbstractModbusTCPClient client = new ModbusTCPProxyClient("server", 1502);
        IntervalReader reader = new IntervalReader(client);
        reader.addHandler(new SolarEdgeInverterHandler());
        reader.addHandler(new SolarEdgeBatteryHandler());
        reader.addHandler(new SolarEdgeMeterHandler(1));
        reader.start();
    }

    public void addHandler(ModbusDataHandler handler)
    {
        handlers.add(handler);
    }

    public void start()
    {
        Timer timer = new Timer();
        try
        {
            client.connect();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        runner = new TimerTask()
        {
            @Override
            public void run() 
            {
                try
                {
                    for(int i=0;i<handlers.size();i++)
                    {
                        handlers.get(i).readData(client);
                    }
                    for(int i=0;i<handlers.size();i++)
                    {
                        handlers.get(i).afterRead();
                    }
                }
                catch (Exception e)
                {
                    try
                    {
                        client.connect();
                    }
                    catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            };
        };
        int delay = 11 - new Date().getSeconds() % interval;
        timer.schedule(runner, delay * 1000, interval * 1000);
    }
    
    public void stop()
    {
        if(runner != null)
        {
            runner.cancel();
        }
    }
}
