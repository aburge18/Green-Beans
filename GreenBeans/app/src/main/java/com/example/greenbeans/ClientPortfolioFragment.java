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

import com.example.greenbeans.authentication.PasswordResetFragment;

import java.util.ArrayList;


public class ClientPortfolioFragment extends Fragment {

    Client currentClient;
    RecyclerView recyclerView;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ClientPortfolioViewAdapter adapter;
    LinearLayoutManager layoutManager;

    public ClientPortfolioFragment() {
        // Required empty public constructor
    }

    public static ClientPortfolioFragment newInstance(String param1, String param2) {
        ClientPortfolioFragment fragment = new ClientPortfolioFragment();
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

        View view = inflater.inflate(R.layout.fragment_client_portfolio, container, false);
        recyclerView = view.findViewById(R.id.clientRecView);
        currentClient = mListener.getCurrentClient();
        if(currentClient.accounts.size() == 0){
            new GetAccounts().execute();
            System.out.println("Executing get account");
        }

        Button addAccout = view.findViewById(R.id.addAccountBtn);

        addAccout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.setAddAccount("TD");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new ClientAddAccountFragment(), "Add Account").addToBackStack(null).commit();

            }
        });
        Button addAlpacaAccount = view.findViewById(R.id.addAccountBtn2);
        addAlpacaAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new AddAlpacaAccountFragment(), "Add Alpaca Account").addToBackStack(null).commit();
            }
        });
        Button addManager = view.findViewById(R.id.addManagerBtn);
        if(mListener.getUserType().matches("manager")){
            addManager.setVisibility(View.INVISIBLE);
            addAccout.setVisibility(View.INVISIBLE);
        }
        addManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new AssignManagerFragment(), "Assign Manager").addToBackStack(null).commit();

            }
        });
        adapter = new ClientPortfolioViewAdapter(currentClient.accounts, getContext());
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        return view;
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
        Client getCurrentClient();
        String getUserType();
        void setAddAccount(String addAccount);
    }

    public class GetAccounts extends AsyncTask<Integer, Double, String> implements Runnable {//function to update adapter until gains are calculated

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public String doInBackground(Integer... ints) {
currentClient.getAccounts();
            String price = "";

            return price;
        }

        protected void onProgressUpdate(Double... values) {}

        protected void onPostExecute(String price) {//when doInBackground is done executing
            System.out.println("ERROROR: " + price);
            super.onPostExecute(price);
            try {
                Thread.sleep(550);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void run() {}
    }
}