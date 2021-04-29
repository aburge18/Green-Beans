package com.example.greenbeans;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class ConfirmSellFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    TextView confirmTV;
    Double priceVal;
    Position tempPosition;
    String symbol;
    String quantity;

    public ConfirmSellFragment() {
        // Required empty public constructor
    }

    public static ConfirmSellFragment newInstance(String param1, String param2) {
        ConfirmSellFragment fragment = new ConfirmSellFragment();
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
        View view = inflater.inflate(R.layout.fragment_confirm_sell, container, false);
        confirmTV = view.findViewById(R.id.confirmSellMsgTV);
        quantity = mListener.getConfirmBuyQuantity();
        tempPosition = mListener.getPositionToBuy();
        symbol = tempPosition.symbol;

        new GetPrice().execute();//get most recent stock price

        Button confirmBtn = view.findViewById(R.id.confirmSellBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new SellPosition().execute();//start buy order
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Button cancelBtn = view.findViewById(R.id.cancelConfirmSellBtn);
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String confirmStr = "Sell: " + quantity + " of " + symbol + " for $" + price;
                    confirmTV.setText(confirmStr);
                }
            });
        }
        @Override
        public void run() {}
    }
    public class SellPosition extends AsyncTask<Integer, Double, Position> implements Runnable {//buys current quantity of current position

        public SellPosition() throws JSONException {
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public Position doInBackground(Integer... values) {

            Account account = mListener.getCurrentAccount();
            try {
                account.sellPosition(quantity, tempPosition);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                mListener.getCurrentAccount().addPositions();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
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