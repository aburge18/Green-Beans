package com.example.greenbeans;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BuyPositionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuyPositionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
TextView buyPriceTV;
    Double priceVal;
    String positionSymbol;
    Double quantity ;
    public BuyPositionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BuyPositionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BuyPositionFragment newInstance(String param1, String param2) {
        BuyPositionFragment fragment = new BuyPositionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buy_position, container, false);
        EditText quatityETV = view.findViewById(R.id.buyQuantityETV);
        TextView buySymobolTV = view.findViewById(R.id.buySymbolTV);
        TextView buyAmountTV = view.findViewById(R.id.buyAmountTV);
        TextView buyPositionTV = view.findViewById(R.id.buyPositionTV);

        TextView buyTotalTV = view.findViewById(R.id.buyTotalTV);
buyPriceTV = view.findViewById(R.id.buyPriceTV);
        positionSymbol = mListener.getPositionToBuy();

        Button buyPositionButton = view.findViewById(R.id.buyPositionBtn);

        buyPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0){
                    mListener.confirmBuy(quantity, positionSymbol);
                }
            }
        });
buySymobolTV.setText(positionSymbol);
quatityETV.setText("0");
buyPositionTV.setText(positionSymbol);
buyAmountTV.setText("0");
new Positions().execute();

quatityETV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!quatityETV.getText().toString().matches("")) {
                    quantity = Double.valueOf(quatityETV.getText().toString());
                    buyAmountTV.setText(quantity.toString());
                    Double total = quantity * priceVal;
                    String.format("%.2f", total);
                    buyTotalTV.setText(total.toString());
                }
            }
        });


        return view;
    }


    public class Positions extends AsyncTask<Integer, Double, String> implements Runnable {


        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public String doInBackground(Integer... values) {
            String price = null;
            try {
                price = getStockPrice();
                return price;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return price;

        }

        protected void onProgressUpdate(Double... values) {

        }

        protected void onPostExecute(String price) {//when doInBackground is done executing
            super.onPostExecute(price);
            System.out.println("Price:  " + price);
            priceVal = Double.valueOf(price);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
buyPriceTV.setText(price);

                }
            });
        }

        @Override
        public void run() {



        }


    }
    public String getStockPrice() throws IOException {
        String stockPrice;
        OkHttpClient client = new OkHttpClient();
String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/market/v2/get-quotes?region=US&symbols=" + positionSymbol;
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





        return "";
    }


    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if (context instanceof IListener){
            mListener = (IListener)context;
        }else{
            throw new RuntimeException(context.toString() + " must implement listener");
        }
    }
   IListener mListener;

    public interface IListener{
        String getPositionToBuy();
        void confirmBuy(Double quantity, String symbol);
    }

}