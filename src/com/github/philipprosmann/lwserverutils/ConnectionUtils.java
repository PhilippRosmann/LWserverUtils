/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.philipprosmann.lwserverutils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Philipp
 */
public class ConnectionUtils
{
    private static final Logger LOG = Logger.getLogger(ConnectionUtils.class.getName());
    
    
    public static void send(Socket so, byte[] data) throws IOException
    {
        OutputStream os = so.getOutputStream();
        os.write(data);
        os.flush();
    }
    
    public static byte[] receive(Socket so) throws SocketTimeoutException,IOException
    {
        InputStream is = so.getInputStream();
        byte[] buffer = new byte[1000];
        byte[] resp;
        int read;
        
        read = is.read(buffer); 
        
        if(read==1000)
            LOG.warning("Receive-Buffer is full");
        
        resp = new byte[read];
        System.arraycopy(buffer, 0, resp, 0, read);
        return resp;
    }
}
