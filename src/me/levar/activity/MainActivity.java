package me.levar.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;
import com.bugsense.trace.BugSenseHandler;
import com.google.android.gcm.GCMRegistrar;
import me.levar.R;
import me.levar.fragment.FaceFragment;
import me.levar.gcm.CommonUtilities;
import me.levar.gcm.ServerUtilities;

public class MainActivity extends FragmentActivity {

    private FaceFragment faceFragment;
    private AsyncTask<Void, Void, Void> mRegisterTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerGCM();

        BugSenseHandler.initAndStartSession(this, "d0435732");

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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

    @Override
    protected void onResume() {
        com.facebook.Settings.publishInstallAsync(this, getResources().getString(R.string.app_id));
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        unregisterReceiver(mHandleMessageReceiver);
        GCMRegistrar.onDestroy(this);
        super.onDestroy();
    }

    private void registerGCM() {
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);

        registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
        } else {
            if (!GCMRegistrar.isRegisteredOnServer(this)) {
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        ServerUtilities.register(context, regId);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }
    }

    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
                }
            };


}