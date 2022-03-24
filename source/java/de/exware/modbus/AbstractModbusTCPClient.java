package de.exware.modbus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;

/**
 * A simple Client to read Modbus TCP Data.
 * @author martin
 *
 */
abstract public class AbstractModbusTCPClient
{
    protected Socket socket;
    protected InputStream in;
    protected OutputStream out;
    private String host;
    private int port;
    
    public AbstractModbusTCPClient(String host, int port)
    {
        this.host = host;
        this.port = port;
    }
    
    public void connect() throws IOException
    {
        socket = new Socket(host, port);
        in = new BufferedInputStream(socket.getInputStream());
        out = new BufferedOutputStream(socket.getOutputStream());
    }
    
    /**
     * Read one 16 bit value from the given addess as unsigned int.
     * @param address
     * @return
     * @throws ModbusException
     * @throws IOException
     */
    public int readUInt16(int address) throws ModbusException, IOException
    {
        byte[] data = readRegister(address, 1);
        int i = ((data[0] & 0xff) << 8) + (data[1] & 0xff);
        return i;
    }
    
    /**
     * Read one 32 bit value from the given addess as unsigned int.
     * @param address
     * @return
     * @throws ModbusException
     * @throws IOException
     */
    public long readUInt32(int address) throws ModbusException, IOException
    {
        byte[] data = readRegister(address, 2);
        long i = ((data[0] & 0xff) << 24) 
            + ((data[1] & 0xff) << 16) 
            + ((data[2] & 0xff) << 8) 
            + (data[3] & 0xff);
        return i;
    }
    
    /**
     * Read one 64 bit value from the given addess as unsigned int.
     * @param address
     * @return
     * @throws ModbusException
     * @throws IOException
     */
    public BigInteger readUInt64(int address) throws ModbusException, IOException
    {
        byte[] data = readRegister(address, 4);
        BigInteger bint = BigInteger.ZERO;
        bint = bint.add(BigInteger.valueOf(data[0] & 0xff));
        bint = bint.shiftLeft(8);
        bint = bint.add(BigInteger.valueOf(data[1] & 0xff));
        bint = bint.shiftLeft(8);
        bint = bint.add(BigInteger.valueOf(data[2] & 0xff));
        bint = bint.shiftLeft(8);
        bint = bint.add(BigInteger.valueOf(data[3] & 0xff));
        bint = bint.shiftLeft(8);
        bint = bint.add(BigInteger.valueOf(data[4] & 0xff));
        bint = bint.shiftLeft(8);
        bint = bint.add(BigInteger.valueOf(data[5] & 0xff));
        bint = bint.shiftLeft(8);
        bint = bint.add(BigInteger.valueOf(data[6] & 0xff));
        bint = bint.shiftLeft(8);
        bint = bint.add(BigInteger.valueOf(data[7] & 0xff));
        return bint;
    }
    
    /**
     * Read one 16 bit value from the given addess as signed int.
     * @param address
     * @return
     * @throws ModbusException
     * @throws IOException
     */
    public int readInt16(int address) throws ModbusException, IOException
    {
        byte[] data = readRegister(address, 1);
        return new BigInteger(data).intValue();
    }
    
    /**
     * Read one 32 bit value from the given addess.
     * @param address
     * @return
     * @throws ModbusException
     * @throws IOException
     */
    public int readInt32(int address) throws ModbusException, IOException
    {
        byte[] data = readRegister(address, 2);
        return new BigInteger(data).intValue();
    }
    
    /**
     * Read one 16 bit value from the given addess.
     * @param address
     * @return
     * @throws ModbusException
     * @throws IOException
     */
    public float readFloat16(int address) throws ModbusException, IOException
    {
        byte[] data = readRegister(address, 1);
        int fint = ((data[0] & 0xff) << 8) + (data[1] & 0xff);
        return Float.intBitsToFloat(fint);
    }
    
    /**
     * Read one 32 bit value from the given addess.
     * @param address
     * @return
     * @throws ModbusException
     * @throws IOException
     */
    public float readFloat32(int address) throws ModbusException, IOException
    {
        byte[] data = readRegister(address, 2);
        int high = ((data[0] & 0xff) << 8) + (data[1] & 0xff);
        int low = ((data[2] & 0xff) << 8) + (data[3] & 0xff);
        
        int fint = ((high & 0xffff) << 16) + (low & 0xffff);
        return Float.intBitsToFloat(fint);
    }
    
    /**
     * read the given addresses and convert the contained bytes to String
     * @param address
     * @param count
     * @return
     * @throws ModbusException
     * @throws IOException
     */
    public String readString(int address, int count) throws ModbusException, IOException
    {
        byte[] data = readRegister(address, count);
        return new String(data).trim();
    }
    
    public byte[] readRegister(int address, int count) throws ModbusException, IOException
    {
        int tid = (int) (Math.random() * 65535);
        return readRegister(tid, address, count);
    }

    abstract protected byte[] readRegister(int tid, int address, int count) throws ModbusException, IOException;

    public static void main(String[] args) throws IOException, ModbusException
    {
        byte[] buf = new byte[2];
        buf[0] = (byte) 0xff;
        buf[1] = (byte) 0xff;
        System.out.println(buf[0]);
        int i = new BigInteger(buf).intValue();
        System.out.println(i);
        int i2 = ((buf[0] & 0xff) << 8) + (buf[1] & 0xff);
        System.out.println(i2);
        
        int high = 17045;
        int low = -24163;
        int fint = ((high & 0xffff) << 16) + (low & 0xffff);
        float f = Float.intBitsToFloat(fint);
        System.out.println(f);
        
        byte[] data = new byte[8];
        data[0] = (byte) 0xff;
        data[6] = (byte) 0xff;
        data[7] = (byte) 0xff;
        BigInteger bint = BigInteger.ZERO;
        bint = bint.add(BigInteger.valueOf(data[0] & 0xff));
        bint = bint.shiftLeft(8);
        bint = bint.add(BigInteger.valueOf(data[1] & 0xff));
        bint = bint.shiftLeft(8);
        bint = bint.add(BigInteger.valueOf(data[2] & 0xff));
        bint = bint.shiftLeft(8);
        bint = bint.add(BigInteger.valueOf(data[3] & 0xff));
        bint = bint.shiftLeft(8);
        bint = bint.add(BigInteger.valueOf(data[4] & 0xff));
        bint = bint.shiftLeft(8);
        bint = bint.add(BigInteger.valueOf(data[5] & 0xff));
        bint = bint.shiftLeft(8);
        bint = bint.add(BigInteger.valueOf(data[6] & 0xff));
        bint = bint.shiftLeft(8);
        bint = bint.add(BigInteger.valueOf(data[7] & 0xff));
        System.out.println(bint);

    }
        
}

enum FunctionCode
{
    HoldingRegister(3);

    private int code;

    FunctionCode(int code)
    {
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }
}
