package me.levar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

public class FriendFragment extends Fragment {


    private ListView friendsListView;
    private String idEvento;


    public FriendFragment(String idEvento) {
        this.idEvento = idEvento;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.friends, container, false);

        friendsListView = (ListView) view.findViewById(R.id.friendsList);
        carregarAmigosQueEstaoNoEvento(idEvento);

        return view;
    }

    private void carregarAmigosQueEstaoNoEvento(String idEvento) {

        Session session = Session.getActiveSession();

        if (session != null) {

            String fqlQuery = "{" +
                    " 'participantes': 'select uid from event_member where uid in (select uid1 from friend where uid2 = me()) and eid = " + idEvento + "', " +
                    " 'nomeparticipante':   'SELECT name, pic_square, uid FROM user WHERE uid IN (SELECT uid FROM #participantes)',}";
            Bundle params = new Bundle();
            params.putString("q", fqlQuery);
            params.putString("access_token", session.getAccessToken());
            Request request = new Request(session,
                    "/fql",
                    params,
                    HttpMethod.GET,
                    new Request.Callback(){
                        public void onCompleted(Response response) {

                            List<Pessoa> participantes = null;
                            try {
                                participantes = getParticipantes(response);

                                FriendAdapter<Pessoa> participantesAdapter = new FriendAdapter<Pessoa>(getActivity(), R.layout.rowfriend, participantes);
                                friendsListView.setAdapter(participantesAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

            Request.executeBatchAsync(request);

        }

    }

    private List<Pessoa> getParticipantes(Response response) throws JSONException {

        GraphObject graphObject  = response.getGraphObject();
        JSONObject jsonObject = graphObject.getInnerJSONObject();
        JSONArray data = jsonObject.getJSONArray("data");
        JSONArray participantesDoEvento = data.getJSONObject(1).getJSONArray("fql_result_set");

        List<Pessoa> amigos = new ArrayList<Pessoa>();
        for (int i = 0; i < (participantesDoEvento.length()); i++) {
            JSONObject obj = participantesDoEvento.getJSONObject(i);

            String id = obj.getString("uid");
            String nome = obj.getString("name");
            String foto = obj.getString("pic_square");

            Pessoa amigo = new Pessoa(id, nome, foto);

            amigos.add(amigo);
        }

        return amigos;
    }

}