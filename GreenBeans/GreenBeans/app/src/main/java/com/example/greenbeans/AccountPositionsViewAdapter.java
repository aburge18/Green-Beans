package com.example.greenbeans;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.security.PublicKey;
import java.util.ArrayList;

public class AccountPositionsViewAdapter extends RecyclerView.Adapter<AccountPositionsViewAdapter.ProfileViewHolder> {

    ArrayList<Position> positions;
    Context context;
    IListener mListener;
    public AccountPositionsViewAdapter(ArrayList<Position> positions, Context context){
        this.positions = positions;
        mListener = (IListener) context;
        this.context = context;
    }

    @NonNull
    @Override
    public AccountPositionsViewAdapter.ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.position_row_item, parent, false);
        AccountPositionsViewAdapter.ProfileViewHolder profileViewHolder = new AccountPositionsViewAdapter.ProfileViewHolder(view);
        return profileViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AccountPositionsViewAdapter.ProfileViewHolder holder, int position) {
Position currPosition = positions.get(position);
        holder.posSymbolTV.setText(currPosition.symbol);
        holder.posBuyPriceTV.setText(currPosition.buyPrice);
        holder.posQuantityTV.setText(currPosition.quantity);
    }

    @Override
    public int getItemCount() {
        if (positions == null){
            return 0;
        }else{
            return this.positions.size();
        }
    }


    public static class ProfileViewHolder extends RecyclerView.ViewHolder{

        TextView posBuyPriceTV, posSymbolTV, posQuantityTV;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);

            posBuyPriceTV = itemView.findViewById(R.id.posBuyPriceTV);

            posSymbolTV = itemView.findViewById(R.id.posSymbolTV);
            posQuantityTV = itemView.findViewById(R.id.posQuantityTV);
        }
    }
    public interface IListener{

    }
}
