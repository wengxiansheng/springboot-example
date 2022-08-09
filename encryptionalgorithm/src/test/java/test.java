import com.citic.asp.citic.sm2.Base64Util;
import com.citic.asp.citic.sm2.SM2Util;
import com.example.encryptionalgorithm.Sm2AndAesApplication;
import com.example.encryptionalgorithm.util.AESCBCUtils;
import com.example.encryptionalgorithm.util.AESECBUtils;
import com.example.encryptionalgorithm.util.SM2Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/**
 * @author wengly
 * @date 2022-08-09 10:51:16
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Sm2AndAesApplication.class)
@Slf4j
public class test {
    //偏移量
    private static final String VIPARA = "Iccibtwicy_xykcl";   //AES 为16位. DES 为8bytes
    //AES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
    private static final String CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";
    //AES 秘钥
    private static final String priKey = "Iccibtwicy_xykcl";
    //sm2 秘钥
    private static final String privateKey = "APGB5gfYsoPfkrDbglYjquDUgYbJyfFmkF9uR3z1bBDR";
    //编码方式
    private static final String CODE_TYPE = "UTF-8";
    @Autowired
    private SM2Utils sm2Utils;
    @Autowired
    private AESCBCUtils aescbcUtils;
    @Autowired
    private AESECBUtils aesecbUtils;
    /**
     * @author wengly
     * @date 2022-08-09 10:51:16
     * @describe SM2测试
     */
    @Test
    public void sm2test(){
        //加密信息
        String text = "您好";
        //加密
        String entext = sm2Utils.sm2Encryption(text);
        log.info("加密后：" + entext);
        //解密
        log.info("解密后：" + sm2Utils.sm2Decrypt(entext));

    }
    /**
     * @author wengly
     * @date 2022-08-09 10:51:16
     * @describe SM2测试
     */
    @Test
    public void sm2test_O(){
        Map<String,byte[]> createCert = sm2Utils.createCert();
        String putKey = Base64Util.encode(createCert.get("publicKeyMapKey"));
        String priKey = Base64Util.encode(createCert.get("privateKeyMapKey"));
        log.info("公钥：" + putKey);
        log.info("私钥：" + priKey);
        //加密信息
        String text = "您好";
        //加密
        String entext = Base64Util.encode(SM2Util.encrypt(Base64Util.decode(putKey.getBytes()),text.getBytes()));
        log.info("加密后：" + entext);
        //解密
        byte[] sm2star = SM2Util.decrypt(Base64Util.decode(priKey.getBytes()),Base64Util.decode(entext));
        //base64转码
        String baseBusiness = Base64Util.encode(sm2star);
        log.info("解密后：" + new String(Base64Util.decode(baseBusiness)));

    }
    @Test
    public void aesCbctest(){
        // 需要加密的字串
        String cSrc = "userId=022222&token=MDRCNzFGNDZBQjI5Q0FEOEQ4RTUzQzg1OEZBNkZFQjhEMzlDQTlDNDUyOUE2MUIyQzlEQjAwRDAxM0I4QUJBNjUyN0FCREQxNUU4ODc2N0NFRDYxMjdDNzUyQTdDQkI0QkUyQjcwRThBODc0NDYwMzY4RkIwN0U0NTcyM0Q1N0U0Qzk5RjZDRTgwMEY2RTdDMEFDOUM2Mjk1NTI0NUU5NkUxMzEzMTlDOTM2OTI5OEFGMEU4MTFBMDBGNjM2MTA2RjMzMEVDQTNDNDI3MzlFRjgxQzlCRkNENzhBOUFEMEZDOTdFN0VGRDlGRTg2Q0Y1MjMyMzNFMzhENzNGNUVGRTk5";
        log.info("未加密字符串："+cSrc);
        // 加密
        try {
            long lStart = System.currentTimeMillis();
            String enString = aescbcUtils.Encrypt(cSrc, priKey,CBC_PKCS5_PADDING,VIPARA);
            log.info("加密后的字串是：" + enString);

            long lUseTime = System.currentTimeMillis() - lStart;
            log.info("加密耗时：" + lUseTime + "毫秒");
            //String cSrc = "NX1B8eG9wmRCB9YVFdQBa+v8SVhib4lLUW8+9EENIfWtDTXOEMrRXy51D13dmsBw6uqm5aazJJSI/cbjM1rqHT9HB1kdXY0D/19xBLG1CPpLLPlN2v8dE5dWXmIX+nvcEZ+L7FPsTPGycama1uIn8eJdHffuOnlFWFcNfjLXMZqWvtYZjqL8cv9eHhmRkw/VeobZOCGU8n0KffPZ1xcr+SYP46Gq86wxDelcy0Atk8sayQE5tWnRk4kxdhbztR2MjA8JetR7Migx6KwXiyiGp7dIgsCvOGJ8dSLjJAr2cxfThBTZTOR0eCpNmPrpPUkwg8ijZiNU2GWwlT5VBeqTCri0QOxZ27h1SJm6b62ZaT/cHhojCxS5ZXiNapyFWOI0szZDlMJ4BmvcXEV0ML6MQSSWEYS8E0PtJ1MY1OM6wbbpKI9uYgtEpEjUgKQXHzHASllwXQRrH3wHjRAT9kqjB6W+JVsYvniUfHhWEGPfAuQ=";

            // 解密
            lStart = System.currentTimeMillis();
            String DeString = aescbcUtils.Decrypt(enString, priKey,CBC_PKCS5_PADDING,VIPARA);
            log.info("解密后的字串是：" + DeString);
            lUseTime = System.currentTimeMillis() - lStart;
            log.info("解密耗时：" + lUseTime + "毫秒");
        }catch (Exception e){

        }

    }

    @Test
    public void aesEcbtest(){

        log.info("编码格式："+System.getProperty("file.encoding"));
        String content = "test123456";
        String password = "sddmpa123456789";
        //加密
        log.info("加密前：" + content);
        byte[] encryptResult = aesecbUtils.encrypt(content, password,16);
        log.info("加密后：" + aesecbUtils.parseByte2HexStr(encryptResult));


        String passwordStr = aesecbUtils.parseByte2HexStr(encryptResult);

        //解密
//        byte[] decryptResult = decrypt(encryptResult,password,16);
        byte[] decryptResult = aesecbUtils.decrypt(aesecbUtils.parseHexStr2Byte(passwordStr),password,16);
        log.info("解密后：" + new String(decryptResult));
        log.info("KeyGenerator 结果：");


        System.out.println(aesecbUtils.stringToBase64("sddmpa123456789"));
        try {
            log.info(aesecbUtils.base64ToString(aesecbUtils.stringToBase64("sddmpa123456789")));
        } catch (Exception e) {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }

    }
}
