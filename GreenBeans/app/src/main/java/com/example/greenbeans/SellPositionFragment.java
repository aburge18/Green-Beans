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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellPositionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellPositionFragment extends Fragment {

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
    Position currentPosition;
    public SellPositionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SellPositionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SellPositionFragment newInstance(String param1, String param2) {
        SellPositionFragment fragment = new SellPositionFragment();
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
        View view = inflater.inflate(R.layout.fragment_sell_position, container, false);
        EditText quatityETV = view.findViewById(R.id.buyQuantityETV);
        TextView buySymobolTV = view.findViewById(R.id.buySymbolTV);
        TextView buyAmountTV = view.findViewById(R.id.buyAmountTV);
        TextView buyPositionTV = view.findViewById(R.id.buyPositionTV);
        EditText searchETV = view.findViewById(R.id.buySearchETV);
        TextView buyTotalTV = view.findViewById(R.id.buyTotalTV);
        buyPriceTV = view.findViewById(R.id.buyPriceTV);
        currentPosition = mListener.getPositionToBuy();

        quantity = 0.0;
        if(currentPosition != null){//if position to buy was already specified
            new GetPositionPrice().execute();
            positionSymbol = currentPosition.symbol;
        }

        searchETV.setOnFocusChangeListener(new View.OnFocusChangeListener() {//if user clicks or unclicks search feature
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!searchETV.getText().toString().matches("")) {//search bar is not empty

                    currentPosition = new Position();//create new position with specified symbol
                    currentPosition.symbol = searchETV.getText().toString().toUpperCase();
                    new GetPositionPrice().execute();//get the positions current price
                    positionSymbol = searchETV.getText().toString();
                    //buySymobolTV.setText(positionSymbol);
                    //buyPositionTV.setText(positionSymbol);
                }
            }
        });

        Button buyPositionButton = view.findViewById(R.id.buyPositionBtn);
        buyPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0){
                    Position tempPosition = new Position();
                    tempPosition.symbol = buySymobolTV.getText().toString();
                    mListener.confirmSell(quantity, tempPosition);
                }
            }
        });

        buySymobolTV.setText(positionSymbol);
        quatityETV.setText("0");
        buyPositionTV.setText(positionSymbol);
        buyAmountTV.setText("0");
        buyTotalTV.setText("0.00");

        quatityETV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!quatityETV.getText().toString().matches("")) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    quantity = Double.valueOf(quatityETV.getText().toString());
                    String quantStr = String.format("%.0f", quantity);
                    buyAmountTV.setText(quantStr);
                    Double total = quantity * priceVal;
                    buyTotalTV.setText(df.format(total));
                }
            }
        });
        return view;
    }
    public class GetPositionPrice extends AsyncTask<Integer, Double, String> implements Runnable {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public String doInBackground(Integer... values) {
            currentPosition.setCurrentPrice();
            return currentPosition.currentPrice;
        }

        protected void onProgressUpdate(Double... values) {}

        protected void onPostExecute(String price) {//when doInBackground is done executing
            super.onPostExecute(price);

            System.out.println("Price:  " + price);
            if(price != null) {
                priceVal = Double.valueOf(price);
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buyPriceTV.setText(price);
                    }
                });
            }else{
                Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Please Enter A Valid Position", Toast.LENGTH_SHORT);
                toast1.show();
            }
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
        Position getPositionToBuy();
        void confirmBuy(Double quantity, Position position);
        void confirmSell(Double quantity, Position position);
    }
}