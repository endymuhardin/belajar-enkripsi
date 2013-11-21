package com.muhardin.endy.belajar.enkripsi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
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
        
        String algoritmaKey = "AES";
        String algoritmaEnkripsi = "AES/CBC/PKCS5Padding";
        
        // load file
        File plain = new File("target"+File.separator+"classes"+File.separator+filePlain);
        File keyFile = new File("target"+File.separator+"classes"+File.separator+fileKey);
        File ivFile = new File("target"+File.separator+"classes"+File.separator+fileIv);
        File encryptedFile = new File("target"+File.separator+"classes"+File.separator+fileEncrypted);
        
        // generate random key
        KeyGenerator keygen = KeyGenerator.getInstance(algoritmaKey);
        keygen.init(256);
        SecretKey key = keygen.generateKey();
        FileWriter keyWriter = new FileWriter(keyFile);
        keyWriter.write(Base64.encodeBase64String(key.getEncoded()));
        keyWriter.close();
        
        // inisialisasi AES
        Cipher cipher = Cipher.getInstance(algoritmaEnkripsi);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        // generate IV
        byte[] iv = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
        FileWriter ivWriter = new FileWriter(ivFile);
        ivWriter.write(Base64.encodeBase64String(iv));
        ivWriter.close();
        
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
    }
}
