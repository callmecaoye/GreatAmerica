package com.promeets.android.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Shashank Shekhar on 26-01-2017.
 */

public final class EncryptUtil {
    public static final String SHA1 = "SHA-1";
    public static final String SHA128 = "SHA-128";
    public static final String SHA256 = "SHA-256";
    public static final String SHA512 = "SHA-512";


    public String getEncryptedText(String aStrText,String hashTechnique) {

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(hashTechnique);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        md.update(aStrText.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        System.out.println("Hex format : " + sb.toString());

        //convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
        for (int i=0;i<byteData.length;i++) {
            String hex=Integer.toHexString(0xff & byteData[i]);
            if(hex.length()==1) hexString.append('0');
            hexString.append(hex);
        }
        System.out.println("Hex format : " + hexString.toString());

        return hexString.toString();
    }
}
