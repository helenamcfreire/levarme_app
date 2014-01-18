package me.levar.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;
import com.bugsense.trace.BugSenseHandler;
import com.testflightapp.lib.TestFlight;
import me.levar.fragment.FaceFragment;

public class MainActivity extends FragmentActivity {

    private FaceFragment faceFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize TestFlight with your app token.
        TestFlight.takeOff(getApplication(), "d5aa0e27-011f-4a33-97e2-7c46263d1b34");

        BugSenseHandler.initAndStartSession(this, "d0435732");

        BugSenseHandler.setLogging(5000);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (savedInstanceState == null) {
            TestFlight.passCheckpoint("Criando a tela de login...");
            BugSenseHandler.sendEvent("Criando a tela de login...");
            // Add the fragment on initial activity setup
            faceFragment = new FaceFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, faceFragment)
                    .commit();
        } else {
            TestFlight.passCheckpoint("Criando a tela de login...");
            BugSenseHandler.sendEvent("Criando a tela de login...");
            // Or set the fragment from restored state info
            faceFragment = (FaceFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }

    }

}