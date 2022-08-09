package com.example.encryptionalgorithm.util;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
/**
 * @author wengly
 * @date 2022-08-09 14:56:44
 * @describe AES+CBC偏移向量加密和解密，加密用的Key 可以用26个字母和数字组成，最好不要用保留字符，虽然不会错，至于怎么裁决，个人看情况而定，此处使用AES-128-CBC加密模式，key需要为16位。
 */
@Component
@Slf4j
public class AESCBCUtils {
    // 加密
    public String Encrypt(String sSrc, String sKey,String cbc_pkcs5_padding,String vipara) throws Exception {
        if (sKey == null) {
            log.error("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            log.error("Key长度不是16位");
            return null;
        }
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance(cbc_pkcs5_padding);//"算法/模式/补码方式"
        IvParameterSpec iv = new IvParameterSpec(vipara.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes());

        return new BASE64Encoder().encode(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }

    // 解密
    public String Decrypt(String sSrc, String sKey,String cbc_pkcs5_padding,String vipara) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                log.error("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                log.error("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance(cbc_pkcs5_padding);
            IvParameterSpec iv = new IvParameterSpec(vipara.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);
                return originalString;
            } catch (Exception e) {
                log.error(e.toString());
                return null;
            }
        } catch (Exception ex) {
            log.error(ex.toString());
            return null;
        }
    }
}
