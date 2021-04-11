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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;



public class AccountPositionsFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    AccountPositionsViewAdapter adapter;
    LinearLayoutManager layoutManager;
    Account currentAccount;


    public AccountPositionsFragment() {
        // Required empty public constructor
    }

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

        currentAccount =  mListener.getCurrentAccount();
        currentAccount.positions = new ArrayList<>();

        adapter = new AccountPositionsViewAdapter(currentAccount.positions, getContext());
        recyclerView = view.findViewById(R.id.positionRecView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        new SetAuthToken().execute();

        Button addPositionsBtn = view.findViewById(R.id.addPositionBtn);
        addPositionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.setPositionToBuy();
            }
        });

        return view;
    }




    public class SetAuthToken extends AsyncTask<Integer, Double, Position> implements Runnable {//set accounts auth token for future


        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public Position doInBackground(Integer... values) {
            currentAccount.setAuthTokenViaRefresh();//create auth token in account obj
            return new Position();
        }

        protected void onProgressUpdate(Double... values) {

        }

        protected void onPostExecute(Position tempPosition) {//when doInBackground is done executing
            super.onPostExecute(tempPosition);

            new SetPositions().execute();
            adapter.notifyDataSetChanged();
        }

        @Override
        public void run() {}
    }

    public class SetPositions extends AsyncTask<Integer, Double, Position> implements Runnable {//get positions in account

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public Position doInBackground(Integer... values) {

            currentAccount.addPositions();//searched td database and adds all position to linked account obj
            return new Position();//send list of nums to postExecute
        }

        protected void onProgressUpdate(Double... values) {}

        protected void onPostExecute(Position tempPosition) {//when doInBackground is done executing
            super.onPostExecute(tempPosition);
            adapter.notifyDataSetChanged();
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
      void setPositionToBuy();
    }
}