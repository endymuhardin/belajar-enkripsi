package com.muhardin.endy.belajar.enkripsi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class AESDecryptionDemo {
    public static void main(String[] args) throws Exception{
        // lokasi file
        
        String filePlain = "lyric.txt";
        String fileEncrypted = "lyric-enc.txt";
        String fileDecrypted = "lyric-dec.txt";
        String fileKey = "key.txt";
        String fileIv = "iv.txt";
        String fileHmacKey = "hmac-key.txt";
        String fileHmac = "hmac.txt";
        
        String algoritmaKey = "AES";
        String algoritmaEnkripsi = "AES/CBC/PKCS5Padding";
        String algoritmaHmac = "HmacSHA512";
        
        // load file
        File plainFile = new File("target"+File.separator+"classes"+File.separator+filePlain);
        File keyFile = new File("target"+File.separator+"classes"+File.separator+fileKey);
        File ivFile = new File("target"+File.separator+"classes"+File.separator+fileIv);
        File encryptedFile = new File("target"+File.separator+"classes"+File.separator+fileEncrypted);
        File decryptedFile = new File("target"+File.separator+"classes"+File.separator+fileDecrypted);
        File hmacKeyFile = new File("target"+File.separator+"classes"+File.separator+fileHmacKey);
        File hmacFile = new File("target"+File.separator+"classes"+File.separator+fileHmac);
        
        // sebelum dekripsi, cek dulu HMAC apakah datanya otentik
        List<String> hmacKeys = Files.readAllLines(hmacKeyFile.toPath(), Charset.forName("UTF-8"));
        if(hmacKeys.isEmpty()) {
            throw new IllegalStateException("No mac key");
        }
        String hmacKey = hmacKeys.get(0);
        SecretKeySpec hmacKeySpec = new SecretKeySpec(Base64.decodeBase64(hmacKey), algoritmaHmac);
        Mac hmacGenerator = Mac.getInstance(algoritmaHmac);
        hmacGenerator.init(hmacKeySpec);
        
        // generate hmac dari data yang diterima
        byte[] hmacDiterima = hmacGenerator.doFinal(Files.readAllBytes(encryptedFile.toPath()));
        byte[] hmacSeharusnya = Files.readAllBytes(hmacFile.toPath());
        boolean cocok = Arrays.equals(hmacDiterima, hmacSeharusnya);
        System.out.println(cocok ? "HMAC Cocok" : "HMAC Tidak Cocok");
        if(!cocok){
            throw new IllegalStateException("Data tidak otentik, mungkin sudah dioprek orang ditengah jalan");
        }
        
        // load key
        BufferedReader keyReader = new BufferedReader(new FileReader(keyFile));
        String key = keyReader.readLine();
        if(key == null){
            throw new IllegalStateException("File key invalid");
        }
        keyReader.close();
        System.out.println("Key : "+key);
        SecretKeySpec keySpec = new SecretKeySpec(Base64.decodeBase64(key), algoritmaKey);
        
        // load IV
        BufferedReader ivReader = new BufferedReader(new FileReader(ivFile));
        String iv = ivReader.readLine();
        if(iv == null){
            throw new IllegalStateException("File IV invalid");
        }
        keyReader.close();
        System.out.println("IV : "+iv);
        IvParameterSpec ivSpec = new IvParameterSpec(Base64.decodeBase64(iv));
        
        // inisialisasi AES
        Cipher cipher = Cipher.getInstance(algoritmaEnkripsi);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        
        // output terdekripsi
        FileWriter output = new FileWriter(decryptedFile);
        
        // dekripsi isi file
        CipherInputStream cis = new CipherInputStream(new FileInputStream(encryptedFile), cipher);
        int data;
        while((data = cis.read()) != -1){
            output.write(data);
        }
        cis.close();
        output.close();
        
        // bandingkan apakah hasil dekripsi sama dengan file asli
        String md5sumAsli = DigestUtils.md5Hex(new FileInputStream(plainFile));
        String md5sumHasilDekripsi = DigestUtils.md5Hex(new FileInputStream(decryptedFile));
        System.out.println("MD5 Sum File Asli           : "+md5sumAsli);
        System.out.println("MD5 Sum File Hasil Dekripsi : "+md5sumHasilDekripsi);
        System.out.println(md5sumAsli.equals(md5sumHasilDekripsi) ? "Cocok" : "Tidak cocok");
    }
}
