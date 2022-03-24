package de.exware.modbus;

import java.io.IOException;

public class ModbusTCPProxyClient extends AbstractModbusTCPClient
{
    public ModbusTCPProxyClient(String host, int port)
    {
        super(host, port);
    }

    public static void main(String[] args) throws ModbusException, IOException
    {
        ModbusTCPProxyClient client = new ModbusTCPProxyClient("server", 1502);
        client.connect();
        System.out.println(client.readString(40004, 16));
    }
    
    @Override
    protected byte[] readRegister(int tid, int address, int count) throws ModbusException, IOException
    {
        byte[] buf;
        out.write(ModbusTCPProxyServer.READ_REGISTER_COMMAND.getBytes());
        write(tid, 5);
        write(address, 5);
        write(count, 5);
        out.flush();
        int ret = in.read();
        if(ret != 0)
        {
            throw new ModbusException("Error on ModbusTCPProxyServer", null);
        }
        else
        {
            buf = read(4);
            String dataLength = new String(buf);
            int len = Integer.parseInt(dataLength);
            buf = read(len);
        }
        return buf;
    }
 
    public void close() throws IOException
    {
        if(out != null)
        {
            out.write(ModbusTCPProxyServer.CLOSE_COMMAND.getBytes());
            out.flush();
            out.close();
            in.close();
            socket.close();
        }
    }
    
    private void write(int num, int length) throws IOException
    {
        String dl = "" + num;
        while(dl.length() < length)
        {
            dl = "0" + dl;
        }
        out.write(dl.getBytes());
    }

    private byte[] read(int length) throws IOException
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
