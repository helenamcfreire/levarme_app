package me.levar.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;
import com.bugsense.trace.BugSenseHandler;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import me.levar.fragment.FaceFragment;

public class MainActivity extends FragmentActivity {

    public static final String MIXPANEL_TOKEN = "571ec076911ca93d1c6c1a2e429049d1";

    private FaceFragment faceFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BugSenseHandler.initAndStartSession(this, "d0435732");
        BugSenseHandler.setLogging(5000);
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, MIXPANEL_TOKEN);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (savedInstanceState == null) {
            BugSenseHandler.sendEvent("Criando a tela de login...");
            // Add the fragment on initial activity setup
            faceFragment = new FaceFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, faceFragment)
                    .commit();
        } else {
            BugSenseHandler.sendEvent("Criando a tela de login...");
            // Or set the fragment from restored state info
            faceFragment = (FaceFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }

    }

}