package com.example.greenbeans;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    }
}