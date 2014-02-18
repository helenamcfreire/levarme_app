package me.levar.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.bugsense.trace.BugSenseHandler;
import com.facebook.*;
import com.facebook.model.GraphObject;
import com.facebook.widget.WebDialog;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import me.levar.R;
import me.levar.adapter.FriendAdapter;
import me.levar.entity.Pessoa;
import me.levar.fragment.JsonHelper;
import me.levar.fragment.MixPanelHelper;
import me.levar.task.RequestLevarmeTask;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static me.levar.fragment.JsonHelper.*;

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

    @Override
    protected void onResume() {
        com.facebook.Settings.publishInstallAsync(this, getResources().getString(R.string.app_id));
        super.onResume();
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

            List<String> usersLevarMe = getPessoasCadastradasNoLevarMe();

            String fqlQuery = getFQLQuery(idEvento, usersLevarMe);

            final Bundle params = new Bundle();
            params.putString("q", fqlQuery);
            params.putString("access_token", session.getAccessToken());
            Request request = new Request(session,
                    "/fql",
                    params,
                    HttpMethod.GET,
                    new Request.Callback(){
                        public void onCompleted(Response response) {

                            List<Pessoa> participantes = buscarParticipantesDoEvento(response);
                            participantesAdapter = new FriendAdapter<Pessoa>(FriendActivity.this, R.layout.rowfriend, participantes);
                            friendsListView.setAdapter(participantesAdapter);
                            friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                            {
                                public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
                                {
                                    spinner.show();
                                    BugSenseHandler.sendEvent("Escolheu uma pessoa");
                                    MixPanelHelper.sendEvent(FriendActivity.this, "Escolheu uma pessoa");

                                    Pessoa amigo = (Pessoa) friendsListView.getItemAtPosition(position);

                                    postarNoMuralDoEvento(amigo);
                                    spinner.dismiss();
                                }
                            });
                            spinner.dismiss();
                        }
                    });

            Request.executeBatchAsync(request);

        }

    }

    private String getFQLQuery(String idEvento, List<String> usersLevarMe) {

        StringBuilder builder = new StringBuilder();

        builder.append(" {'participantes': 'select uid from event_member where ");
        builder.append(" ( ");
        builder.append(" uid in (select uid1 from friend where uid2 = me()) ");
        builder.append(" OR uid IN ( ");
        builder.append(idsUsersLevarMe(usersLevarMe));
        builder.append(" )) ");
        builder.append(" and eid =  ");
        builder.append(idEvento);
        builder.append(" ', ");
        builder.append(" 'dados_participante':  'SELECT name, pic_square, uid, is_app_user, mutual_friend_count FROM user WHERE (uid IN (SELECT uid FROM #participantes)) ");
        builder.append(" AND uid != me()' ");
        builder.append(" ,} ");

        return builder.toString();
    }

    private String idsUsersLevarMe(List<String> pessoasCadastradasNoLevarMe) {
        return StringUtils.join(pessoasCadastradasNoLevarMe, ",");
    }

    private void postarNoMuralDoEvento(final Pessoa amigo) {

        final Session session = Session.getActiveSession();

        JSONObject json = new JSONObject();
        try {
            json.put("message", "Alguém quer pegar uma carona/rachar um taxi comigo?");
            json.put("link", "http://www.levar.me/?utm_source=android&utm_medium=mobile&utm_campaign=postwall");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Postando no mural do evento
        Request request = Request.newPostRequest(session, getIdEvento() + "/feed", GraphObject.Factory.create(json), new Request.Callback() {
            @Override
            public void onCompleted(Response response) {

                adicionarParticipantesNoChat(session, amigo);

            }
        });
        request.executeAsync();

    }

    private void adicionarParticipantesNoChat(final Session session, final Pessoa amigo) {

        SharedPreferences settings = getSharedPreferences(LevarmeActivity.CURRENT_USER_FILE, 0);
        String currentUserId = settings.getString("currentUserId", "0");

        List<String> idsParticipantes = new ArrayList<String>();

        idsParticipantes.add(currentUserId);
        idsParticipantes.add(amigo.getUid());

        String params = idsParticipantes.toString().replace(" ", "%20");

        String json = null;
        try {
            json = new RequestLevarmeTask().execute("http://www.levar.me/pessoa/add_pessoa_chat?idsParticipantes=" + params + "&idEvento=" + getIdEvento()).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = null;
        boolean naoFoiCadastrado = false;
        try {
            jsonObject = new JSONObject(json);
            naoFoiCadastrado = jsonObject.getBoolean("naoFoiCadastrado");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!naoFoiCadastrado) {
            enviarNotificacao(amigo);
        } else {
            buscarParticipantesDoChat(amigo.getUid());
        }


    }

    private void enviarNotificacao(final Pessoa amigo) {

        Bundle params = new Bundle();
        String message = amigo.getNome() + ", também to indo para " + getNomeEvento() + ", entre no Levar.me para coordenarmos um taxi ou uma carona.";
        params.putString("message", message);

        WebDialog requestsDialog = (
                new WebDialog.RequestsDialogBuilder(this,
                        Session.getActiveSession(),
                        params))
                .setTo(amigo.getUid())
                .setTitle("Convide seus amigos")
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values, FacebookException error) {
                        if (error != null) {
                            if (error instanceof FacebookOperationCanceledException) {

                                BugSenseHandler.sendEvent("Convites Cancelados");
                                MixPanelHelper.sendEvent(FriendActivity.this, "Convites Cancelados");
                                Toast.makeText(FriendActivity.this.getApplicationContext(), "Request cancelled", Toast.LENGTH_SHORT).show();

                            } else {

                                BugSenseHandler.sendEvent("Convites Cancelados");
                                MixPanelHelper.sendEvent(FriendActivity.this, "Convites Cancelados");
                                Toast.makeText(FriendActivity.this.getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            final String requestId = values.getString("request");
                            if (requestId != null) {

                                BugSenseHandler.sendEvent("Convites Enviados");
                                MixPanelHelper.sendEvent(FriendActivity.this, "Convites Enviados");
                                buscarParticipantesDoChat(amigo.getUid());

                            } else {
                                BugSenseHandler.sendEvent("Convites Cancelados");
                                MixPanelHelper.sendEvent(FriendActivity.this, "Convites Cancelados");
                                Toast.makeText(FriendActivity.this.getApplicationContext(), "Request cancelled", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                })
                .build();
        requestsDialog.show();
    }

    private void buscarParticipantesDoChat(final String idAmigo) {

        SharedPreferences settings = getSharedPreferences(LevarmeActivity.CURRENT_USER_FILE, 0);
        String currentUserId = settings.getString("currentUserId", "0");

        String chats = null;
        try {
            chats = new RequestLevarmeTask().execute("http://www.levar.me/pessoa/list_chat?currentUserId=" + currentUserId + "&idAmigo=" + idAmigo + "&idEvento=" + getIdEvento()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = JsonHelper.newJsonArray(chats);
        for (int i = 0; i < (jsonArray.length()); i++) {

            JSONObject obj = JsonHelper.getJsonObject(jsonArray, i);
            String chatId = JsonHelper.getString(obj, "chat_id");
            String participante_1 = JsonHelper.getString(obj, "participante_1_id");
            String participante_2 = JsonHelper.getString(obj, "participante_2_id");
            String registration_id = JsonHelper.getString(obj, "registration_id");

            irParaTelaDeChat(chatId, idAmigo, registration_id);
        }

    }

    private void irParaTelaDeChat(String idChat, String idAmigo, String registration_id) {

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("idChat", idChat);
        intent.putExtra("idAmigo", idAmigo);
        intent.putExtra("registration_id", registration_id);
        startActivity(intent);

    }

    private List<String> getPessoasCadastradasNoLevarMe() {

        List<String> ids = new ArrayList<String>();

        String pessoasCadastradas = null;
        try {
            pessoasCadastradas = new RequestLevarmeTask().execute("http://www.levar.me/pessoa/list").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = newJsonArray(pessoasCadastradas);

        for (int i = 0; i < (jsonArray.length()); i++) {
            JSONObject obj = getJsonObject(jsonArray, i);
            String id = JsonHelper.getString(obj, "uid");
            ids.add(id);
        }

        return ids;
    }

    private List<Pessoa> buscarParticipantesDoEvento(Response response) {

        List<Pessoa> amigos = null;

        if (response != null) {
            JSONArray data = getJsonArrayNodeData(response);
            JSONObject obj = getJsonObject(data, 1);
            JSONArray amigosJson = getFqlResultSet(obj);

            amigos = new ArrayList<Pessoa>();

            for (int i = 0; i < (amigosJson.length()); i++) {

                JSONObject amigoJson = JsonHelper.getJsonObject(amigosJson, i);

                String id = JsonHelper.getString(amigoJson, "uid");
                String nome = JsonHelper.getString(amigoJson, "name");
                String foto = JsonHelper.getString(amigoJson, "pic_square");
                boolean is_app_user = JsonHelper.getBoolean(amigoJson, "is_app_user");
                String mutual_friend_count = JsonHelper.getString(amigoJson, "mutual_friend_count");

                Pessoa amigo = new Pessoa(id, nome, foto, is_app_user, mutual_friend_count);

                amigos.add(amigo);
            }
        }

        Collections.sort(amigos);

        return amigos;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent intent = new Intent(this, EventActivity.class);
            startActivity(intent);

            MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, MixPanelHelper.MIXPANEL_TOKEN);
            mixpanel.flush();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}