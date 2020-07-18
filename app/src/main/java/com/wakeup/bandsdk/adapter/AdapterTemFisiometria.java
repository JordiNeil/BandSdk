package com.wakeup.bandsdk.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wakeup.bandsdk.Pojos.Fisiometria.DataFisiometria;
import com.wakeup.bandsdk.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterTemFisiometria extends RecyclerView.Adapter<AdapterTemFisiometria.ViewHolderFisiometria> {
    List<DataFisiometria> listFisiome;

    public AdapterTemFisiometria(List<DataFisiometria> listFisiome) {
        System.out.println("aaaaaaaaaaa"+listFisiome);
        this.listFisiome = listFisiome;

    }

    @NonNull
    @Override
    public ViewHolderFisiometria onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tem, null, false);
        return new ViewHolderFisiometria(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderFisiometria holder, int position) {
        holder.t1.setText(Integer.toString(listFisiome.get(position).getOximetria()));
        holder.t2.setText(Integer.toString(listFisiome.get(position).getRitmoCardiaco()));
        holder.t3.setText(Integer.toString(listFisiome.get(position).getPresionArterialDiastolica()));
        holder.t4.setText(Integer.toString(listFisiome.get(position).getPresionArterialSistolica()));
        holder.t5.setText(Float.toString( listFisiome.get(position).getTemperatura()));
        holder.t6.setText(listFisiome.get(position).getFechaToma());
    }

    @Override
    public int getItemCount() {
        return listFisiome.size();
    }

    public class ViewHolderFisiometria extends RecyclerView.ViewHolder {

        TextView t1, t2, t3, t4, t5, t6;

        public ViewHolderFisiometria(@NonNull View itemView) {
            super(itemView);
            t1 = itemView.findViewById(R.id.txt1);
            t2 = itemView.findViewById(R.id.txt2);
            t3 = itemView.findViewById(R.id.txt3);
            t4 = itemView.findViewById(R.id.txt4);
            t5 = itemView.findViewById(R.id.txt5);
            t6 = itemView.findViewById(R.id.txt6);


        }
    }
}