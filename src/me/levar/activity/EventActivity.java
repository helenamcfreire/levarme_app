package me.levar.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import me.levar.CurrentLocation;
import me.levar.R;
import me.levar.entity.Evento;
import me.levar.fragment.JsonHelper;
import me.levar.fragment.MixPanelHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: Helena
 * Date: 15/08/13
 * Time: 00:22
 * To change this template use File | Settings | File Templates.
 */
public class EventActivity extends LevarmeActivity {

    private static final String MSG_ERROR_USER_WITHOUT_EVENTS = "You don't seem to have any parties lined up.";
    private static final String MSG_ERROR_NO_INTERNET = "No internet detected :(";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash);

        TextView waitMessage = (TextView) findViewById(R.id.wait_message);
        Typeface waitFont = Typeface.createFromAsset(getAssets(), "fonts/GothamLight.otf");
        waitMessage.setTypeface(waitFont);

        if (!isOnline()) {
            Toast.makeText(this, MSG_ERROR_NO_INTERNET, Toast.LENGTH_LONG).show();
        }

        findBairroCurrentUser();

        carregarEventos();
    }

    private void findBairroCurrentUser() {
        CurrentLocation.LocationResult locationResult = new CurrentLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){

                Geocoder geocoder = new Geocoder(EventActivity.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    if (!addresses.isEmpty()) {
                        String bairro = addresses.get(0).getSubLocality();

                        if (isNotBlank(bairro)) {
                            SharedPreferences settings = getSharedPreferences(MY_LOCATION_FILE, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("bairroCurrentUser", bairro);
                            editor.commit();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        CurrentLocation currentLocation = new CurrentLocation();
        currentLocation.getLocation(this, locationResult);
    }

    @Override
    protected void onResume() {
        com.facebook.Settings.publishInstallAsync(this, getResources().getString(R.string.app_id));
        super.onResume();
    }

    private void carregarEventos() {

        Session session = Session.getActiveSession();

        if (session != null) {

            Bundle params = new Bundle();
            params.putString("q", getFQLQuery());
            params.putString("access_token", session.getAccessToken());

            Request request = new Request(session,
                    "/fql",
                    params,
                    HttpMethod.GET,
                    new Request.Callback() {
                        public void onCompleted(Response response) {

                            final List<Evento> eventos = getEventosLevarMe(response);

                            Intent intent = new Intent(EventActivity.this, EventListActivity.class);
                            intent.putParcelableArrayListExtra("eventos", (ArrayList<Evento>) eventos);
                            startActivity(intent);

                        }
                    });

            Request.executeBatchAsync(request);

        }

    }

    private String getFQLQuery() {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT name, start_time, eid FROM event WHERE eid IN ");
        builder.append(" (SELECT eid FROM event_member WHERE uid = me()) ");
        builder.append(" AND start_time >= now() ");
        builder.append(" ORDER BY start_time ");

        return builder.toString();
    }

    private List<Evento> getEventosLevarMe(Response response) {

        JSONArray eventsByFacebook = JsonHelper.getJsonArrayNodeData(response);

        List<Evento> eventosLevarMe = new ArrayList<Evento>();

        if (eventsByFacebook != null) {
            if (eventsByFacebook.length() == 0) {
                Toast.makeText(this, MSG_ERROR_USER_WITHOUT_EVENTS, Toast.LENGTH_LONG).show();
            }

            for (int i = 0; i < (eventsByFacebook.length()); i++) {

                JSONObject obj = JsonHelper.getJsonObject(eventsByFacebook, i);

                String id = JsonHelper.getString(obj, "eid");
                String name = JsonHelper.getString(obj, "name");
                String start_time = JsonHelper.getString(obj, "start_time");

                String date = format_date(start_time);

                Evento evento = new Evento(id, name, date);

                eventosLevarMe.add(evento);
            }
        } else {
            Toast.makeText(this, MSG_ERROR_USER_WITHOUT_EVENTS, Toast.LENGTH_LONG).show();
        }

        return eventosLevarMe;
    }

    public String format_date(String start_time) {

        SimpleDateFormat faceFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;
        try {
            date = faceFormat.parse(start_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat levarmeFormat = new SimpleDateFormat("dd/MM", Locale.ENGLISH);

        return levarmeFormat.format(date);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, MixPanelHelper.MIXPANEL_TOKEN);
            mixpanel.flush();

            moveTaskToBack(true);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}