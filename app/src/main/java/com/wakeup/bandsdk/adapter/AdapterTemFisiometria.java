package com.wakeup.bandsdk.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
       // holder.t1.setText(Integer.toString(listFisiome.get(position).getOximetria()));
        holder.p_Bar_temp.setProgress(listFisiome.get(position).getOximetria());
        holder.t4.setText(listFisiome.get(position).getOximetria());

    }

    @Override
    public int getItemCount() {
        return listFisiome.size();
    }

    public class ViewHolderFisiometria extends RecyclerView.ViewHolder {

        TextView t4;
        ProgressBar p_Bar_temp;

        public ViewHolderFisiometria(@NonNull View itemView) {
            super(itemView);
          //  t1 = itemView.findViewById(R.id.txt1);
            p_Bar_temp=itemView.findViewById(R.id.p_Bar_temp);
            t4 = itemView.findViewById(R.id.txt4);



        }
    }
}