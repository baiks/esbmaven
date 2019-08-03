/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esb.common;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 *
 * @author adm
 */
public class HTTPCalls {

    /**
     * This function process HTTP post request.
     *
     * @param url
     * @param jsonString
     * @return
     * @throws IOException
     */
    public static String POSTRequest(String url, String jsonString) throws IOException {
        String resp = "";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        int timeout = 60000;
        con.setConnectTimeout(timeout);
        con.setReadTimeout(timeout);

        // add reuqest header
        con.setRequestMethod("POST");
        // con.setRequestProperty("User-Agent", "Mozilla/5.0");
        // con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("content-type", "application/json");
        con.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(jsonString);
        wr.flush();

        //int responseCode = con.getResponseCode();
        int size = 20000;
        StringBuilder response = new StringBuilder(size);

        try (InputStreamReader input = new InputStreamReader(con.getInputStream())) {
            try (BufferedReader in = new BufferedReader(input)) {
                String inputLine = null;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
        }
        resp = response.toString();

        return resp;
    }

    /**
     * This function process HTTPS requests
     *
     * @param https_url
     * @param message
     * @return
     * @throws IOException
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     */
    public String httpsPOST(String https_url, String message)
            throws IOException, KeyManagementException, NoSuchAlgorithmException {
        String response = "";
        SSLContext ssl_ctx = SSLContext.getInstance("TLS");
        TrustManager[] trust_mgr = getTrustManager();
        ssl_ctx.init(null, // key manager
                trust_mgr, // trust manager
                new SecureRandom()); // random number generator
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(ssl_ctx.getSocketFactory());

        java.net.URL url = new java.net.URL(https_url);

        javax.net.ssl.HttpsURLConnection conn = (javax.net.ssl.HttpsURLConnection) url.openConnection();
        // Guard against "bad hostname" errors during handshake.
        conn.setHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String host, SSLSession sess) {
                return true;
            }
        });
        int timeout = 10000;
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setReadTimeout(timeout);
        conn.setConnectTimeout(timeout);
        conn.setRequestProperty("content-type", "application/json");
        OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

        osw.write(message);
        osw.flush();
        int size = 20000;
        StringBuilder res = new StringBuilder(size);
        InputStreamReader in = new InputStreamReader(conn.getInputStream(), "UTF-8");
        try (BufferedReader reader = new BufferedReader(in)) {
            String inputLine = "";
            while ((inputLine = reader.readLine()) != null) {
                res.append(inputLine);
            }
        }
        response = res.toString();
        return response;

    }

    /**
     * This function process HTTP GET 
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String sendGet(String url, Map<String, Object> params) throws Exception {
        URL obj = new URL(url);
        int size = 20000;
        StringBuilder postData = new StringBuilder(size);
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) {
                postData.append('&');
            }
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod("POST");
        // conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String inputLine = "";
        int size2 = 20000;
        StringBuffer response = new StringBuffer(size2);

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // print result
        // System.out.println(response.toString());
        return response.toString();
    }

    /**
     * This function is used to bypass SSL
     * @return
     */
    private static TrustManager[] getTrustManager() {
        TrustManager[] certs = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String t) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String t) {
            }
        }};
        return certs;
    }
}
