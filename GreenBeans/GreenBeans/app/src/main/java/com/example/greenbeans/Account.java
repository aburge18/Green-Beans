package com.example.greenbeans;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class Account {
    String accountType, refreshToken, authCode, lastRefresh;
    private final okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();


    public Account(){

    }
    public Account(String accountType, String refreshToken, String lastRefresh){
        this.accountType = accountType;
        this.refreshToken = refreshToken;
        this.lastRefresh = lastRefresh;

    }

    public void addAccount(String accountType, String refreshToken, String lastRefresh){
        this.accountType = accountType;
        this.refreshToken = refreshToken;
        this.lastRefresh = lastRefresh;

    }
    public void addAccountType(String accountType){
        this.accountType = accountType;
    }

    public void addRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
    public void addLastRefresh(String lastRefresh){
        this.lastRefresh = lastRefresh;
    }

    public void setAuthCode(String authCode){
        this.authCode = authCode;
    }


    public void setAuthTokenViaRefresh(){

        FormBody formBody = new FormBody.Builder().add("grant_type", "refresh_token").add("refresh_token", refreshToken).add("client_id", "HJ8DN850FB0BCX4ZCYCZK85SDKLKPLX7").add("redirect_uri", "http://localhost").build();
        System.out.println("Form: " + refreshToken);
        final Request request = new Request.Builder().url("https://api.tdameritrade.com/v1/oauth2/token").post(formBody).addHeader("Content-Type", "application/x-www-form-urlencoded").build();


        String response1Body;
        try {
            try(Response response1 = client.newCall(request).execute()) {
                if (!response1.isSuccessful()) try {
                    throw new IOException("Unexpected code " + response1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                response1Body = response1.body().string();
                JSONObject responseObj = new JSONObject(response1Body);
                String access = responseObj.getString("access_token");
                //String refresh = responseObj.getString("refresh_token");
                //System.out.println("Refresh: " + refresh);
                System.out.println(responseObj.toString());
                  /*  authCode = response1Body;
                    authCode = authCode.substring(authCode.indexOf(":") + 3);
                    authCode = authCode.substring(0, authCode.indexOf("\n") - 2);*/
                authCode = access;
                //FirebaseFirestore db = FirebaseFirestore.getInstance();
                //db.collection("clients").document("Kqugtw2GLpXJh2G7p8rPjRjOOhs2").update("refreshtoken", refresh);
                authCode = "Bearer " + authCode;

                System.out.println("Auth Code: " + authCode);

                System.out.println(response1Body);
                System.out.println("Response 1 response:          " + response1);
                System.out.println("Response 1 cache response:    " + response1.cacheResponse());
                System.out.println("Response 1 network response:  " + response1.networkResponse());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
         this.authCode = authCode;
    }

}
