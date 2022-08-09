package com.example.encryptionalgorithm.util;

import com.citic.asp.citic.sm2.Base64Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import java.math.BigInteger;
import javax.crypto.KeyGenerator;

import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;
/**
 * @author wengly
 * @date 2022-08-09 15:27:42
 * @describe AES+ECB加解密
 */
@Component
@Slf4j
public class AESECBUtils {

    //密钥 (需要前端和后端保持一致)
    private static final String KEY = "a2sj9f8!s=df?rf9";
    //算法
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    /**
     * aes解密
     * @param encrypt   内容
     * @return
     * @throws Exception
     */
    public static String aesDecrypt(String encrypt) {
        try {
            return aesDecrypt(encrypt, KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 将byte[]转为各种进制的字符串
     * @param bytes byte[]
     * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    public static String binary(byte[] bytes, int radix){
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }


    /**
     * base 64 decode
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     * @throws Exception
     */
    public static byte[] base64Decode(String base64Code) throws Exception{
        return StringUtils.isEmpty(base64Code) ? null : new BASE64Decoder().decodeBuffer(base64Code);
    }


    /**
     * AES加密
     * @author wengly
     * @date 2021-08-20
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的byte[]
     * @throws Exception
     */
    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));

        return cipher.doFinal(content.getBytes("utf-8"));
    }

    /**
     * AES解密
     * @author wengly
     * @date 2021-08-20
     * @param encryptBytes 待解密的byte[]
     * @param decryptKey 解密密钥
     * @return 解密后的String
     * @throws Exception
     */
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);

        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }


    /**
     * 将base 64 code AES解密
     * @author wengly
     * @date 2021-08-20
     * @param encryptStr 待解密的base 64 code
     * @param decryptKey 解密密钥
     * @return 解密后的string
     * @throws Exception
     */
    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
    }


    /**
     * 加密
     *
     * @param content 需要加密的内容
     * @param password  加密密码
     * @param keySize 密钥长度16,24,32
     * @return
     */
    public static byte[] encrypt(String content, String password, int keySize) {
        try {
            SecretKeySpec key = new SecretKeySpec(ZeroPadding(password.getBytes(), keySize), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
            byte[] byteContent = content.getBytes();
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**解密
     * @param content  待解密内容
     * @param password 解密密钥
     * @param keySize 密钥长度16,24,32
     * @return
     */
    public static byte[] decrypt(byte[] content, String password, int keySize) {
        try {
            SecretKeySpec key = new SecretKeySpec(ZeroPadding(password.getBytes(), keySize), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**将二进制转换成16进制
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**将16进制转换为二进制
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static byte[] ZeroPadding(byte[] in,Integer blockSize){
        Integer copyLen = in.length;
        if (copyLen > blockSize) {
            copyLen = blockSize;
        }
        byte[] out = new byte[blockSize];
        System.arraycopy(in, 0, out, 0, copyLen);
        return out;
    }


    /**把String的转换成base64码
     * @author wengly
     * @date 2021-08-20
     * @param paramstr
     * @return
     */
    public static String stringToBase64(String paramstr) {
        byte[] bytes = paramstr.getBytes();
        String encode = Base64Util.encode(bytes);
        return encode;
    }


    /**把base64的String码转换成正常显示的字符串
     * @author wengly
     * @date 2021-08-20
     * @param base64
     * @return
     */
    public static String base64ToString(String base64) throws Exception {
        String result;
        try {
            byte[] decode = Base64Util.decode(base64);
            result = new String(decode);
        }catch (Exception e){
            throw new Exception(base64+"：格式错误！");
        }
        return result;
    }
}
