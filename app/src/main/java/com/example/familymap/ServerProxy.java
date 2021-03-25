package com.example.familymap;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServerProxy {

    public void ServerProxy() {}

    public static boolean login(String serverHost, String serverPort, String userName, String password, Context context) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(true);
            http.setDoInput(true);
            Log.d("TEST", "finished initializing http");
            Log.d("TEST", url.toString());

            http.connect();

            String reqData =
                    "{" +
                            "\"username\": \"" + userName + "\",\n" + "\"password\": \"" + password + "\""
                     + "}";

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream resBody = http.getInputStream();
                Log.d("TEST", resBody.toString());

                //from lecture
                //need to do this because the JSONObject won't accept resBody as a parameter
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = resBody.read(buffer)) != -1) {
                    bytes.write(buffer, 0, length);
                }

                String resBodyData = bytes.toString();

                JSONObject json = new JSONObject(resBodyData);
                String message = json.optString("message"); //optstring so it doesn't throw an exception
                if (!message.equals("")) {
                    return false;
                }
                else {
                    String personID = json.getString("personID");
                    String authToken = json.getString("authtoken");
                    Log.d("TEST", personID);
                }
            }
            else {
                System.out.println("error");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;

    }

    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

}
