package me.levar.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.facebook.*;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import me.levar.R;
import me.levar.adapter.FriendAdapter;
import me.levar.entity.Pessoa;
import me.levar.task.RequestPessoaTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: Helena
 * Date: 15/08/13
 * Time: 01:10
 * To change this template use File | Settings | File Templates.
 */
public class FriendActivity extends LevarmeActivity {

    private FriendAdapter<Pessoa> participantesAdapter;
    private ListView friendsListView;
    private ProgressDialog spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friends);

        friendsListView = (ListView) findViewById(R.id.friendsList);

        setTitle("   Who's going to " + getNomeEvento());

        spinner = new ProgressDialog(this);
        spinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        spinner.setMessage(getString(com.facebook.android.R.string.com_facebook_loading));

        String idEvento = getIdEvento();

        carregarAmigosQueEstaoNoEvento(idEvento);
    }

    private String getIdEvento() {
        Intent intent = getIntent();
        return intent.getStringExtra("idEvento");
    }

    private String getNomeEvento() {
        Intent intent = getIntent();
        return intent.getStringExtra("nomeEvento");
    }

    private void carregarAmigosQueEstaoNoEvento(String idEvento) {

        spinner.show();

        final Session session = Session.getActiveSession();

        if (session != null) {

            List<String> pessoasCadastradasNoLevarMe = getPessoasCadastradasNoLevarMe();

            StringBuilder builder = new StringBuilder();
            builder.append(" {'participantes': 'select uid from event_member where ");
            builder.append(" ( ");
            builder.append(" uid in (select uid1 from friend where uid2 = me()) ");
            builder.append(" OR uid IN ( ");
            builder.append(pessoasCadastradasNoLevarMe.toString().replace("[", "").replace("]", ""));
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

            final Bundle params = new Bundle();
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
                                participantes = getParticipantesDoEventoNoFace(response);

                                participantesAdapter = new FriendAdapter<Pessoa>(FriendActivity.this, R.layout.rowfriend, participantes);
                                friendsListView.setAdapter(participantesAdapter);
                                friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                {
                                    public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
                                    {
                                        Pessoa amigo = (Pessoa) friendsListView.getItemAtPosition(position);

                                        postarNoMuralDoEvento(amigo.getUid());
                                    }
                                });

                                spinner.dismiss();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

            Request.executeBatchAsync(request);

        }

    }

    private void postarNoMuralDoEvento(final String idAmigo) {

        final Session session = Session.getActiveSession();

        JSONObject json = new JSONObject();
        try {
            json.put("message", "Estou indo para o evento pelo Levar.me");
            json.put("link", "http://www.levar.me");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Postando no mural do evento
        Request request = Request.newPostRequest(session, getIdEvento()+"/feed", GraphObject.Factory.create(json), new Request.Callback() {
            @Override
            public void onCompleted(Response response) {

                adicionarParticipantesNoChat(session, idAmigo);
                enviarNotificacao(idAmigo);

            }
        });
        request.executeAsync();

    }

    private void adicionarParticipantesNoChat(final Session session, final String idAmigo) {

        // Make an API call to get user data and define a
        // new callback to handle the response.
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {

                                List<String> idsParticipantes = new ArrayList<String>();

                                idsParticipantes.add(user.getId());
                                idsParticipantes.add(idAmigo);

                                String params = idsParticipantes.toString().replace(" ", "%20");

                                new RequestPessoaTask().execute("http://www.levar.me/pessoa/add_pessoa_chat?idsParticipantes=" + params + "&idEvento=" + getIdEvento());

                            }
                        }
                    }
                });
        request.executeAsync();

    }

    private void enviarNotificacao(final String idAmigo) {

        Bundle params = new Bundle();
        params.putString("message", "Venha para o evento comigo !!!");

        WebDialog requestsDialog = (
                new WebDialog.RequestsDialogBuilder(this,
                        Session.getActiveSession(),
                        params))
                .setTo(idAmigo)
                .setTitle("Convide seus amigos")
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values, FacebookException error) {
                        if (error != null) {
                            if (error instanceof FacebookOperationCanceledException) {

                                Toast.makeText(FriendActivity.this.getApplicationContext(), "Request cancelled", Toast.LENGTH_SHORT).show();

                            } else {

                                Toast.makeText(FriendActivity.this.getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            final String requestId = values.getString("request");
                            if (requestId != null) {

                                buscarParticipantesDoChat(idAmigo);

                            } else {
                                Toast.makeText(FriendActivity.this.getApplicationContext(), "Request cancelled", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                })
                .build();
        requestsDialog.show();
    }

    private void buscarParticipantesDoChat(final String idAmigo) {

        // Make an API call to get user data and define a
        // new callback to handle the response.
        Request request = Request.newMeRequest(Session.getActiveSession(),
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // If the response is successful
                        if (user != null) {

                            List<String> idsParticipantes = new ArrayList<String>();

                            idsParticipantes.add(user.getId());
                            idsParticipantes.add(idAmigo);

                            String params = idsParticipantes.toString().replace(" ", "%20");

                            try {

                                ArrayList<String> participantesId = new ArrayList<String>();

                                String chats = new RequestPessoaTask().execute("http://www.levar.me/pessoa/list_chat?idsParticipantes=" + params + "&idEvento=" + getIdEvento()).get();
                                JSONArray jsonArray = new JSONArray(chats);
                                for (int i = 0; i < (jsonArray.length()); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    String chatId = obj.getString("chat_id");
                                    String pessoaId = obj.getString("pessoa_id");

                                    participantesId.add(pessoaId);

                                    irParaTelaDeChat(chatId, participantesId);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
        request.executeAsync();
    }

    private void irParaTelaDeChat(String idChat, ArrayList<String> participantesId) {

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("idChat", idChat);
        intent.putStringArrayListExtra("idsParticipantes", participantesId);
        startActivity(intent);

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

    private List<Pessoa> getParticipantesDoEventoNoFace(Response response) throws JSONException {

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