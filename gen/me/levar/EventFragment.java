package me.levar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class EventFragment extends Fragment {

    private ListView eventsListView;
    private FriendFragment friendFragment;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.events, container, false);

        eventsListView = (ListView) view.findViewById(R.id.eventsList);
        carregarEventos();


        return view;
    }

    private void carregarEventos() {

        Session session = Session.getActiveSession();

        if (session != null) {

            StringBuilder builder = new StringBuilder();
            builder.append(" SELECT name, start_time, eid FROM event WHERE eid IN ");
            builder.append(" (SELECT eid FROM event_member WHERE uid = me()) ");
            builder.append(" ORDER BY start_time DESC ");

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

                                ArrayAdapter<String> eventsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.rowevent, new ArrayList<String>(eventosLevarMe.keySet()));
                                eventsListView.setAdapter(eventsAdapter);
                                eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                {
                                    public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
                                    {
                                        String nomeEvento = (String) eventsListView.getItemAtPosition(position);
                                        String idEvento = eventosLevarMe.get(nomeEvento);

                                        //Mudar para a view de amigos
                                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                        friendFragment = new FriendFragment(idEvento, nomeEvento);
                                        transaction.replace(android.R.id.content, friendFragment);
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                    }
                                });

                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    });

            Request.executeBatchAsync(request);

        }

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