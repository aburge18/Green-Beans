package com.example.greenbeans;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AccountPositionsViewAdapter extends RecyclerView.Adapter<AccountPositionsViewAdapter.ProfileViewHolder> {

    ArrayList<Position> positions;
    Context context;
    IListener mListener;

    public AccountPositionsViewAdapter(ArrayList<Position> positions, Context context){
        this.positions = positions;
        mListener = (IListener) context;
        this.context = context;
    }

    @NonNull
    @Override
    public AccountPositionsViewAdapter.ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.position_row_item, parent, false);
        AccountPositionsViewAdapter.ProfileViewHolder profileViewHolder = new AccountPositionsViewAdapter.ProfileViewHolder(view);
        return profileViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AccountPositionsViewAdapter.ProfileViewHolder holder, int position) {
        Position currPosition = positions.get(position);
        holder.posSymbolTV.setText(currPosition.symbol);
        holder.posBuyPriceTV.setText(currPosition.buyPrice);
        holder.posQuantityTV.setText(currPosition.quantity);
        new Positionst().execute(position);
        if (currPosition.currentPrice != null){
            holder.currentStockPriceTV.setText(currPosition.currentPrice);
        }
        holder.buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.setPositionToBuy(currPosition.symbol);
            }
        });














    }

    @Override
    public int getItemCount() {
        if (positions == null){
            return 0;
        }else{
            return this.positions.size();
        }
    }


    public static class ProfileViewHolder extends RecyclerView.ViewHolder{

        TextView posBuyPriceTV, posSymbolTV, posQuantityTV, currentStockPriceTV;
        Button buyBtn, sellBtn;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);


            posBuyPriceTV = itemView.findViewById(R.id.posBuyPriceTV);

            buyBtn = itemView.findViewById(R.id.buyPosBtn);
            sellBtn = itemView.findViewById(R.id.sellPosBtn);
            posSymbolTV = itemView.findViewById(R.id.posSymbolTV);
            posQuantityTV = itemView.findViewById(R.id.posQuantityTV);
            currentStockPriceTV = itemView.findViewById(R.id.currentStockPriceTV);
        }






    }
    public interface IListener{
        void setPositionToBuy(String positionToBuy);
    }
    public String getCurrentPositionPrice(String symbol){


        String stockPrice;
        OkHttpClient client = new OkHttpClient();
        String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/market/v2/get-quotes?region=US&symbols=" + symbol;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-key", "fb1ef2c3bfmshfc9f09614f04e15p1f5320jsn81db61cc9d2a")
                .addHeader("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
                .build();





        try(Response response1 = client.newCall(request).execute()) {

            String responseStr = response1.body().string();
            JSONObject responseObj = new JSONObject(responseStr);
            System.out.println("Response: " + responseStr);
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

        Position currentPosition;
        Integer position;
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public String doInBackground(Integer... ints) {
            String symobol;
            position = ints[0];
           currentPosition = positions.get(ints[0]);
            currentPosition.setCurrentPrice();
            String price = "";
            //getCurrentPositionPrice(symobol);
            return price;




        }

        protected void onProgressUpdate(Double... values) {

        }

        protected void onPostExecute(String price) {//when doInBackground is done executing
            super.onPostExecute(price);
            System.out.println("Price:  " + price);

            //currentPosition.currentPrice = price;
            //positions.get(position).currentPrice = price;
 notifyDataSetChanged();



        }

        @Override
        public void run() {

        }


    }


}
