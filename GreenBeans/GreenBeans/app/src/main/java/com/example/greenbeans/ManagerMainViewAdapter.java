package com.example.greenbeans;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        System.out.println("Trying " + clients.get(0).accounts.get(0).refreshToken + " _ " + clients.size() + " _ " + clients.get(0).accounts.size());
        holder.name.setText(clients.get(position).name);
        holder.name.setOnClickListener(new View.OnClickListener() {
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
        TextView phone;
        Button delete;
        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.symbolTV);

        }
    }
    public interface IListener{
        void setCurrentClient(Client client);
    }
}
