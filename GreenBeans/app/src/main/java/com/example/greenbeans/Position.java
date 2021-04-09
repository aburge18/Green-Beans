package com.example.greenbeans;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Position {

    String symbol, quantity, buyPrice, currentPrice;
    int quantityInt, countDownTime;
    double buyPriceNum, currentPriceVal, dayProfitLoss;
Boolean countingDown = false;
    public Position(String symbol, String quantity, String buyPrice){
        this.symbol = symbol;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
    }
    public Position(){}

    public void startPositionTimer(){
       new Positionst().execute();
    }
   public String printCurrentPosition(){
        return currentPrice;
    }

    public void setCurrentPrice(){
        if(!countingDown) {
            countingDown = true;
            countDownTime = 50;
            String url = "https://api.tdameritrade.com/v1/marketdata/" + symbol + "/quotes?apikey=HJ8DN850FB0BCX4ZCYCZK85SDKLKPLX7";
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(url).get().build();
            try (Response response1 = client.newCall(request).execute()) {


                String responseStr = response1.body().string();
                JSONObject responseObj = new JSONObject(responseStr);
                JSONObject positionObj = responseObj.getJSONObject(symbol);

                DecimalFormat df = new DecimalFormat("0.00");
                currentPrice = df.format(Double.valueOf(positionObj.getString("lastPrice")));

                System.out.println("PRICE RESPONSE" + responseStr);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }else{
            countDownTime --;
            System.out.println(symbol + " --- " + countDownTime);
            if (countDownTime <= 0){
                countingDown = false;
            }
        }
    }
    public String getCurrentPositionPrice(){


        String stockPrice;
        OkHttpClient client = new OkHttpClient();
        String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/market/v2/get-quotes?region=US&symbols=" + this.symbol;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-key", "fb1ef2c3bfmshfc9f09614f04e15p1f5320jsn81db61cc9d2a")
                .addHeader("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
                .build();





        try(Response response1 = client.newCall(request).execute()) {

            String responseStr = response1.body().string();
            JSONObject responseObj = new JSONObject(responseStr);
            JSONObject quoteResponseObj = responseObj.getJSONObject("quoteResponse");
            JSONArray resultArr = quoteResponseObj.getJSONArray("result");
            JSONObject positionObj = resultArr.getJSONObject(0);
            String currentPrice = positionObj.getString("regularMarketPrice");
            System.out.println("BID: " + currentPrice);
            return currentPrice;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return "ERROR: NO PRICE";


    }
    public class Positionst extends AsyncTask<Integer, Double, String> implements Runnable {


        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public String doInBackground(Integer... values) {

            currentPrice = getCurrentPositionPrice();
            return currentPrice;


        }

        protected void onProgressUpdate(Double... values) {

        }

        protected void onPostExecute(String price) {//when doInBackground is done executing
            super.onPostExecute(price);
            System.out.println("Price:  " + price);
            currentPrice = price;
            currentPriceVal = Double.valueOf(currentPrice);


        }

        @Override
        public void run() {

        }


    }
}
