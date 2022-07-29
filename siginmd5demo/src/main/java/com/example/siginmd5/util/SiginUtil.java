package com.example.siginmd5.util;

import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

/**
 * @author wengly
 * @date 2022-06-27 16:09:10
 */
@Component
@Slf4j
public class SiginUtil {
    public static Map<String, String> appMap = Maps.newConcurrentMap();
    /**
     * 分别保存生成的公私钥对
     * key:appId，value:公私钥对
     */
    public static Map<String, Map<String, String>> appKeyPair = Maps.newConcurrentMap();
    //公钥
    public static final String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0Z+ZIn6y0+lucx9a2/EDuhpeA7VrXWuJsebQkFxkM3Jcd+YEMikunEVREoVLeZOqaS7Cp4F1oz/Sq6/YxEti02eAp+0oBq5RNTb8H77ddRAU6A3TnzvjRNGM1BTDeKHq4lOJVFIdsr0/pmZf6ZvU03ZlmkmJD7r1B7NhkX92NzPhlet3DG2nG5jawOdsQWYtg7m81DG3YXZoPC3uDCOKUO/NFHIiLLa2jr8WbB5MEyUCDlKVZNxMJMdFWkBkNYIXR0VTAAcTq60Lf8SWtlN0RVXPqOPG2KQkSVREy4pidRUH9ikmLye+ASa/5tiyhhTOiqtKO/Wrk8k+/y1TrakpLwIDAQAB";
    //私钥
    public static final String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDRn5kifrLT6W5zH1rb8QO6Gl4DtWtda4mx5tCQXGQzclx35gQyKS6cRVEShUt5k6ppLsKngXWjP9Krr9jES2LTZ4Cn7SgGrlE1Nvwfvt11EBToDdOfO+NE0YzUFMN4oeriU4lUUh2yvT+mZl/pm9TTdmWaSYkPuvUHs2GRf3Y3M+GV63cMbacbmNrA52xBZi2DubzUMbdhdmg8Le4MI4pQ780UciIstraOvxZsHkwTJQIOUpVk3Ewkx0VaQGQ1ghdHRVMABxOrrQt/xJa2U3RFVc+o48bYpCRJVETLimJ1FQf2KSYvJ74BJr/m2LKGFM6Kq0o79auTyT7/LVOtqSkvAgMBAAECggEBAKCqmd21jG0d0pkMJEDFhtzCTuK6z3LH9L7VMXAkUGyaL8N6Gic0J1SBUHVIkyO5CXka7PkkIb0hMuCCpDs3qKRuBU8wV2hffUCzRyUxzl1mGRAPYO8AIN5jWnRHAddZC0WzT2PAmKChqsJoXwUXelUlvxQ1Xoce/aWMWLEDJ+GAUAR/NI2Sb58U7CDVhIvp/rDOK0CqP1pk5Ri9es4B4qFcbd2oRQcjTXyaA3pCw24ZxJ0s7YRNCPNC7uvaocGs3aRZ8k2LPC+lC8Gej6y3UraJqceeCUFB9K6PCu5EMGda5NLqmC2voU605GwRkOABiTHAx3QGJaTAOvb49S4i/IECgYEA8DB1D/uu9CVFSl0NeT2anYt82LhSoLU4644+17j5ipSdPyIQTBuUBRKQCL7lYdR5AJqmFmr5hONupP/gu5nYh2Irbh2j0gw36mk779+s0bUfNst7WURZ6k6WszEa4mfsL3K7kR1lZd7U+VyyTtXYWELhAvW4PbaUcMBpJ+3+ykECgYEA32wPsDsyzOfW60GHTu6pmgQMC9ywKljR9aP6RWniuMgTGO06nZDI4/3PP/eRc8Nct2vJbh2qTaHIKpxYLdPrP6kL78h8G6eoE5LtKubH/Tb1B5V7i6WXYcuhWjAc2klU9bJYCLTcqZLdCRbfkcHomU6QP8dLXqGdM+8HDlTbt28CgYA+/UZF6chJCNkyFlMpP4mj1WfpYfVxZfVGeShr5Hu05yKR4voJK5ZW+jZBxOZdEJ6nFOeHq4hCXG6w4NXr/P23C3y+RRGoTUIAvJZWYv9sg6Zq9VQk5fL6qp+E4NTfGUfsbZdZCC+GVrjdqHyuhhBVnTxrTUuDC7XEGAG70gNmwQKBgAqSVWe0VWIGsk+taRaeCl5kRKGnDpriWwTxfl0cdoLhzK2IRBbCjA7lqpHPSA2fN9rh+cUBGFbWCd+iSDXa6i239P2ikeTJGRXcBObbxqi/mfaC5zajXjddaEQZNLRLdf6uMqYu0KYhcd4FPAzsjPH8Gq054+IVaZfUfpP6Be93AoGAWn2Dm6olKDnzD2+Vmetz6j6H9D5k0zpeRwpazfhMhPslp5b8BLtNpYm430v/OzkXBasNZFRFUFK8qH7KcY1uxwyaTqlC/M36VyDukjCH3niqyEGxo1O9/QohfBPnEXnOhKPH7UC8mCV/2U+OrMkHBK0KAaPFhEgyM+y1iy5s7kc=";
    static {
        try {
            // appId、appSecret生成规则，依据之前介绍过的方式，保证全局唯一即可
            String appId = "123456";
            String appSecret = "654321";
            appMap.put(appId, appSecret);
            // 根据appId生成公私钥对
            Map<String, String> keyMap = Maps.newHashMap();
            keyMap.put("publicKey", publicKey);
            keyMap.put("privateKey", privateKey);
            appKeyPair.put(appId, keyMap);
        }catch (Exception e){
            log.error(e.getMessage());
        }

    }
    /**
     * 公钥验签
     *
     * @param dataStr
     * @param publicKeyStr
     * @param signStr
     * @return
     * @throws Exception
     */
    public boolean rsaVerifySignature(String dataStr, String publicKeyStr, String signStr) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyStr));
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(dataStr.getBytes());
        return signature.verify(Base64.getDecoder().decode(signStr));
    }
    public String getAppSecret(String appId) {
        return String.valueOf(appMap.get(appId));
    }
    /**
     * 公钥验签
     *
     * @param data
     * @return
     * @throws Exception
     */
    @SneakyThrows
    public String getSHA256Str(String data) {
        MessageDigest messageDigest;
        messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hash = messageDigest.digest(data.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(hash);
    }
}
