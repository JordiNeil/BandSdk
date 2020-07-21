package com.wakeup.bandsdk.Fragments.Fisiometria;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wakeup.bandsdk.Pojos.Fisiometria.DataFisiometria;
import com.wakeup.bandsdk.R;
import com.wakeup.bandsdk.Services.ServiceFisiometria;
import com.wakeup.bandsdk.adapter.AdapterRCFisiometria;
import com.wakeup.bandsdk.adapter.AdapterTemFisiometria;
import com.wakeup.bandsdk.configVar.ConfigGeneral;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RitmoCardiaco extends Fragment {




    String storedJwtToken;
    Integer userId;
    RecyclerView mRecyclerViewFisiometria;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        storedJwtToken = sharedPrefs.getString(ConfigGeneral.TOKENSHARED, "");
        userId = sharedPrefs.getInt(ConfigGeneral.STOREDUSERID, 0);
        View temFragment = inflater.inflate(R.layout.fragment_ritmo_cardiaco, container, false);

        mRecyclerViewFisiometria=temFragment.findViewById(R.id.rv_ritmo_cardiaco);
        mRecyclerViewFisiometria.setLayoutManager(new LinearLayoutManager(getActivity()));

        getPhysiometryDataById();

        return temFragment;
    }

    public void getPhysiometryDataById() {



        System.out.println("ID DE USUARIO-+++++++++++++++++++++++++" + userId + "\n" + storedJwtToken);

        ServiceFisiometria service = ConfigGeneral.retrofit.create(ServiceFisiometria.class);
        final Call<List<DataFisiometria>> dataResponse = service.getPhysiometryDataAll("Bearer " + storedJwtToken, userId);

        dataResponse.enqueue(new Callback<List<DataFisiometria>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<DataFisiometria>> call, Response<List<DataFisiometria>> response) {


                if (response.isSuccessful()) {
                    List<DataFisiometria> res = response.body();

                    assert res != null;
                    AdapterRCFisiometria mAdapter = new AdapterRCFisiometria(res);
                    mRecyclerViewFisiometria.setAdapter(mAdapter);
//
                }

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onFailure(Call<List<DataFisiometria>> call, Throwable t) {


            }
        });
    }
}