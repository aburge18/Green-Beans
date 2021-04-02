package com.example.greenbeans;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfirmBuyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfirmBuyFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView confirmTV;

    String authCode;
    ArrayList list = new ArrayList();
    Double priceVal;
    private final okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
    public ConfirmBuyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfirmBuyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfirmBuyFragment newInstance(String param1, String param2) {
        ConfirmBuyFragment fragment = new ConfirmBuyFragment();
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
        View view = inflater.inflate(R.layout.fragment_confirm_buy, container, false);
        confirmTV = view.findViewById(R.id.confirmMsgTV);
        list = mListener.getConfirmBuy();
        new GetPrice().execute();//get most recent stock price
        Account account = mListener.getCurrentAccount();
        authCode = account.authCode;//authentication token for buy order
        Button confirmBtn = view.findViewById(R.id.confirmBuyBtn);
        Button cancelBtn = view.findViewById(R.id.cancelConfirmBuyBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                   new BuyPosition().execute();//start buy order
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }


    public String createBuyOrder() throws JSONException {//creates the post request object

        String order = "{\n" +
                "  \"orderType\": \"MARKET\",\n" +
                "  \"session\": \"NORMAL\",\n" +
                "  \"duration\": \"DAY\",\n" +
                "  \"orderStrategyType\": \"SINGLE\",\n" +
                "  \"orderLegCollection\": [\n" +
                "    {\n" +
                "      \"instruction\": \"Buy\",\n" +
                "      \"quantity\": " + list.get(0) + ",\n" +
                "      \"instrument\": {\n" +
                "        \"symbol\": \"" + list.get(1) + "\",\n" +
                "        \"assetType\": \"EQUITY\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";


        System.out.println("ORDER:  " + order);
        return order;

    }







    public class GetPrice extends AsyncTask<Integer, Double, String> implements Runnable {


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
                    String confirmStr = "Buy: " + list.get(0) + " of " + list.get(1) + " for $" + price;
                    confirmTV.setText(confirmStr);

                }
            });
        }

        @Override
        public void run() {



        }


    }
    public class BuyPosition extends AsyncTask<Integer, Double, Position> implements Runnable {

        String symbol;
        public final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, createBuyOrder());

        public BuyPosition() throws JSONException {
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public Position doInBackground(Integer... values) {
            ArrayList<Double> avgs = new ArrayList<Double>();//temp list to send to postExecute
            //final Request request = new Request.Builder().url("https://api.tdameritrade.com/v1/accounts?fields=positions").addHeader("Authorization", authCode).build();
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
                    /*JSONArray responseArray = new JSONArray(response1Body);

                    JSONObject responseObj = responseArray.getJSONObject(0);
                    JSONObject securitiesAccountObj =  responseObj.getJSONObject("securitiesAccount");
                    JSONArray positionsArr = securitiesAccountObj.getJSONArray("positions");
                    JSONObject positionObj = positionsArr.getJSONObject(0);
                    JSONObject instrumentObj= positionObj.getJSONObject("instrument");
                    symbol = instrumentObj.getString("symbol");
                    System.out.println("Symbol: " + symbol);

                    tempPosition = new Position(symbol, positionObj.getString("longQuantity"), positionObj.getString("averagePrice"));
//positions.add(tempPosition);


                    //System.out.println("Temp pos: " + tempPosition.symbol + tempPosition.quantity);
                    String numOfPos = positionObj.getString("longQuantity");
                    numOfPos = numOfPos.substring(0, numOfPos.length() - 2);
                    int posNum = Integer.valueOf(numOfPos);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                        }
                    });*/
                    System.out.println("Response 1 response:          " + response1);
                    System.out.println("Response 1 cache response:    " + response1.cacheResponse());
                    System.out.println("Response 1 network response:  " + response1.networkResponse());
                    return tempPosition;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new Position();//send list of nums to postExecute
        }

        protected void onProgressUpdate(Double... values) {

        }

        protected void onPostExecute(Position tempPosition) {//when doInBackground is done executing
            super.onPostExecute(tempPosition);
            System.out.println("ADDed: " + tempPosition.symbol);
            //positions.add(tempPosition);
            //adapter.notifyDataSetChanged();
        }

        @Override
        public void run() {



        }
    }



    public String getStockPrice() throws IOException {
        String stockPrice;
        OkHttpClient client = new OkHttpClient();
        String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/market/v2/get-quotes?region=US&symbols=" + list.get(1);
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
            String currentPrice = positionObj.getString("ask");
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
        ArrayList<String> getConfirmBuy();
        Account getCurrentAccount();
    }
}