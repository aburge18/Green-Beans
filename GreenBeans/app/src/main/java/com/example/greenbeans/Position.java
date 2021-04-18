package com.example.greenbeans;

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
        this.buyPriceNum = Double.valueOf(buyPrice);
        setCurrentPrice();
    }

    public Position(){}

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

                currentPriceVal = Double.valueOf(currentPrice);
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
}
