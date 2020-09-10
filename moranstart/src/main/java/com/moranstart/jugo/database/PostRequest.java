package com.moranstart.jugo.database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;


public class PostRequest {

    public static Map<String, Object> request(String url, String content) {
        Map<String, Object> result = new HashMap<String, Object>();
        String errorStr = "";
        String status = "";
        String response = "";
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            //
            URLConnection conn = realUrl.openConnection();
            HttpURLConnection httpUrlConnection = (HttpURLConnection) conn;
            //
            httpUrlConnection.setRequestProperty("Content-Type", "application/json");
            httpUrlConnection.setRequestProperty("x-adviewrtb-version", "2.1");
            //
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            //
            out = new PrintWriter(httpUrlConnection.getOutputStream());
            // ��??????��????
            out.write(content);
            // flush
            out.flush();
            httpUrlConnection.connect();
            //
            in = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                response +=new String(line.getBytes(), "UTF-8");

            }
            status = new Integer(httpUrlConnection.getResponseCode()).toString();
        } catch (Exception e) {
            System.out.println("��POST error" + e);
            errorStr = e.getMessage();
        }
        // ????finally?��????��??????��???????��
        finally {
            try {
                if (out != null) { out.close();}
                if (in != null) {in.close();}
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        result.put("errorStr", errorStr);
        result.put("response", response);
        //result.put("status", status);
        return result;

    }


}
