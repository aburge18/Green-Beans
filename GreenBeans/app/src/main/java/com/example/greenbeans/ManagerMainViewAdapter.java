package com.example.greenbeans;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ManagerMainViewAdapter extends RecyclerView.Adapter<ManagerMainViewAdapter.ProfileViewHolder> {

    ArrayList<Client> clients;
    IListener mListener;

    public ManagerMainViewAdapter(ArrayList<Client> clients, Context context){
        this.clients = clients;
        mListener = (IListener) context;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clients_row_item, parent, false);
        ProfileViewHolder profileViewHolder = new ProfileViewHolder(view);

        return profileViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Client client = clients.get(position);
        holder.name.setText(client.name);
        holder.email.setText(client.email);

        if (client.gainsStr != null){
            holder.totalGainsTV.setText(client.gainsStr);
        }else{
            new GetGains().execute();//function to update adapter until gains are calculated
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mListener.setCurrentClient(clients.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        if (clients == null){
            return 0;
        }else {
            return this.clients.size();
        }
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView email;
        TextView totalGainsTV;
        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.clientNameTV);
            email = itemView.findViewById(R.id.clientEmailTV);
            totalGainsTV = itemView.findViewById(R.id.totalGainsTV);
        }
    }

    public interface IListener{
        void setCurrentClient(Client client);
    }

    public class GetGains extends AsyncTask<Integer, Double, String> implements Runnable {//function to update adapter until gains are calculated

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public String doInBackground(Integer... ints) {

            String price = "";

            return price;
        }

        protected void onProgressUpdate(Double... values) {}

        protected void onPostExecute(String price) {//when doInBackground is done executing
            System.out.println("ERROROR: " + price);
            super.onPostExecute(price);
            notifyDataSetChanged();
        }

        @Override
        public void run() {}
    }
}
