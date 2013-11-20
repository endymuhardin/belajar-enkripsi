package com.muhardin.endy.belajar.enkripsi;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.apache.commons.codec.binary.Base64;

public class KeyGeneratorDemo {

    public static void main(String[] args) throws Exception {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(256);
        SecretKey key = keygen.generateKey();

        System.out.println("Generated Key : " + Base64.encodeBase64String(key.getEncoded()));
    }
}
