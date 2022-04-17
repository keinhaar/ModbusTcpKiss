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
- void writeRegisters(int address, byte[] data)
The data array must be a multiple of 2, because each register has 16 bit. It's up to you to encode the data as required
by your device.


## Additional Features
We added an optional Proxy, that allows us to make more then 1 connection to a device, that otherwise would only accept on connection (like SolarEdge Inverters). The ModbusTCPClient can be connected to the Proxy like to a real Modbus Server. 
The ModbusTCPProxy must be created and started before connecting.
<pre>
  ModbusTCPClient client = new ModbusTCPClient("SERVERNAME", 1502);
  ModbusTCPProxy proxy = new ModbusTCPProxyServer(client, 5502);
  proxy.connect();
  ModbusTCPClient proxyClient = new ModbusTCPClient("localhost", 5502);
  proxyClient.readString(57600, 16);
</pre>
In this case ```proxyClient.readString(57600, 16);``` and ```client.readString(57600, 16);``` should produce the
same results.