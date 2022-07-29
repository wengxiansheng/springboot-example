package com.example.httpclient.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author wly
 * @date 2022-05-17 17:31:25
 * @describe HttpClient同时支持发送http及htpps请求
 */
@Component
@Slf4j
public class HttpClientUtils {
    private static int SocketTimeout = 5000;//5秒
    private static int ConnectTimeout = 5000;//5秒
    private static Boolean SetTimeOut = true;

    private static CloseableHttpClient getHttpClient() {
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();
        ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
        registryBuilder.register("http", plainSF);
            //指定信任密钥存储对象和连接套接字工厂
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            //信任任何链接
            TrustStrategy anyTrustStrategy = new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            };
            SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, anyTrustStrategy).build();
            LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            registryBuilder.register("https", sslSF);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Registry<ConnectionSocketFactory> registry = registryBuilder.build();
        //设置连接管理器
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
//		connManager.setDefaultConnectionConfig(connConfig);
//		connManager.setDefaultSocketConfig(socketConfig);
        //构建客户端
        return HttpClientBuilder.create().setConnectionManager(connManager).build();
    }

    /**
     * @param url     请求的url
     * @describe get
     * @param queries 请求的参数，在url？后面的数据，可以传null
     * @return
     * @throws IOException
     */
    public static String get(String url,Map<String, String> queries) throws IOException {
        log.info("get请求……");
        String responseBody = "";
        //CloseableHttpClient httpClient=HttpClients.createDefault();
        //支持http及https请求
        CloseableHttpClient httpClient = getHttpClient();

        StringBuilder sb = new StringBuilder(url);

        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = true;
            Iterator iterator = queries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                if (firstFlag) {
                    sb.append("?" + (String) entry.getKey() + "=" + (String) entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&" + (String) entry.getKey() + "=" + (String) entry.getValue());
                }
            }
        }

        HttpGet httpGet = new HttpGet(sb.toString());
        if (SetTimeOut) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(SocketTimeout)
                    .setConnectTimeout(ConnectTimeout).build();//设置请求和传输超时时间
            httpGet.setConfig(requestConfig);
        }
        try {
            log.info("正在执行请求是： " + httpGet.getRequestLine());
            //请求数据
            CloseableHttpResponse response = httpClient.execute(httpGet);
            System.out.println(response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                responseBody = EntityUtils.toString(entity);
            } else {
                log.info("http返回错误状态:" + status);
                throw new ClientProtocolException("未知响应状态: " + status);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            httpClient.close();
        }
        return responseBody;
    }
    /**
     * @param url     请求的url
     * @describe put
     * @param queries 请求的参数，在url？后面的数据，可以传null
     * @return
     * @throws IOException
     */
    public static String put(String url,Map<String, String> queries) throws IOException {
        log.info("put请求……");
        String responseBody = "";
        //CloseableHttpClient httpClient=HttpClients.createDefault();
        //支持http及https请求
        CloseableHttpClient httpClient = getHttpClient();

        StringBuilder sb = new StringBuilder(url);

        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = true;
            Iterator iterator = queries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                if (firstFlag) {
                    sb.append("?" + (String) entry.getKey() + "=" + (String) entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&" + (String) entry.getKey() + "=" + (String) entry.getValue());
                }
            }
        }
        HttpPut httpPut = new HttpPut(sb.toString());
        if (SetTimeOut) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(SocketTimeout)
                    .setConnectTimeout(ConnectTimeout).build();//设置请求和传输超时时间
            httpPut.setConfig(requestConfig);
        }
        try {
            log.info("正在执行请求是：" + httpPut.getRequestLine());
            //请求数据
            CloseableHttpResponse response = httpClient.execute(httpPut);
            System.out.println(response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                responseBody = EntityUtils.toString(entity);
            } else {
                log.info("http返回错误状态:" + status);
                throw new ClientProtocolException("未知响应状态: " + status);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            httpClient.close();
        }
        return responseBody;
    }
    /**
     * @param url     请求的url
     * @describe delete
     * @param queries 请求的参数，在url？后面的数据，可以传null
     * @return
     * @throws IOException
     */
    public static String delete(String url,Map<String, String> queries) throws IOException {
        log.info("delete请求……");
        String responseBody = "";
        //CloseableHttpClient httpClient=HttpClients.createDefault();
        //支持http及https请求
        CloseableHttpClient httpClient = getHttpClient();

        StringBuilder sb = new StringBuilder(url);

        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = true;
            Iterator iterator = queries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                if (firstFlag) {
                    sb.append("?" + (String) entry.getKey() + "=" + (String) entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&" + (String) entry.getKey() + "=" + (String) entry.getValue());
                }
            }
        }
        HttpDelete httpDelete = new HttpDelete(sb.toString());
        if (SetTimeOut) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(SocketTimeout)
                    .setConnectTimeout(ConnectTimeout).build();//设置请求和传输超时时间
            httpDelete.setConfig(requestConfig);
        }
        try {
            log.info("正在执行请求是：" + httpDelete.getRequestLine());
            //请求数据
            CloseableHttpResponse response = httpClient.execute(httpDelete);
            System.out.println(response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                responseBody = EntityUtils.toString(entity);
            } else {
                log.info("http返回错误状态:" + status);
                throw new ClientProtocolException("未知响应状态: " + status);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            httpClient.close();
        }
        return responseBody;
    }

    /**
     * @param url     请求的url
     * @describe post
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @param params  post form 提交的参数
     * @return
     * @throws IOException
     */
    public static String post(String url, Map<String, String> queries, Map<String, String> params) throws IOException {
        log.info("post请求……");
        String responseBody = "";
        //CloseableHttpClient httpClient = HttpClients.createDefault();
        //支持https
        CloseableHttpClient httpClient = getHttpClient();

        StringBuilder sb = new StringBuilder(url);

        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = true;
            Iterator iterator = queries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                if (firstFlag) {
                    sb.append("?" + (String) entry.getKey() + "=" + (String) entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&" + (String) entry.getKey() + "=" + (String) entry.getValue());
                }
            }
        }

        HttpPost httpPost = new HttpPost(sb.toString());
        if (SetTimeOut) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(SocketTimeout)
                    .setConnectTimeout(ConnectTimeout).build();//设置超时时间
            httpPost.setConfig(requestConfig);
        }
        //添加参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (params != null && params.keySet().size() > 0) {
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                nvps.add(new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));
            }
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        //请求数据
        CloseableHttpResponse response = httpClient.execute(httpPost);
        try {
            System.out.println(response.getStatusLine());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                responseBody = EntityUtils.toString(entity);
            } else {
                log.info("http返回错误状态:" + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.close();
        }
        return responseBody;
    }
}
