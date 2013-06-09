package me.levar;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

public class FaceActivity extends FragmentActivity {

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
                    .addToBackStack(null)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            faceFragment = (FaceFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            getFragmentManager().popBackStack();
        }

        return super.onKeyDown(keyCode, event);
    }

}