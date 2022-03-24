package de.exware.modbus;

import java.io.IOException;

/**
 * A Handler to perform system specific read operations.
 * The Handler should know the Hardware. 
 * @author martin
 *
 */
public interface ModbusDataHandler
{
    /**
     * Reads all the Data the Handler knows about.
     * @param client
     * @throws IOException
     * @throws ModbusException
     */
    void readData(AbstractModbusTCPClient client) throws IOException, ModbusException;
    
    /**
     * Will be called after all handlers have performed the readData method.
     */
    void afterRead();
}
