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

/**
 * A Proxy Class that allows multiple connections to an TCP implementation, that otherwise would only
 * accept one connection like SolarEdge inverters. Requests are serialized. This will only work, 
 * with small requests, and only if one request produces exactly one answer.
 */
public class TCPProxy
{
    private ServerSocket serverSocket;
    
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private String host;
    private int port;
    private int listeningPort;
    private boolean run = false;
    private List<Connection> connections = new ArrayList<>();
    
    public static void main(String[] args) throws IOException
    {
        ServerSocket dummy = new ServerSocket(10001);
        TCPProxy proxy = new TCPProxy("localhost", 10001, 10000);
        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    Socket socket = dummy.accept();
                    InputStream in = socket.getInputStream();
                    OutputStream out = socket.getOutputStream();
                    while(true)
                    {
                        byte[] buf = new byte[2048];
                        int c = in.read(buf);
                        System.out.println(new String(buf, 0, c));
                        out.write("HALLO WELT".getBytes());
                        out.flush();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        };
        t.start();
        proxy.connect();
        Socket s1 = new Socket("localhost", 10000);
        InputStream i1 = s1.getInputStream();
        OutputStream o1 = s1.getOutputStream();

        Socket s2 = new Socket("localhost", 10000);
        InputStream i2 = s2.getInputStream();
        OutputStream o2 = s2.getOutputStream();
        
        o2.write("Request 2".getBytes());
        o2.flush();
        o1.write("Request 1".getBytes());
        o1.flush();
        while(true)
        {
            if(i2.available() > 0)
            {
                byte[] buf = new byte[2048];
                int c = i2.read(buf);
                System.out.println(new String(buf, 0, c));
            }
            if(i1.available() > 0)
            {
                byte[] buf = new byte[2048];
                int c = i1.read(buf);
                System.out.println(new String(buf, 0, c));
            }
        }
    }
    
    public TCPProxy(String host, int port, int listeningPort)
    {
        this.host = host;
        this.port = port;
        this.listeningPort = listeningPort;
    }
    
    public void connect() throws UnknownHostException, IOException
    {
        socket = new Socket(host, port);
        in = socket.getInputStream();
        out = new BufferedOutputStream(socket.getOutputStream());
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
                    catch (IOException ste)
                    {
                        ste.printStackTrace();
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
                    in.close();
                    out.close();
                    socket.close();
                    socket = null;
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        };
        Thread t = new Thread(runner,"TCPProxy");
        t.start();
        t = new Thread("TCPProxyClientHandler")
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
                handleClient(connections.get(i));
            }
        }
    }

    private void handleClient(Connection con) throws IOException
    {
        if(con.in.available() > 0)
        {
            byte[] buf = new byte[2048];
            int pos = 0;
            int count = 0;
            int ret = 1;
            while(count == 0 || con.in.available() > 0)
            {
                ret = con.in.read(buf, pos, buf.length - pos);
                count += ret;
            }
            out.write(buf, 0, count);
            out.flush();
            
            pos = 0;
            count = 0;
            ret = 1;
            while(count == 0 || in.available() > 0)
            {
                ret = in.read(buf, pos, buf.length - pos);
                count += ret;
            }
            con.out.write(buf, 0, count);
            con.out.flush();
        }
    }

    class Connection
    {
        Socket socket;
        InputStream in;
        OutputStream out;
    }

    public boolean isStopped()
    {
        return socket == null;
    }
    
    public void stop()
    {
        run = false;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public int getListeningPort()
    {
        return listeningPort;
    }

    public void setListeningPort(int listeningPort)
    {
        this.listeningPort = listeningPort;
    }
}
