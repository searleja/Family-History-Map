package com.example.familymap;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
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
import java.util.ArrayList;

import Models.Person;
import Models.User;
import Results.LoginResult;
import Results.PersonResult;
import Results.RegisterResult;

public class ServerProxy {

    public void ServerProxy() {}

    public static LoginResult login(String serverHost, String serverPort, String userName, String password) {

        LoginResult result;
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
                    result = new LoginResult("Failed to login");
                    return result;
                }
                else {
                    String authToken = json.getString("authtoken");
                    String personID = json.getString("personID");
                    result = new LoginResult(authToken, userName, personID);
                    return result;
                }
            }
            else {
                result = new LoginResult("Failed to login");
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        result = new LoginResult("Failed to login");
        return result;
    }

    public static RegisterResult register(String serverHost, String serverPort, String userName,
                                          String password, String firstName, String lastName, String email, String gender) {
        RegisterResult result;
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(true);
            http.setDoInput(true);
            Log.d("TEST", "finished initializing http");
            Log.d("TEST", url.toString());

            http.connect();

            String reqData =
                    "{" +
                            "\"username\": \"" + userName + "\",\n" + "\"password\": \"" + password + "\",\n"
                            + "\"email\": \"" + email + "\",\n" + "\"firstName\": \"" + firstName + "\",\n"
                            + "\"lastName\": \"" + lastName + "\",\n" + "\"gender\": \"" + gender + "\"\n"
                            + "}";

            Log.d("TEST", reqData);

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream resBody = http.getInputStream();

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
                    result = new RegisterResult("Failed to register");
                    return result;
                }
                else {
                    String authToken = json.getString("authtoken");
                    String personID = json.getString("personID");
                    result = new RegisterResult(authToken, userName, personID);
                    return result;
                }
            }
            else {
                result = new RegisterResult("Failed to register");
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        result = new RegisterResult("Failed to register");
        return result;

    }

    public static PersonResult retrieveData(String serverHost, String serverPort, String authToken) {
        PersonResult result;
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestProperty("Authorization", authToken);
            http.setRequestMethod("GET");
            http.setDoInput(true);
            Log.d("RETRIEVAL", "about to connect");
            Log.d("RETRIEVAL", url.toString());

            http.connect();
            Log.d("RETRIEVAL", "connected");
            Log.d("RETRIEVAL", http.getRequestMethod());
            Log.d("RETRIEVAL", String.valueOf(http.getResponseCode()));

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.d("RETRIEVAL", "ok");
                InputStream resBody = http.getInputStream();

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
                JSONArray jsonArray = json.getJSONArray("data");
                Gson g = new Gson();
                String message = json.optString("message"); //optstring so it doesn't throw an exception
                Log.d("RETRIEVAL", "before if in server");
                if (!message.equals("")) {
                    result = new PersonResult("Failed to retrieve persons");
                    return result;
                }
                else {
                    Log.d("RETRIEVAL", "about to insert into cache");
                    DataCache cache = DataCache.getInstance();
                    result = new PersonResult(cache.insertPeople(jsonArray));
                    return result;
                }
            }
            else {
                result = new PersonResult("Failed to retrieve persons");
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        result = new PersonResult("Failed to retrieve persons");
        return result;
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
