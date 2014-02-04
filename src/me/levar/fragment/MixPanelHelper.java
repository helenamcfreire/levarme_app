package me.levar.fragment;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

/**
 * Created with IntelliJ IDEA.
 * User: Helena
 * Date: 03/02/14
 * Time: 21:36
 * To change this template use File | Settings | File Templates.
 */
public class MixPanelHelper {

    public static final String MIXPANEL_TOKEN = "571ec076911ca93d1c6c1a2e429049d1";

    public static void sendEvent(android.content.Context context, String eventTitle) {

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN);

        mixpanel.track(eventTitle, null);
    }

}