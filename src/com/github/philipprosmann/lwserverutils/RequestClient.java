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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Philipp
 */
public class RequestClient
{
    private static final Logger LOG = Logger.getLogger(RequestClient.class.getName());
    private final InetAddress target;
    private final int port;
    private final Crypto crypto;

    public static synchronized void setLogLevel(Level newLevel) throws SecurityException
    {
        LOG.setLevel(newLevel);
    }

    private RequestClient(String server, int port, String key) throws UnknownHostException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException
    {
        target = Inet4Address.getByName(server);
        this.port = port;
        crypto = new Crypto(key);
    }
    
    public static RequestClient getInstance(String server, int port, String key) throws UnknownHostException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException
    {
        return new RequestClient(server, port,key);
    }
    
    public static byte[] sendRequest(String server, int port,byte[] request, String key) throws UnknownHostException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        RequestClient client = getInstance(server, port, key);
        return client.sendBytesReceiveResponse(request);
    }
     
    public byte[] sendBytesReceiveResponse(byte[] data) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        Socket so = new Socket(target, port);
        so.setSoTimeout(2000);
        LOG.log(Level.INFO, 
            "Connected to {0}", 
            so.getInetAddress().getCanonicalHostName());
        
        LOG.log(Level.INFO, "Sending {0} Bytes", data.length);        
        crypto.send(so.getOutputStream(), data);
        so.shutdownOutput();
        
        byte[] response = crypto.receive(so.getInputStream());
        
        
        LOG.log(Level.INFO, "received {0} Bytes", response.length);
        return response;
    }
}
