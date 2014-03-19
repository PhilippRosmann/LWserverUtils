/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.philipprosmann.lwserverutils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Philipp
 */
public abstract class HandleRequestServer
{
    private static final Logger LOG = Logger.getLogger(HandleRequestServer.class.getName());
    
    private int port;
    private WaitForRequestsThread serverTh = null;
    private ExecutorService handleRequestWorkers = null;

    public static synchronized void setLogLevel(Level newLevel) throws SecurityException
    {
        LOG.setLevel(newLevel);
    }
    
    public HandleRequestServer(int port)
    {
        this.port = port;
    }
    
    public void startService() throws IllegalStateException, IOException
    {
        if(isRunning())
            throw new IllegalStateException("Server has already started");
        
        LOG.log(Level.INFO, "Server starting on Port {0}", String.format("%d", port));
        handleRequestWorkers = Executors.newFixedThreadPool(4);
        serverTh = new WaitForRequestsThread(port);
        serverTh.start();
    }
    
    public void stopService()
    {
        if(!isRunning())
            throw new IllegalStateException("Server is already stopped");
        
        serverTh.interrupt();
        try{serverTh.join(2000);} catch (InterruptedException ignored){}
        
        handleRequestWorkers.shutdown();
        try{handleRequestWorkers.awaitTermination(2, TimeUnit.SECONDS);}catch (InterruptedException ignored){}
        
        if(serverTh.isAlive() || !handleRequestWorkers.isTerminated())
        {
            LOG.warning("Server couldn´t exit safely");
            
            if(serverTh.isAlive())
                serverTh.stop();
            
            if(!handleRequestWorkers.isTerminated())
                handleRequestWorkers.shutdownNow();
        }
        else
        {
            LOG.info("Server exited safely");
        }
        
        
        handleRequestWorkers = null;
        serverTh = null;
    }
    
    private class WaitForRequestsThread extends Thread
    {
        private final ServerSocket serversocket;

        public WaitForRequestsThread(int port) throws IOException
        {
            serversocket = new ServerSocket(port);
            serversocket.setSoTimeout(500);
        }
        
        @Override
        public void run()
        {
            while(!isInterrupted())
            {
                try
                {
                    Socket client = serversocket.accept();
                    LOG.log(Level.INFO, "Incoming Request from {0}", 
                        client.getInetAddress().getCanonicalHostName());
                    
                    handleRequestWorkers.execute(new HandleRequestWorker(client));
                }
                catch (SocketTimeoutException ignored){}
                catch (IOException ex)
                {
                    LOG.severe(ex.getMessage());
                }
            }
            
            try{serversocket.close();}
            catch (IOException ex){LOG.warning(ex.getMessage());}
        }
        
    }
    
    protected abstract byte[] handleRequestReturnResponse(byte[] request);
    
    private class HandleRequestWorker implements Runnable
    {
        private final Socket client;

        public HandleRequestWorker(Socket client)
        {
            this.client = client;
        }
        

        @Override
        public void run()
        {
            try
            {
                byte[] request = ConnectionUtils.receive(client);
                
                LOG.log(Level.INFO, 
                    "{1}: Received {0} Bytes", 
                    new Object[]{request.length, 
                        client.getInetAddress().getCanonicalHostName()});
                
                byte[] response = handleRequestReturnResponse(request);
                
                LOG.log(Level.INFO, 
                    "{1}: Sending {0} Bytes", 
                    new Object[]{response.length, 
                        client.getInetAddress().getCanonicalHostName()});
                
                ConnectionUtils.send(client, response);
            }
            catch (SocketTimeoutException ex)
            {
                LOG.log(Level.WARNING, 
                    "Connection to {0} timed out", 
                    client.getInetAddress().getCanonicalHostName());
            }
            catch (IOException ex)
            {
                LOG.severe(ex.getMessage());
            }
        }
        
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        if(isRunning())
            throw new IllegalStateException("Server is running, cannot change Port");
        this.port = port;
    }
    
    public boolean isRunning()
    {
        return serverTh != null;
    }
    
}
