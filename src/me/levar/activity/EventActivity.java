package me.levar.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.bugsense.trace.BugSenseHandler;
import com.facebook.*;
import com.facebook.widget.WebDialog;
import me.levar.R;
import me.levar.adapter.EventAdapter;
import me.levar.fragment.JsonHelper;
import me.levar.fragment.MixPanelHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: Helena
 * Date: 15/08/13
 * Time: 00:22
 * To change this template use File | Settings | File Templates.
 */
public class EventActivity extends LevarmeActivity {

    private EventAdapter<Evento> eventosAdapter;
    private ListView eventsListView;
    private ProgressDialog spinner;
    private static final String MSG_ERROR_USER_WITHOUT_EVENTS = "You don't seem to have any parties lined up.";
    private static final String MSG_ERROR_NO_INTERNET = "No internet detected :(";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.share:
                enviarNotificacao();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.events);

        setTitle("   Which party?");

        eventsListView = (ListView) findViewById(R.id.eventList);

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

                            final List<Evento> eventos = getEventosLevarMe(response);

                            eventosAdapter = new EventAdapter<Evento>(EventActivity.this, R.layout.rowevent, eventos);
                            eventsListView.setAdapter(eventosAdapter);
                            eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

                                    BugSenseHandler.sendEvent("Clicou no evento");
                                    MixPanelHelper.sendEvent(EventActivity.this, "Clicou no evento");

                                    Evento evento = (Evento) eventsListView.getItemAtPosition(position);

                                    if (evento != null) {
                                        irParaTelaDeParticipantesDoEvento(evento.getEid(), evento.getNome());
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

    private void enviarNotificacao() {

        Bundle params = new Bundle();
        String message = "Venha participar do Levar.me";
        params.putString("message", message);

        WebDialog requestsDialog = (
                new WebDialog.RequestsDialogBuilder(this,
                        Session.getActiveSession(),
                        params))
                .setTitle("Chame seus amigos")
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values, FacebookException error) {
                        if (error != null) {
                                BugSenseHandler.sendEvent("Share Cancelado");
                                MixPanelHelper.sendEvent(EventActivity.this, "Share Cancelado");
                        } else {
                            final String requestId = values.getString("request");
                            if (requestId != null) {
                                BugSenseHandler.sendEvent("Share Enviado");
                                MixPanelHelper.sendEvent(EventActivity.this, "Share Enviado");
                                Toast.makeText(EventActivity.this.getApplicationContext(), "Seus amigos foram convidados com sucesso", Toast.LENGTH_SHORT).show();
                            } else {
                                BugSenseHandler.sendEvent("Share Cancelado");
                                MixPanelHelper.sendEvent(EventActivity.this, "Share Cancelado");
                            }
                        }
                    }

                })
                .build();
        requestsDialog.show();
    }

}