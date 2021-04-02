package com.example.greenbeans;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountPositionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountPositionsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;

    AccountPositionsViewAdapter adapter;
    LinearLayoutManager layoutManager;
ArrayList<Position> positions = new ArrayList<>();
    Account currentAccount;
    String authCode;
    public AccountPositionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountPositionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountPositionsFragment newInstance(String param1, String param2) {
        AccountPositionsFragment fragment = new AccountPositionsFragment();
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
        View view = inflater.inflate(R.layout.fragment_account_positions, container, false);

        adapter = new AccountPositionsViewAdapter(positions, getContext());
        recyclerView = view.findViewById(R.id.positionRecView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        currentAccount = mListener.getCurrentAccount();
        System.out.println("ALmost done: " + currentAccount.refreshToken);
        authCode = currentAccount.authCode;
        new Positions().execute();
        Button addPositionsBtn = view.findViewById(R.id.addPositionBtn);
        addPositionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.setPositionToBuy();
            }
        });

        return view;
    }

    public class Positions extends AsyncTask<Integer, Double, Position> implements Runnable {

String symbol;
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public Position doInBackground(Integer... values) {
            ArrayList<Double> avgs = new ArrayList<Double>();//temp list to send to postExecute
            final Request request = new Request.Builder().url("https://api.tdameritrade.com/v1/accounts?fields=positions").addHeader("Authorization", authCode).build();

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
                    Position tempPosition;
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
                    });
                    System.out.println("Response 1 response:          " + response1);
                    System.out.println("Response 1 cache response:    " + response1.cacheResponse());
                    System.out.println("Response 1 network response:  " + response1.networkResponse());
                    return tempPosition;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return new Position();//send list of nums to postExecute
        }

        protected void onProgressUpdate(Double... values) {

        }

        protected void onPostExecute(Position tempPosition) {//when doInBackground is done executing
            super.onPostExecute(tempPosition);
            System.out.println("ADDed: " + tempPosition.symbol);
positions.add(tempPosition);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void run() {



        }
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
      Account getCurrentAccount();
      void setPositionToBuy();
    }
}