package me.levar.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import me.levar.fragment.MixPanelHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Helena
 * Date: 15/08/13
 * Time: 22:58
 * To change this template use File | Settings | File Templates.
 */
public class LevarmeActivity extends FragmentActivity {

    public static final String CURRENT_USER_FILE = "CurrentUserFile";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, MixPanelHelper.MIXPANEL_TOKEN);

        // To preserve battery life, the Mixpanel library will store
        // events rather than send them immediately. This means it
        // is important to call flush() to send any unsent events
        // before your application is taken out of memory.
        mixpanel.flush();
    }

}