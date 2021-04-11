package com.example.greenbeans;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ClientPortfolioViewAdapter extends RecyclerView.Adapter<ClientPortfolioViewAdapter.ProfileViewHolder> {
    ArrayList<Account> accounts;
    Context context;
    IListener mListener;

    public ClientPortfolioViewAdapter(ArrayList<Account> accounts, Context context){
        this.accounts = accounts;
        mListener = (IListener) context;
        this.context = context;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_portfolio_row_item, parent, false);
        ProfileViewHolder profileViewHolder = new ProfileViewHolder(view);
        return profileViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Account account = accounts.get(position);
        String accountType = "";
        switch (account.accountType){
            case "TD":
                accountType = "TD Ameritrade";
                break;
            case "Fidelity":
                accountType = account.accountType;
                break;
        }

        holder.accountType.setText(accountType);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.setCurrentAccount(account);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (accounts == null){
            return 0;
        }else{
            return this.accounts.size();
        }
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder{
        TextView accountType;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            accountType = itemView.findViewById(R.id.accountTypeTV);
        }
    }

    public interface IListener{
        void setCurrentAccount(Account account);
    }
}
