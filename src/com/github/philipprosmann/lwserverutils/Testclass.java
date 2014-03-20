/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.philipprosmann.lwserverutils;

/**
 *
 * @author Philipp
 */
public class Testclass
{
    public static void main(String[] args)
    {
        final int port = 45689;
        
        try
        {
            HandleRequestServer server = new HandleRequestServer(port, "Test1234")
            {
                @Override
                protected byte[] handleRequestReturnResponse(byte[] request)
                {
                    return (new String(request)+" Can´t touch thiz\n").getBytes();
                }
            };
            
            server.startService();
            
            try
            {
                System.out.println(new String(
                    RequestClient.sendRequest("localhost", port, "Test:\n".getBytes(), "Test1234")));
                
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
            }
            
            server.stopService();
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }
}
