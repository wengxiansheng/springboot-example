package com.example.encryptionalgorithm.util;

import com.citic.asp.citic.sm2.Base64Util;
import com.citic.asp.citic.sm2.SM2KeyPair;
import com.citic.asp.citic.sm2.SM2Util;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wengly
 * @date 2022-08-09 10:14:08
 * @describe 国密SM2加解密
 */
@Component
@Slf4j
public class SM2Utils {
    //sm2公钥
    private static final String publicKey = "BKFkUTrIwS89xECMn3uOYVAOqXoBejrNaVUY0uyvsFQAlmAv2IlY0CgG6Aa/MwrnYAMVT+dP8vU10T1WFzXBbjE=";
    //sm2私钥
    private static final String privateKey = "BWRvST9x+FWoxGOZlLrU0acifIc+lmzbnE8JXpqLl7o=";
    /**
     * @author wengly
     * @date 2022-08-09 11:11:28
     * @describe SM2加密
     * @param paramStr //待加密字符串
     */
    public String sm2Encryption(String paramStr){
        //sm2加密
        byte[] sm2ParamStr = SM2Util.encrypt(Base64Util.decode(publicKey.getBytes()),paramStr.getBytes());
        //base64转码
        String baseBusiness = Base64Util.encode(sm2ParamStr);
        return baseBusiness;
    }

    /**
     * @author wengly
     * @date 2022-08-09 11:32:13
     * describe SM2解密
     * @param paramStr //待解密字符串
     */
    public String sm2Decrypt(String paramStr){
        //sm2加密
        byte[] sm2ParamStr = SM2Util.decrypt(Base64Util.decode(privateKey.getBytes()),Base64Util.decode(paramStr));
        //base64转码
        String baseBusiness = Base64Util.encode(sm2ParamStr);
        baseBusiness = new String(Base64Util.decode(baseBusiness));
        return baseBusiness;
    }
    /**
     * @author wengly
     * @date 2022-08-09 11:55:36
     * describe SM2生成私钥和公钥
     * @param //null
     */
    public Map<String, byte[]> createCert()
    {
        SM2KeyPair keyPair = SM2Util.generateKeyPair();
        ECPoint publicKey = keyPair.getPublicKey();
        BigInteger privateKey = keyPair.getPrivateKey();

        byte[] publicKeyEncoded = publicKey.getEncoded(false);

        byte[] privateKeyBytes = privateKey.toByteArray();
        Map<String, byte[]> certMap = new HashMap();

        certMap.put("privateKeyMapKey", privateKeyBytes);
        certMap.put("publicKeyMapKey", publicKeyEncoded);

        return certMap;
    }
}
