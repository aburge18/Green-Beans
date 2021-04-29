package com.example.greenbeans;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;



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
        new SetPrice().execute(position);
        System.out.println("Executing");
        if (currPosition.currentPrice != null){
            holder.currentStockPriceTV.setText(currPosition.currentPrice);
        }

        holder.buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.setPositionToBuy(currPosition);
            }
        });
        holder.sellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.setPositionToSell(currPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (positions == null){
            return 0;
        }else{
            return this.positions.size();
        }
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder{//initialize all view elements

        TextView posBuyPriceTV, posSymbolTV, posQuantityTV, currentStockPriceTV;
        Button buyBtn, sellBtn;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);

            posBuyPriceTV = itemView.findViewById(R.id.posBuyPriceTV);
            buyBtn = itemView.findViewById(R.id.buyPosBtn);
            sellBtn = itemView.findViewById(R.id.sellPosBtn);
            posSymbolTV = itemView.findViewById(R.id.posSymbolTV);
            posQuantityTV = itemView.findViewById(R.id.posQuantityTV);
            currentStockPriceTV = itemView.findViewById(R.id.currentStockPriceTV);
        }
    }

    public interface IListener{
        void setPositionToBuy(Position positionToBuy);
        void setPositionToSell(Position positionToSell);
    }

    public class SetPrice extends AsyncTask<Integer, Double, ArrayList<Integer>> implements Runnable {//retrieves current price from td ameritrade

        Position currentPosition;
        Integer position;
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public ArrayList<Integer> doInBackground(Integer... ints) {

            position = ints[0];
            currentPosition = positions.get(ints[0]);
            currentPosition.setCurrentPrice();
            int time = currentPosition.countDownTime;

            ArrayList<Integer> list = new ArrayList<>();
            list.add(time);
            list.add(position);
            return  list;
        }

        protected void onProgressUpdate(Double... values) {}

        protected void onPostExecute(ArrayList<Integer> list) {//when doInBackground is done executing
            super.onPostExecute(list);

            System.out.println("Price:  " + list.get(0));
            if (list.get(0) != 0){
                notifyDataSetChanged();
            }else{
                new SetPrice().execute(list.get(1));
            }

        }

        @Override
        public void run() {}
    }
}
