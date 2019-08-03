/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esb.common;

import com.esb.Main.ConfigSession;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
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

    public String POSTRequest(String url, String jsonString) throws IOException {
        String resp = "";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setConnectTimeout(20000);
        con.setReadTimeout(20000);

        //add reuqest header
        con.setRequestMethod("POST");
        //con.setRequestProperty("User-Agent", "Mozilla/5.0");
        //con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("content-type", "application/json");
        con.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(jsonString);
        wr.flush();

        //int responseCode = con.getResponseCode();
        StringBuilder response = new StringBuilder();

        try (InputStreamReader input = new InputStreamReader(con.getInputStream())) {
            try (BufferedReader in = new BufferedReader(input)) {
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
        }

//        logging.applicationLog(Utilities.logPreString() + Utilities.logPreString()
//                + "Response from Client : " + response, "", "2");
        resp = response.toString();

        return resp;
    }

    public String httpsPOST(String https_url, String message, ClassImportantValues cl) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        cl.LogErrors("request", message);
        String response = "";
        try {
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
                @Override
                public boolean verify(String host, SSLSession sess) {
                    return true;
                }
            });

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setReadTimeout(20000);
            conn.setConnectTimeout(20000);
            conn.setRequestProperty("content-type", "application/json");
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

            osw.write(message);
            osw.flush();
            String res = "";
            InputStreamReader in = new InputStreamReader(conn.getInputStream(), "UTF-8");
            try (Reader reader = new BufferedReader(in)) {
                for (int c; (c = reader.read()) >= 0; res = res + ((char) c));
            }

            response = res;
        } catch (NoSuchAlgorithmException | KeyManagementException | IOException ex) {
            StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                ConfigSession.cl.LogErrors("httpsPOST", sw.toString());

        }
        cl.LogErrors("response", response);
        return response;

    }
// HTTP GET request

    public String sendGet(String url, Map<String, Object> params) throws Exception {
        URL obj = new URL(url);

        StringBuilder postData = new StringBuilder();
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
        //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        StringBuilder response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            String inputLine;
            response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        //print result
        //System.out.println(response.toString());
        return response.toString();
    }

    private TrustManager[] getTrustManager() {
        TrustManager[] certs = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String t) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String t) {
                }
            }
        };
        return certs;
    }
}
