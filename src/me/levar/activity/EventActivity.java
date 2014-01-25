package me.levar.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import me.levar.R;
import me.levar.adapter.EventAdapter;
import me.levar.fragment.EventAdapterHelper;
import me.levar.fragment.JsonHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: Helena
 * Date: 15/08/13
 * Time: 00:22
 * To change this template use File | Settings | File Templates.
 */
public class EventActivity extends ListActivity {

    private ListView eventsListView;
    private ProgressDialog spinner;
    private static final String MSG_ERROR_USER_WITHOUT_EVENTS = "You don't seem to have any parties lined up.";
    private static final String MSG_ERROR_NO_INTERNET = "No internet detected :(";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.events);

        setTitle("   Which party?");

        eventsListView = (ListView) findViewById(android.R.id.list);

        spinner = new ProgressDialog(this);
        spinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        spinner.setMessage(getString(com.facebook.android.R.string.com_facebook_loading));

        carregarEventos();

        if (!isOnline()) {
            Toast.makeText(this, MSG_ERROR_NO_INTERNET, Toast.LENGTH_LONG).show();
        }

    }

    private void carregarEventos() {

        spinner.show();

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

                            final EventAdapterHelper helper = getEventosLevarMe(response);

                            EventAdapter adapter = createEventAdapter();

                            Map<String, List<String>> eventsGroupByDate = helper.getMapaDataENomesEventos();

                            for (String date : eventsGroupByDate.keySet()) {
                                List<String> eventosNaData = eventsGroupByDate.get(date);
                                adapter.addSection(date, new ArrayAdapter<String>(EventActivity.this, R.layout.rowevent, eventosNaData));
                            }

                            setListAdapter(adapter);

                            eventsListView.setAdapter(adapter);
                            eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

                                    Map<String, String> eventsGroupById = helper.getMapaNomeEIdEvento();

                                    String nomeEvento = (String) eventsListView.getItemAtPosition(position);
                                    String idEvento = eventsGroupById.get(nomeEvento);

                                    if (isNotBlank(nomeEvento) && isNotBlank(idEvento)) {
                                        irParaTelaDeParticipantesDoEvento(idEvento, nomeEvento);
                                    }
                                }
                            });

                            try {
                                spinner.dismiss();
                            } catch (Exception e) {
                                // nothing
                            }

                        }
                    });

            Request.executeBatchAsync(request);

        }

    }

    private EventAdapter createEventAdapter() {
        return new EventAdapter() {
            protected View getHeaderView(String caption, int index, View convertView, ViewGroup parent) {
                TextView result = (TextView) convertView;

                if (convertView == null) {
                    result = (TextView) getLayoutInflater().inflate(R.layout.headerevent, null);
                }

                result.setText(caption);

                return (result);
            }
        };
    }

    private String getFQLQuery() {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT name, start_time, eid FROM event WHERE eid IN ");
        builder.append(" (SELECT eid FROM event_member WHERE uid = me()) ");
        builder.append(" AND start_time >= now() ");
        builder.append(" ORDER BY start_time ");

        return builder.toString();
    }

    private void irParaTelaDeParticipantesDoEvento(String idEvento, String nomeEvento) {

        //Mudar para a view de amigos
        Intent intent = new Intent(this, FriendActivity.class);
        intent.putExtra("idEvento", idEvento);
        intent.putExtra("nomeEvento", nomeEvento);
        startActivity(intent);

    }

    private EventAdapterHelper getEventosLevarMe(Response response) {

        EventAdapterHelper helper = new EventAdapterHelper();

        JSONArray eventsByFacebook = JsonHelper.getJsonArrayNodeData(response);

        Map<String, List<String>> eventsGroupByDate = new LinkedHashMap<String, List<String>>();
        Map<String, String> eventsGroupById = new LinkedHashMap<String, String>();


        if (eventsByFacebook != null) {
            if (eventsByFacebook.length() == 0) {
                Toast.makeText(this, MSG_ERROR_USER_WITHOUT_EVENTS, Toast.LENGTH_LONG).show();
            }

            for (int i = 0; i < (eventsByFacebook.length()); i++) {

                JSONObject obj = JsonHelper.getJsonObject(eventsByFacebook, i);

                String name = JsonHelper.getString(obj, "name");
                String start_time = JsonHelper.getString(obj, "start_time");
                String id = JsonHelper.getString(obj, "eid");

                String date = format_date(start_time);

                eventsGroupById.put(name, id);

                eventsGroupByDate = groupEventsByDate(eventsGroupByDate, name, date);
            }
        } else {
            Toast.makeText(this, MSG_ERROR_USER_WITHOUT_EVENTS, Toast.LENGTH_LONG).show();
        }

        helper.setMapaDataENomesEventos(eventsGroupByDate);
        helper.setMapaNomeEIdEvento(eventsGroupById);

        return helper;
    }


    private Map<String, List<String>> groupEventsByDate(Map<String, List<String>> eventsGroupByDate, String name, String date) {

        boolean dataJaExiste = eventsGroupByDate.containsKey(date);
        if (dataJaExiste) {
            List<String> eventos = eventsGroupByDate.get(date);
            eventos.add(name);
            eventsGroupByDate.put(date, eventos);
        } else {
            List<String> eventos = new ArrayList<String>();
            eventos.add(name);
            eventsGroupByDate.put(date, eventos);
        }

        return eventsGroupByDate;
    }

    public String format_date(String start_time) {

        SimpleDateFormat faceFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;
        try {
            date = faceFormat.parse(start_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat levarmeFormat = new SimpleDateFormat("EEEE - dd/MM", Locale.ENGLISH);

        return levarmeFormat.format(date);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

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