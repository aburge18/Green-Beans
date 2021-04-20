package com.example.greenbeans;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Account {

    private final okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();

    String accountType, refreshToken, authCode, lastRefresh, accountID, accountBuyValStr, accountCurrentValStr, clientID;
    Double accountBuyVal = 0.0;
    Double accountCurrentVal = 0.0;
    int stage = 0;
    String code;
    ArrayList<Position> positions = new ArrayList<Position>();

    public Account(){}

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

    public void getFirstRefreshToken(String clientID){
        this.clientID = clientID;
        switch (accountType){
            case "TD":
                getFirstTDRefreshTokenStart();
                break;
            case "Fidelity":
                getFirstTDRefreshTokenStart();//TODO: change to fidelty
                break;
        }
    }


    public void getFirstTDRefreshTokenStart(){
        GetFirstTDRefreshTokenRunnable runnable = new GetFirstTDRefreshTokenRunnable();
        new Thread(runnable).start();
    }


    class GetFirstTDRefreshTokenRunnable implements Runnable{

        @Override
        public void run() {
            FormBody formBody = new FormBody.Builder().add("grant_type", "authorization_code").add("access_type", "offline").add("code", code).add("client_id", "HJ8DN850FB0BCX4ZCYCZK85SDKLKPLX7").add("redirect_uri", "http://localhost").build();
           //create post request with specified header
            final Request request = new Request.Builder().url("https://api.tdameritrade.com/v1/oauth2/token").post(formBody).addHeader("Content-Type", "application/x-www-form-urlencoded").build();

            String response1Body;//will hold entire response body

            try {
                try(Response response1 = client.newCall(request).execute()) {//try to execute post request
                    if (!response1.isSuccessful()) try {//if request isnt successful
                        throw new IOException("Unexpected code " + response1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    response1Body = response1.body().string();
                    JSONObject responseObj = new JSONObject(response1Body);//turn body string into JSON obj
                    String access = responseObj.getString("access_token");//get auth token
                    System.out.println(responseObj.toString());

                    refreshToken = responseObj.getString("refresh_token");

                    String lastRefresh = responseObj.getString("refresh_token_expires_in");
                    System.out.println("REFRESH: " + refreshToken);
                    authCode = access;
                    authCode = "Bearer " + authCode; //add "Bearer " since it is required in header post request
                    stage = 2;
                    System.out.println("Auth Code: " + authCode);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();//initialize firestore
                    Map<String, Object> data = new HashMap<>();
                    data.put("accountType", "TD");
                    data.put("lastRefresh", lastRefresh);
                    data.put("refreshToken", refreshToken);



                    DocumentReference accounts = db.collection("accounts").document();
                    accounts.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            DocumentReference currentClient = db.collection("clients").document(clientID);

                            currentClient.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot document = task.getResult();
                                    //JSONObject clientInfo = new JSONObject(document.getData());
                                        System.out.println("Account Present: " + clientID);
                                        currentClient.update("accounts", FieldValue.arrayUnion(accounts.getId()));
                                    }

                            });

                            System.out.println(accounts.getId());
                        }
                    });

                    System.out.println(response1Body);
                    System.out.println("Response 1 response:          " + response1);
                    System.out.println("Response 1 cache response:    " + response1.cacheResponse());
                    System.out.println("Response 1 network response:  " + response1.networkResponse());
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            System.out.println("REALLY GOT AUTH CODE: " + authCode);
        }
    }








    public void setAuthTokenViaRefresh(){//choose which way to get auth token based on account type
       switch (accountType){
           case "TD":
               setTDAuthTokenViaRefresh();
               break;
           case "Fidelity":
               setTDAuthTokenViaRefresh();//TODO: Change to fidelity when added
               break;
        }
    }

    public void setTDAuthTokenViaRefresh(){//get auth token for TD Ameritrade

        //body for post request
        FormBody formBody = new FormBody.Builder().add("grant_type", "refresh_token").add("refresh_token", refreshToken).add("client_id", "HJ8DN850FB0BCX4ZCYCZK85SDKLKPLX7").add("redirect_uri", "http://localhost").build();
        //create post request with specified header
        final Request request = new Request.Builder().url("https://api.tdameritrade.com/v1/oauth2/token").post(formBody).addHeader("Content-Type", "application/x-www-form-urlencoded").build();

        String response1Body;//will hold entire response body

        try {
            try(Response response1 = client.newCall(request).execute()) {//try to execute post request
                if (!response1.isSuccessful()) try {//if request isnt successful
                    throw new IOException("Unexpected code " + response1);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                response1Body = response1.body().string();
                JSONObject responseObj = new JSONObject(response1Body);//turn body string into JSON obj
                String access = responseObj.getString("access_token");//get auth token
                System.out.println(responseObj.toString());

                authCode = access;
                authCode = "Bearer " + authCode; //add "Bearer " since it is required in header post request
                stage = 2;
                System.out.println("Auth Code: " + authCode);
                System.out.println(response1Body);
                System.out.println("Response 1 response:          " + response1);
                System.out.println("Response 1 cache response:    " + response1.cacheResponse());
                System.out.println("Response 1 network response:  " + response1.networkResponse());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        System.out.println("REALLY GOT AUTH CODE: " + authCode);
    }

    public void addPositions(){//choose to add position based off account type

    switch (accountType){
        case "TD":
            addTDPositions();
            break;
        case "Fidelity":
            addTDPositions();//TODO: change to fidelty
            break;
    }
    }

    public void addTDPositions(){//add a TD Ameritrade position

        final Request request = new Request.Builder().url("https://api.tdameritrade.com/v1/accounts?fields=positions").addHeader("Authorization", authCode).build();//build request with authtoken header

        String response1Body;
        try {
            try(Response response1 = client.newCall(request).execute()) {
                if (!response1.isSuccessful()) try {
                    throw new IOException("Unexpected code " + response1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                response1Body = response1.body().string();
                System.out.println("Response 2: " + response1Body);
                JSONArray responseArray = new JSONArray(response1Body);
                Position tempPosition = null;
                JSONObject responseObj = responseArray.getJSONObject(0);
                JSONObject securitiesAccountObj =  responseObj.getJSONObject("securitiesAccount");
                accountID = securitiesAccountObj.getString("accountId");
                JSONArray positionsArr = securitiesAccountObj.getJSONArray("positions");
                JSONObject currentBalancesObj = securitiesAccountObj.getJSONObject("currentBalances");
                Double currentAccountValue;
                Double initialAccountValue;
                for (int i = 0; i < positionsArr.length(); i++) {
                    String symbol;
                    JSONObject positionObj = positionsArr.getJSONObject(i);
                    JSONObject instrumentObj = positionObj.getJSONObject("instrument");
                    symbol = instrumentObj.getString("symbol");
                    System.out.println("Symbol: " + symbol);

                    Double avgPrice = Double.valueOf(positionObj.getString("averagePrice"));
Double quantity = Double.valueOf(positionObj.getString("longQuantity"));
                    accountBuyVal += (avgPrice * quantity);
                    tempPosition = new Position(symbol, positionObj.getString("longQuantity"), String.format("%.2f", avgPrice));
                    positions.add(tempPosition);
                }
                getAccountValue();
                //stage = 4;
                System.out.println("Response 1 response:          " + response1);
                System.out.println("Response 1 cache response:    " + response1.cacheResponse());
                System.out.println("Response 1 network response:  " + response1.networkResponse());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
    public void buyPosition(String quantity, Position positionToBuy) throws JSONException {
        switch (accountType) {
            case "TD":
                buyTDPosition(quantity, positionToBuy);
                break;
            case "Fidelity":
                buyTDPosition(quantity, positionToBuy);//TODO: change to fidelty
                break;
        }
    }
    public void buyTDPosition(String quantity, Position positionToBuy) throws JSONException {
        ArrayList<Double> avgs = new ArrayList<Double>();//temp list to send to postExecute
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, createBuyOrder(quantity, positionToBuy));
        String url = "https://api.tdameritrade.com/v1/accounts/" + accountID + "/orders";
        final Request request = new Request.Builder().url(url).post(body).addHeader("Content-Type", "application/x-www-form-urlencoded").addHeader("Authorization", authCode).build();

        String response1Body;
        try {
            try(Response response1 = client.newCall(request).execute()) {
                if (!response1.isSuccessful()) try {
                    throw new IOException("Unexpected code " + response1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Position tempPosition = new Position();
                response1Body = response1.body().string();
                System.out.println("Response 2: " + response1Body);

                System.out.println("Response 1 response:          " + response1);
                System.out.println("Response 1 cache response:    " + response1.cacheResponse());
                System.out.println("Response 1 network response:  " + response1.networkResponse());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String createBuyOrder(String quantityVal, Position positionToBuy) throws JSONException {//creates the post request object for the buy order

        String quantity;
        quantity = quantityVal;
        Position tempPosition;
        tempPosition = positionToBuy;
        String symbol;
        symbol = tempPosition.symbol;
        String order = "{\n" +
                "  \"orderType\": \"MARKET\",\n" +
                "  \"session\": \"NORMAL\",\n" +
                "  \"duration\": \"DAY\",\n" +
                "  \"orderStrategyType\": \"SINGLE\",\n" +
                "  \"orderLegCollection\": [\n" +
                "    {\n" +
                "      \"instruction\": \"Buy\",\n" +
                "      \"quantity\": " + quantity + ",\n" +
                "      \"instrument\": {\n" +
                "        \"symbol\": \"" + symbol + "\",\n" +
                "        \"assetType\": \"EQUITY\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        return order;
    }


    public void sellPosition(String quantity, Position positionToBuy) throws JSONException {
        switch (accountType) {
            case "TD":
                sellTDPosition(quantity, positionToBuy);
                break;
            case "Fidelity":
                sellTDPosition(quantity, positionToBuy);//TODO: change to fidelty
                break;
        }
    }

    public void sellTDPosition(String quantity, Position positionToBuy) throws JSONException {
        ArrayList<Double> avgs = new ArrayList<Double>();//temp list to send to postExecute
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, createSellOrder(quantity, positionToBuy));
        final Request request = new Request.Builder().url("https://api.tdameritrade.com/v1/accounts/236970209/orders").post(body).addHeader("Content-Type", "application/x-www-form-urlencoded").addHeader("Authorization", authCode).build();

        String response1Body;
        try {
            try(Response response1 = client.newCall(request).execute()) {
                if (!response1.isSuccessful()) try {
                    throw new IOException("Unexpected code " + response1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Position tempPosition = new Position();
                response1Body = response1.body().string();
                System.out.println("Response 2: " + response1Body);

                System.out.println("Response 1 response:          " + response1);
                System.out.println("Response 1 cache response:    " + response1.cacheResponse());
                System.out.println("Response 1 network response:  " + response1.networkResponse());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String createSellOrder(String quantityVal, Position positionToBuy) throws JSONException {//creates the post request object for the buy order

        String quantity;
        quantity = quantityVal;
        Position tempPosition;
        tempPosition = positionToBuy;
        String symbol;
        symbol = tempPosition.symbol;
        String order = "{\n" +
                "  \"orderType\": \"MARKET\",\n" +
                "  \"session\": \"NORMAL\",\n" +
                "  \"duration\": \"DAY\",\n" +
                "  \"orderStrategyType\": \"SINGLE\",\n" +
                "  \"orderLegCollection\": [\n" +
                "    {\n" +
                "      \"instruction\": \"Sell\",\n" +
                "      \"quantity\": " + quantity + ",\n" +
                "      \"instrument\": {\n" +
                "        \"symbol\": \"" + symbol + "\",\n" +
                "        \"assetType\": \"EQUITY\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        return order;
    }
    public void getAccountValue(){
            GetCurrentAccountValue runnable = new GetCurrentAccountValue();
            new Thread(runnable).start();
    }

    class GetCurrentAccountValue implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < positions.size(); i++){
                positions.get(i).setCurrentPrice();
            }
            for (int i = 0; i < positions.size(); i++){
               accountCurrentVal += (positions.get(i).currentPriceVal * Double.valueOf(positions.get(i).quantity));
                System.out.println("ACCOUNT VALUE: " + accountCurrentVal + accountType);
            }
            stage = 5;

        }
    }
}
