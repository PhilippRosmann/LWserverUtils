/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.philipprosmann.lwserverutils;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Philipp
 */
public class RequestClient
{
    private static final Logger LOG = Logger.getLogger(RequestClient.class.getName());
    private final InetAddress target;
    private final int port;

    public static synchronized void setLogLevel(Level newLevel) throws SecurityException
    {
        LOG.setLevel(newLevel);
    }

    private RequestClient(String server, int port) throws UnknownHostException
    {
        target = Inet4Address.getByName(server);
        this.port = port;
    }
    
    public static RequestClient getInstance(String server, int port) throws UnknownHostException
    {
        return new RequestClient(server, port);
    }
    
    public static byte[] sendRequest(String server, int port,byte[] request) throws UnknownHostException, IOException
    {
        RequestClient client = getInstance(server, port);
        return client.sendBytesReceiveResponse(request);
    }
     
    public byte[] sendBytesReceiveResponse(byte[] data) throws IOException
    {
        Socket so = new Socket(target, port);
        so.setSoTimeout(2000);
        LOG.log(Level.INFO, 
            "Connected to {0}", 
            so.getInetAddress().getCanonicalHostName());
        
        LOG.log(Level.INFO, "Sending {0} Bytes", data.length);        
        ConnectionUtils.send(so, data);
        so.shutdownOutput();
        
        byte[] response = new byte[0];
        
        response = ConnectionUtils.receive(so);
        
        
        LOG.log(Level.INFO, "received {0} Bytes", response.length);
        return response;
    }
}
