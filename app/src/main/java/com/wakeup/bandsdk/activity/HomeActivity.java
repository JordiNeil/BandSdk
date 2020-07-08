package com.wakeup.bandsdk.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.wakeup.bandsdk.Fragments.HomeFragment;
import com.wakeup.bandsdk.Fragments.UserFragment;
import com.wakeup.bandsdk.R;

public class HomeActivity extends AppCompatActivity {
    public RadioButton radioButtonHome;
    public RadioButton radioButtonInfo;
    public RadioButton radioButtonUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        radioButtonHome = (RadioButton) findViewById(R.id.rb_home);
        radioButtonHome.callOnClick();
        radioButtonInfo = (RadioButton) findViewById(R.id.rb_discover);
        radioButtonInfo.callOnClick();
        radioButtonUser = (RadioButton) findViewById(R.id.rb_mine);
        radioButtonUser.callOnClick();

        /*FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fl_fragment_container, fragmentHome);
        fragmentTransaction.commit();*/
    }


    public void fragmentHome(View view) {
        Fragment fragmentHome = new HomeFragment();
        if (radioButtonHome.isChecked() == true) {
            System.out.println("cambio a home");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_fragment_container, fragmentHome);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }


    }

    public void fragmentInfo(View view) {

    }

    public void fragmentUser(View view) {
        Fragment fragmentUser = new UserFragment();
        Bundle args = new Bundle();
        if (radioButtonUser.isChecked() == true) {
            System.out.println("cambio a user");
            args.putString("a", "");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_fragment_container, fragmentUser);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }
    }

}