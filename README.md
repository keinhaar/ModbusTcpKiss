# ModbusTcpKiss
A ModbusTCP implementation that follows the KISS principles.

## Usage
Its very simple to use. Just set the Hostname and Port and connect. Nothing more.
<pre>
  AbstractModbusTCPClient client = new ModbusTCPClient("SERVERNAME", 1502);
  client.connect();
  String manufacturer = client.readString(40004, 16);  //Example for SolarEdge Inverter
  int status = client.readUInt16(40107);
</pre>

### Read data
There are predefined methods to decode data like
- int readInt16(int address)
- int readUInt16(int address)
- int readInt32(int address)
- long readUInt32(int address)
- BigInteger readUInt64(int address)
- float readFloat16(int address)
- float readFloat32(int address)
These methods don't need a length information, they just read the correct number of registers. These methods return the register contents as data type, that is sufficiant for the requested information.

If you have special needs, you could always use the basic method:
- byte[] readRegister(int address, int numberOfRegisters)
You will get a plain, undecoded byte[] of size "numberOfRegsiters * 2", because each register has 16 bit.

### Write data
There are predefined methods which will encode data like
- void writeUInt16(int address, int value)

If you have special needs, you could always use the basic method:
- void WriteRegisters(int address, byte[] data)
The data array must be a multiple of 2, because each register has 16 bit. It's up to you to encode the data as required
by your device.


## Additional Features
We added an optional Proxy, that allows us to make more then 1 connection to a device, that otherwise would only accept on connection (like SolarEdge Inverters). The ModbusTCPProxyClient is used the same way as the ModbusTCPClient decribed above. The only difference is, that it will connect to a ModbusTCPProxyServer, which must be startet first.
<pre>
  ModbusTCPClient client = new ModbusTCPClient("SRVERNAME", 1502);
  ModbusTCPProxyServer proxy = new ModbusTCPProxyServer(client, 1502);
  proxy.connect();
</pre>
