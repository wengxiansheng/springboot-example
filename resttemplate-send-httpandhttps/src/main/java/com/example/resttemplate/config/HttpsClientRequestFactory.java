package com.example.resttemplate.config;

import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.net.HttpURLConnection;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author wly
 * @date 2022-05-17 11:59:40
 * @describe同时支持http及https请求★/
 */
public class HttpsClientRequestFactory extends SimpleClientHttpRequestFactory {
    @Override
    protected void prepareConnection(HttpURLConnection connection, String httpMethod){
        try{
            if(!(connection instanceof HttpsURLConnection)){
                //http协议
                super.prepareConnection (connection, httpMethod) ;
            }
            if ((connection instanceof HttpsURLConnection)) {
                //https协议
                KeyStore trustStore = KeyStore.getInstance (KeyStore.getDefaultType());
                //信任任何链接
                TrustStrategy anyTrustStrategy = new TrustStrategy(){
                    @Override
                    public boolean isTrusted (X509Certificate[] chain,String authType) throws CertificateException{
                        return true;
                    }
                };
                SSLContext ctx = SSLContexts.custom().useSSL().loadTrustMaterial(trustStore , anyTrustStrategy).build();
                ((HttpsURLConnection) connection).setSSLSocketFactory(ctx.getSocketFactory());
                HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                super.prepareConnection(httpsConnection,httpMethod) ;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
