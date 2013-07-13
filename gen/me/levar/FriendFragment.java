package me.levar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FriendFragment extends Fragment {

    private ListView friendsListView;
    private String idEvento;
    private String nomeEvento;
    private NotificationFragment notificationFragment;


    public FriendFragment(String idEvento, String nomeEvento) {
        this.idEvento = idEvento;
        this.nomeEvento = nomeEvento;
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

            List<String> pessoasCadastradasNoLevarMe = getPessoasCadastradasNoLevarMe();

            StringBuilder builder = new StringBuilder();
            builder.append(" {'participantes': 'select uid from event_member where ");
            builder.append(" ( ");
            builder.append(" uid in (select uid1 from friend where uid2 = me()) ");
            builder.append(" OR uid IN ( ");
            builder.append(Arrays.toString(pessoasCadastradasNoLevarMe.toArray()).replace("[", "").replace("]", ""));
            builder.append(" )) ");
            builder.append(" and eid =  ");
            builder.append(idEvento);
            builder.append(" ', ");
            builder.append(" 'nomeparticipante':  'SELECT name, pic_square, uid, mutual_friend_count FROM user WHERE (uid IN (SELECT uid FROM #participantes) ");
            builder.append(" ) ");
            builder.append(" AND uid != me() ");
            builder.append(" ORDER BY name ");
            builder.append(" ',} ");

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

                            List<Pessoa> participantes = null;
                            try {
                                participantes = getParticipantes(response);

                                FriendAdapter<Pessoa> participantesAdapter = new FriendAdapter<Pessoa>(getActivity(), R.layout.rowfriend, participantes);
                                friendsListView.setAdapter(participantesAdapter);
                                friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                {
                                    public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
                                    {
                                        Pessoa amigo = (Pessoa) friendsListView.getItemAtPosition(position);

                                        //Mudar para a view de amigos
                                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                        notificationFragment = new NotificationFragment(amigo.getUid(), nomeEvento);
                                        transaction.replace(android.R.id.content, notificationFragment);
                                        transaction.commit();
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

            Request.executeBatchAsync(request);

        }

    }

    private List<String> getPessoasCadastradasNoLevarMe() {

        List<String> ids = new ArrayList<String>();
        try {
            String pessoasCadastradas = new RequestPessoaTask().execute("http://www.levar.me/pessoa/list").get();
            try {
                JSONArray jsonArray = new JSONArray(pessoasCadastradas);
                for (int i = 0; i < (jsonArray.length()); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String id = obj.getString("uid");
                    ids.add(id);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return ids;
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
            String qtdAmigosEmComum = obj.getString("mutual_friend_count");

            Pessoa amigo = new Pessoa(id, nome, foto, qtdAmigosEmComum);

            amigos.add(amigo);
        }

        return amigos;
    }

}