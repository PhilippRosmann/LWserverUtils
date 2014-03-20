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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Philipp
 */
public class Crypto
{
    private final Cipher decrypt;
    private final Cipher encrypt;

    public Crypto(String passphrase) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException
    {
        MessageDigest digest = MessageDigest.getInstance("SHA");
        digest.update(passphrase.getBytes());
        SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
        
        decrypt = Cipher.getInstance("AES/ECB/PKCS5Padding");
        decrypt.init(Cipher.DECRYPT_MODE, key);
        
        encrypt = Cipher.getInstance("AES/ECB/PKCS5Padding");
        encrypt.init(Cipher.ENCRYPT_MODE, key);
    }
    
    public void send(OutputStream os, byte[] data) throws IOException, IllegalBlockSizeException, BadPaddingException
    {
        ConnectionUtils.send(os, encrypt.doFinal(data));
    }
    
    public byte[] receive(InputStream is) throws SocketTimeoutException, IOException, IllegalBlockSizeException, BadPaddingException 
    {
        return decrypt.doFinal(ConnectionUtils.receive(is));
    }
    
    
}
