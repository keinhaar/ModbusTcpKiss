package de.exware.modbus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import de.exware.modbus.solaredge.SolarEdgeBatteryHandler;

/**
 * This class forwards Modbus Request to another ModbusTCP implementation.
 * This is useful if the Modbus Server only accepts a single connection.
 * @author martin
 *
 */
public class ModbusTCPProxy
{
    private ModbusTCPClient modbus;
    private ServerSocket serverSocket;
    private int listeningPort;
    private List<Connection> connections = new ArrayList<>();
    private boolean run = false;
   
    public ModbusTCPProxy(ModbusTCPClient modbus, int listeningPort)
    {
        this.setModbusClient(modbus);
        this.listeningPort = listeningPort;
    }

    public void connect() throws UnknownHostException, IOException
    {
        modbus.connect();
        serverSocket = new ServerSocket(listeningPort, 100, InetAddress.getByName("0.0.0.0"));
        serverSocket.setSoTimeout(5000);
        Runnable runner = new Runnable()
        {
            @Override
            public void run()
            {
                run = true;
                while (run)
                {
                    try
                    {
                        Socket socket = serverSocket.accept();
                        if (socket != null)
                        {
                            socket.setSoTimeout(10000);
                            Connection con = new Connection();
                            con.socket = socket;
                            con.in = new BufferedInputStream(socket.getInputStream());
                            con.out = new BufferedOutputStream(socket.getOutputStream());
                            synchronized (connections)
                            {
                                connections.add(con);
                            }
                        }
                    }
                    catch (SocketTimeoutException ste)
                    {
                    }
                    catch (Exception ste)
                    {
                        ste.printStackTrace();
                        try
                        {
                            modbus.close();
                            modbus.connect();
                        }
                        catch(Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                }
                try
                {
                    synchronized (connections)
                    {
                        for(int i=0;i<connections.size();i++)
                        {
                            Connection con = connections.get(i);                        
                            con.in.close();
                            con.out.close();
                            con.socket.close();
                        }
                    }
                    modbus.close();
                    serverSocket.close();
                    serverSocket = null;
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        };
        Thread t = new Thread(runner,"ModbusProxy");
        t.start();
        t = new Thread("ModbusProxyHandler")
        {
            @Override
            public void run()
            {
                while(run)
                {
                    try
                    {
                        handleClients();
                        Thread.sleep(100);
                    }
                    catch (IOException e1)
                    {
                        e1.printStackTrace();
                        try
                        {
                            modbus.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        try
                        {
                            modbus.connect();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
            }
        };
        t.start();
    }
    
    protected void handleClients() throws IOException
    {
        synchronized (connections)
        {
            for(int i=0;i<connections.size();i++)
            {
                Connection con = connections.get(i);
                try
                {
                    handleClient(con);
                }
                catch(Exception ex)
                {
                    connections.remove(i);
                    throw ex;
                }
            }
        }
    }

    private void handleClient(Connection con) throws IOException
    {
        if(con.in.available() > 0)
        {
            byte[] header = con.readRaw(8);
            int transactionId = AbstractModbusTCPClient.convert2UInt16(header[1], header[0]);
            int length = AbstractModbusTCPClient.convert2UInt16(header[5], header[4]);
            int unit = header[6];
            int functionCode = header[7];
            if (functionCode == FunctionCode.HoldingRegister.getCode())
            {
                try
                {
                    byte[] data = con.readRaw(4);
                    int adress = AbstractModbusTCPClient.convert2UInt16(data[1], data[0]);
                    int registerCount = AbstractModbusTCPClient.convert2UInt16(data[3], data[2]);
                    getModbusClient().setUnitIdentifier(unit);
                    data = getModbusClient().readRegisterRaw(transactionId, adress, registerCount);
                    con.out.write(data);
                    con.out.flush();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    con.out.write(-1);
                    con.out.flush();
                    throw new IOException("", e);
                }
            }
        }
    }
    
    public boolean isStopped()
    {
        return serverSocket == null;
    }
    
    public void stop()
    {
        run = false;
    }
    
    public ModbusTCPClient getModbusClient()
    {
        return modbus;
    }

    public void setModbusClient(ModbusTCPClient modbus)
    {
        this.modbus = modbus;
    }

    class Connection
    {
        Socket socket;
        InputStream in;
        OutputStream out;

        private String read(int length) throws IOException
        {
            byte[] buf = readRaw(length);
            return new String(buf);
        }
        
        private byte[] readRaw(int length) throws IOException
        {
            byte[] buf = new byte[length];
            int pos = 0;
            int count = 0;
            while(count != buf.length)
            {
                int ret = in.read(buf, pos, buf.length - pos);
                count += ret;
            }
            return buf;
        }
    }
    
    public static void main(String[] args) throws UnknownHostException, IOException
    {
        ModbusTCPClient client = new ModbusTCPClient("192.168.100.60", 1502);
        ModbusTCPProxy proxy = new ModbusTCPProxy(client, 5502);
        proxy.connect();
        ModbusTCPClient proxyClient = new ModbusTCPClient("localhost", 5502);
        proxyClient.connect();
        IntervalReader reader = new IntervalReader(proxyClient);
        reader.addHandler(new SolarEdgeBatteryHandler());
        reader.start();
    }
}

