/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.philipprosmann.lwserverutils;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 *
 * @author Philipp
 */
public class Testclass
{
    public static void main(String[] args)
    {
        final int port = 45689;
        
        HandleRequestServer server = new HandleRequestServer(port)
        {

            @Override
            protected byte[] handleRequestReturnResponse(byte[] request)
            {
                return (new String(request) + " ADD Something").getBytes();
            }
        };
        
        
        try
        {
            server.startService();
        } catch (IllegalStateException | IOException ex)
        {
            System.out.println(ex.getMessage());
        }
        
        
        try
        {
            System.out.println(new String(RequestClient.sendRequest("localhost", port, "Hallo Test".getBytes())));
        }
        catch (UnknownHostException ex)
        {
            System.out.println(ex.getMessage());
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }
        
        
        server.stopService();
    }
}
