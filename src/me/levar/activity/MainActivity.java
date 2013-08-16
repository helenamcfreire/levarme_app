package me.levar.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import me.levar.fragment.FaceFragment;

public class MainActivity extends FragmentActivity {

    private FaceFragment faceFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            faceFragment = new FaceFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, faceFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            faceFragment = (FaceFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }

    }

}