package me.levar.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import me.levar.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Helena
 * Date: 15/08/13
 * Time: 00:22
 * To change this template use File | Settings | File Templates.
 */
public class EventActivity extends LevarmeActivity {

    private ListView eventsListView;
    private ProgressDialog spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.events);

        setTitle("   Which party?");

        eventsListView = (ListView) findViewById(R.id.eventsList);

        spinner = new ProgressDialog(this);
        spinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        spinner.setMessage(getString(com.facebook.android.R.string.com_facebook_loading));

        carregarEventos();

    }

    private void carregarEventos() {

        spinner.show();

        Session session = Session.getActiveSession();

        if (session != null) {

            StringBuilder builder = new StringBuilder();
            builder.append(" SELECT name, start_time, eid FROM event WHERE eid IN ");
            builder.append(" (SELECT eid FROM event_member WHERE uid = me()) ");
            builder.append(" AND start_time >= now() ");
            builder.append(" ORDER BY start_time ");

            String fqlQuery = builder.toString();

            Bundle params = new Bundle();
            params.putString("q", fqlQuery);
            params.putString("access_token", session.getAccessToken());

            Request request = new Request(session,
                    "/fql",
                    params,
                    HttpMethod.GET,
                    new Request.Callback(){
                        public void onCompleted(Response response) {

                            try {
                                final Map<String, String> eventosLevarMe = getEventosLevarMe(response);

                                ArrayAdapter<String> eventsAdapter = new ArrayAdapter<String>(EventActivity.this, R.layout.rowevent, new ArrayList<String>(eventosLevarMe.keySet()));
                                eventsListView.setAdapter(eventsAdapter);
                                eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                {
                                    public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
                                    {
                                        String nomeEvento = (String) eventsListView.getItemAtPosition(position);
                                        String idEvento = eventosLevarMe.get(nomeEvento);

                                        irParaTelaDeParticipantesDoEvento(idEvento, nomeEvento);
                                    }
                                });

                                spinner.dismiss();

                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    });

            Request.executeBatchAsync(request);

        }

    }

    private void irParaTelaDeParticipantesDoEvento(String idEvento, String nomeEvento) {

        //Mudar para a view de amigos
        Intent intent = new Intent(this, FriendActivity.class);
        intent.putExtra("idEvento", idEvento);
        intent.putExtra("nomeEvento", nomeEvento);
        startActivity(intent);

    }

    private Map<String, String> getEventosLevarMe(Response response) throws JSONException {

        GraphObject graphObject  = response.getGraphObject();
        JSONObject jsonObject = graphObject.getInnerJSONObject();
        JSONArray eventsByFacebook = jsonObject.getJSONArray("data");

        Map<String, String> retorno = new LinkedHashMap<String, String>();

        for (int i = 0; i < (eventsByFacebook.length()); i++) {
            JSONObject obj = eventsByFacebook.getJSONObject(i);

            String name = obj.getString("name");
            String id = obj.getString("eid");

            retorno.put(name, id);
        }

        return retorno;
    }

}
