package com.muhardin.endy.belajar.enkripsi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import org.apache.commons.codec.binary.Base64;

public class AESEncryptionDemo {
    public static void main(String[] args) throws Exception {
        // lokasi file
        
        String filePlain = "lyric.txt";
        String fileEncrypted = "lyric-enc.txt";
        String fileKey = "key.txt";
        String fileIv = "iv.txt";
        String fileHmacKey = "hmac-key.txt";
        String fileHmac = "hmac.txt";
        
        String algoritmaKey = "AES";
        String algoritmaEnkripsi = "AES/CBC/PKCS5Padding";
        String algoritmaHmac = "HmacSHA512";
        
        // load file
        File plain = new File("target"+File.separator+"classes"+File.separator+filePlain);
        File keyFile = new File("target"+File.separator+"classes"+File.separator+fileKey);
        File ivFile = new File("target"+File.separator+"classes"+File.separator+fileIv);
        File encryptedFile = new File("target"+File.separator+"classes"+File.separator+fileEncrypted);
        File hmacKeyFile = new File("target"+File.separator+"classes"+File.separator+fileHmacKey);
        File hmacFile = new File("target"+File.separator+"classes"+File.separator+fileHmac);
        
        // generate random key
        KeyGenerator keygen = KeyGenerator.getInstance(algoritmaKey);
        keygen.init(256);
        SecretKey key = keygen.generateKey();
        Files.write(keyFile.toPath(), Base64.encodeBase64String(key.getEncoded()).getBytes(), StandardOpenOption.CREATE);
        
        // inisialisasi AES
        Cipher cipher = Cipher.getInstance(algoritmaEnkripsi);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        // generate IV
        byte[] iv = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
        Files.write(ivFile.toPath(), Base64.encodeBase64String(iv).getBytes(), StandardOpenOption.CREATE);
        
        // output terenkripsi
        CipherOutputStream writer = 
                    new CipherOutputStream(
                        new FileOutputStream(encryptedFile), cipher);
        
        // enkripsi isi file
        FileReader reader = new FileReader(plain);
        int data;
        while((data = reader.read()) != -1){
            System.out.print((char)data);
            writer.write(data);
        }
        
        reader.close();
        writer.close();
        
        // HMAC untuk memastikan integritas data
        KeyGenerator keygenHmac = KeyGenerator.getInstance(algoritmaHmac);
        keygenHmac.init(256);
        
        // key untuk HMAC
        SecretKey keyHmac = keygenHmac.generateKey();
        Files.write(hmacKeyFile.toPath(), 
                Base64.encodeBase64String(keyHmac.getEncoded()).getBytes(), 
                StandardOpenOption.CREATE);
        
        // buat HMAC
        Mac hmacGenerator = Mac.getInstance(algoritmaHmac);
        hmacGenerator.init(keyHmac);
        byte[] hmac = hmacGenerator.doFinal(Files.readAllBytes(encryptedFile.toPath()));
        Files.write(hmacFile.toPath(), hmac, StandardOpenOption.CREATE);
    }
}
