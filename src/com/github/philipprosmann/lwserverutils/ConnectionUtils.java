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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Philipp
 */
public class ConnectionUtils
{
    private static final Logger LOG = Logger.getLogger(ConnectionUtils.class.getName());
    
    public static void send(OutputStream os, byte[] data) throws IOException
    {
        os.write(data);
        os.flush();
    }
    
    public static byte[] receive(InputStream is) throws SocketTimeoutException,IOException
    {
        byte[] buffer = new byte[1000];
        byte[] resp;
        int read;
        
        read = is.read(buffer); 
        
        if(read==1000)
            LOG.warning("Receive-Buffer is full");
        
        if(read == -1)
            resp = new byte[0];
        else
        {
            resp = new byte[read];
            System.arraycopy(buffer, 0, resp, 0, read);
        }
        
        return resp;
    }
    
    
}
