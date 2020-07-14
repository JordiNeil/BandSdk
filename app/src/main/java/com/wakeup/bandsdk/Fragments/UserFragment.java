package com.wakeup.bandsdk.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.wakeup.bandsdk.MainActivity;
import com.wakeup.bandsdk.R;
import com.wakeup.bandsdk.activity.PacienteActivity;

public class UserFragment extends Fragment {
    LinearLayout ll_device_manager;
    ImageView iv_head_portrait;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentview=inflater.inflate(R.layout.fragment_user, container, false);

        ll_device_manager=fragmentview.findViewById(R.id.ll_device_manager);
        ll_device_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivityConection();
            }
        });
        iv_head_portrait=fragmentview.findViewById(R.id.iv_head_portrait);

        iv_head_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActitivyPerfil();
            }
        });
        return fragmentview;
        /*if (getArguments()!=null){
         //  String a= getArguments().getString("a");
        }*/


    }

    public void getActivityConection(){
        System.out.println("cambio de actividad");
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);

    }
    public void getActitivyPerfil(){
        System.out.println("cambio de actividad a paciente");
        Intent intent = new Intent(getActivity(), PacienteActivity.class);
        startActivity(intent);

    }






}
