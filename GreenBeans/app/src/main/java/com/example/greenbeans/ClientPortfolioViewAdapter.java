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
            case "Alpaca":
                accountType = account.accountType;
                System.out.println("GAINS BABY  " + mListener.getCurrentClient().gainsStr + account.accountCurrentValStr);
                break;
        }
String gains;
        Double gainsVal = account.accountCurrentVal - account.accountBuyVal;
        gains = String.format("%.2f", gainsVal);

        holder.accountType.setText(accountType);
        holder.accountVal.setText("$" + account.accountCurrentValStr);
        holder.accountProfit.setText("$" + gains);
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
        TextView accountType, accountVal, accountProfit;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            accountType = itemView.findViewById(R.id.accountTypeTV);
            accountProfit = itemView.findViewById(R.id.accountProfitLossTV);
            accountVal = itemView.findViewById(R.id.accountValTV);
        }
    }

    public interface IListener{
        void setCurrentAccount(Account account);
        Client getCurrentClient();
    }

}
