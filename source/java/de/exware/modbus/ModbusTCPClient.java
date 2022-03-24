package de.exware.modbus;

import java.io.IOException;
import java.math.BigInteger;

/**
 * A simple Client to read Modbus TCP Data.
 * @author martin
 *
 */
public class ModbusTCPClient extends AbstractModbusTCPClient
{
    private int unitIdentifier = 1;
    
    public ModbusTCPClient(String host, int port)
    {
        super(host, port);
    }
    
    @Override
    public byte[] readRegister(int transactionId, int address, int count) throws ModbusException, IOException
    {
        checkRange(0, 65535, address);
        checkRange(0, 100, count);
        byte[] ret = null;
        byte[] header = createHeader(transactionId, 6, FunctionCode.HoldingRegister.getCode());
        byte[] data = new byte[4];
        data[0] = (byte) (address >> 8);
        data[1] = (byte) (address);
        data[2] = (byte) (count >> 8);
        data[3] = (byte) (count);
        out.write(header);
        out.write(data);
        out.flush();
        byte[] buf = new byte[1000];
        in.read(buf);        
        checkError(buf);
        
        ret = new byte[buf[8]];
        for(int i=0;i<buf[8];i++)
        {
            ret[i] = buf[9+i];
        }
        return ret;
    }

    private void checkError(byte[] buf) throws ModbusException
    {
        if((buf[7] & 0xFF) > 0x80)
        {
            if(buf[8] == 1)
            {
                throw new ModbusException("Invalid Function Code", null);
            }
            else if(buf[8] == 2)
            {
                throw new ModbusException("Invalid Data Address", null);
            }
            else if(buf[8] == 3)
            {
                throw new ModbusException("Invalid Data Value", null);
            }
            else
            {
                throw new ModbusException("Unknown Error Code: " + (int)buf[8], null);
            }
        }
    }

    private byte[] createHeader(int transactionid, int length, int functionCode) throws ModbusException
    {
        checkRange(0, 65535, transactionid, length);
        checkRange(0, 247, unitIdentifier);
        checkRange(0, 0x7F, functionCode);
        byte[] header = new byte[8];
        header[0] = (byte) (transactionid >> 8);
        header[1] = (byte) (transactionid);
        header[2] = 0;
        header[3] = 0;
        header[4] = (byte) (length >> 8);
        header[5] = (byte) (length);
        header[6] = (byte) (unitIdentifier);
        header[7] = (byte) (functionCode);
        return header;
    }
    
    /**
     * 
     * @param val
     * @throws ModbusException
     */
    private void checkRange(int min, int max, int ... values) throws ModbusException
    {
        for(int i=0;i<values.length;i++)
        {
            int val = values[i];
            if(val < min || val > max)
            {
                throw new ModbusException("Illegal value: " + val, null);
            }
        }
    }
    
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

    public void close() throws IOException
    {
        in.close();
        out.close();
        socket.close();
    }
}

