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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClientPortfolioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClientPortfolioFragment extends Fragment {

    Client currentClient;

    RecyclerView recyclerView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ClientPortfolioViewAdapter adapter;
    LinearLayoutManager layoutManager;
    ArrayList<Account> accounts = new ArrayList<>();
    public ClientPortfolioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClientPortfolioFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        adapter = new ClientPortfolioViewAdapter(accounts, getContext());

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        for (int i = 0; i < currentClient.accounts.size(); i++){
            accounts.add(currentClient.accounts.get(i));
            System.out.println("Added account: " + accounts.get(i).refreshToken);
            adapter.notifyDataSetChanged();
        }


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
        void setCurrentAccount(Account account);
    }



}