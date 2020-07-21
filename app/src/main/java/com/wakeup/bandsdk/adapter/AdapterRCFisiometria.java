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

import java.util.List;

public class AdapterRCFisiometria extends RecyclerView.Adapter<AdapterRCFisiometria.ViewHolderFisiometria> {
    List<DataFisiometria> listFisiome;
    public AdapterRCFisiometria(List<DataFisiometria> listFisiome) {
        this.listFisiome=listFisiome;



    }

    @NonNull
    @Override
    public AdapterRCFisiometria.ViewHolderFisiometria onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ritmo_cardiaco, null, false);
        return new ViewHolderFisiometria(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRCFisiometria.ViewHolderFisiometria holder, int position) {

        holder.progressBarRc.setProgress(listFisiome.get(position).getRitmoCardiaco());
        holder.txt1.setText(Integer.toString(listFisiome.get(position).getRitmoCardiaco()));


    }

    @Override
    public int getItemCount() {
        return listFisiome.size();
    }

    public class ViewHolderFisiometria extends RecyclerView.ViewHolder {
        ProgressBar progressBarRc;
        TextView txt1;


        public ViewHolderFisiometria(@NonNull View itemView) {
            super(itemView);
            progressBarRc=itemView.findViewById(R.id.p_Bar_ritmo_cardiaco);
            txt1=itemView.findViewById(R.id.txt1);


        }
    }
}
