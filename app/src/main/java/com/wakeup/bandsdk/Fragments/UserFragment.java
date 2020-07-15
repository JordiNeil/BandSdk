package com.wakeup.bandsdk.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.wakeup.bandsdk.MainActivity;
import com.wakeup.bandsdk.R;
import com.wakeup.bandsdk.activity.PacienteActivity;
import com.wakeup.mylibrary.service.BluetoothService;

public class UserFragment extends Fragment {
    LinearLayout ll_device_manager, ll_device_disconnect;
    ImageView iv_head_portrait;
    View viewAlert;
    AlertDialog.Builder builder;
    AlertDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentview = inflater.inflate(R.layout.fragment_user, container, false);


        viewAlert = LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog_sesion_out, null);
        builder = new AlertDialog.Builder(getActivity());
        builder.setView(viewAlert);
        builder.setCancelable(false);
        dialog = builder.create();


        ll_device_manager = fragmentview.findViewById(R.id.ll_device_manager);
        ll_device_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivityConection();
            }
        });
        iv_head_portrait = fragmentview.findViewById(R.id.iv_head_portrait);

        iv_head_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActitivyPerfil();
            }
        });
        ll_device_disconnect = fragmentview.findViewById(R.id.ll_device_disconnect);
        ll_device_disconnect.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dialog.show();
                Button btnAcep = viewAlert.findViewById(R.id.btn_acep_disconnect);
                btnAcep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        disconnectDevice();
                    }
                });
                return false;
            }
        });


        return fragmentview;
        /*if (getArguments()!=null){
         //  String a= getArguments().getString("a");
        }*/


    }

    public void getActivityConection() {
        System.out.println("cambio de actividad");
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);

    }

    public void getActitivyPerfil() {
        System.out.println("cambio de actividad a paciente");
        Intent intent = new Intent(getActivity(), PacienteActivity.class);
        startActivity(intent);

    }


    public void disconnectDevice() {

        dialog.dismiss();
    }


}
