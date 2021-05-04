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

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class ConfirmBuyFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    TextView confirmTV;
    Double priceVal;
    Position tempPosition;
    String symbol;
    String quantity;

    public ConfirmBuyFragment() {
        // Required empty public constructor
    }

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
        quantity = mListener.getConfirmBuyQuantity();
        tempPosition = mListener.getPositionToBuy();
        symbol = tempPosition.symbol;

        new GetPrice().execute();//get most recent stock price

        Button confirmBtn = view.findViewById(R.id.confirmBuyBtn);
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

        Button cancelBtn = view.findViewById(R.id.cancelConfirmBuyBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        return view;
    }


    public class GetPrice extends AsyncTask<Integer, Double, String> implements Runnable {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public String doInBackground(Integer... values) {
            String price;
            tempPosition.setCurrentPrice();
            price = tempPosition.currentPrice;
            return price;
        }

        protected void onProgressUpdate(Double... values) {}

        protected void onPostExecute(String price) {//when doInBackground is done executing
            super.onPostExecute(price);
            //Double total = Double.valueOf(price) * Double.valueOf()
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String confirmStr = "Buy: " + quantity + " of " + symbol + " for $" + price;
                    confirmTV.setText(confirmStr);
                }
            });
        }
        @Override
        public void run() {}
    }
    public class BuyPosition extends AsyncTask<Integer, Double, Position> implements Runnable {//buys current quantity of current position

        public BuyPosition() throws JSONException {
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public Position doInBackground(Integer... values) {

            Account account = mListener.getCurrentAccount();
            try {
                account.buyPosition(quantity, tempPosition);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mListener.getPositionToBuy().quantityInt += (int)Double.parseDouble(quantity);

            return new Position();//send list of nums to postExecute
        }

        protected void onProgressUpdate(Double... values) {

        }

        protected void onPostExecute(Position tempPosition) {//when doInBackground is done executing
            super.onPostExecute(tempPosition);


            System.out.println("ADDed: " + tempPosition.symbol);
            getFragmentManager().popBackStack();
            getFragmentManager().popBackStack();
        }

        @Override
        public void run() {}
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
        String getConfirmBuyQuantity();
        Position getPositionToBuy();
    }
}