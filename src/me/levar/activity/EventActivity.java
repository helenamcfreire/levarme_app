package me.levar.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import me.levar.R;
import me.levar.actionbar.ActionBarActivity;
import me.levar.slidingmenu.SlideMenu;
import me.levar.slidingmenu.SlideMenuInterface;
import me.levar.task.RequestPessoaTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.Integer.valueOf;

/**
 * Created with IntelliJ IDEA.
 * User: Helena
 * Date: 15/08/13
 * Time: 00:22
 * To change this template use File | Settings | File Templates.
 */
public class EventActivity extends ActionBarActivity implements SlideMenuInterface.OnSlideMenuItemClickListener {

    private ListView eventsListView;
    private ProgressDialog spinner;
    private SlideMenu slideMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        slideMenu = new SlideMenu(this, R.menu.slide, this, 333);
        slideMenu.init(this, R.menu.slide, this, 333);

        setContentView(R.layout.events);

        eventsListView = (ListView) findViewById(R.id.eventsList);

        spinner = new ProgressDialog(this);
        spinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        spinner.setMessage(getString(com.facebook.android.R.string.com_facebook_loading));

        carregarEventos();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        listarChats();

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                break;

            case R.id.menu_chat:
                slideMenu.show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSlideMenuItemClick(int itemId) {

        String idChat = String.valueOf(itemId);

        //Mudar para a view de chat
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("idChat", idChat);
        startActivity(intent);

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

                                        irParaTelaDeParticipantesDoEvento(idEvento);
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

    private void irParaTelaDeParticipantesDoEvento(String idEvento) {

        //Mudar para a view de amigos
        Intent intent = new Intent(this, FriendActivity.class);
        intent.putExtra("idEvento", idEvento);
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

    private void listarChats() {

        // Make an API call to get user data and define a
        // new callback to handle the response.
        Request request = Request.newMeRequest(Session.getActiveSession(),
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            try {
                                String chats = new RequestPessoaTask().execute("http://www.levar.me/pessoa/list_chats?uid=" + user.getId()).get();
                                try {
                                    JSONArray jsonArray = new JSONArray(chats);
                                    for (int i = 0; i < (jsonArray.length()); i++) {
                                        JSONObject obj = jsonArray.getJSONObject(i);
                                        String id = obj.getString("chat_id");
                                        addChatInMenuSlider(id);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        request.executeAsync();

    }


    private void addChatInMenuSlider(String chatId) {

        // this demonstrates how to dynamically add menu items
        SlideMenu.SlideMenuItem item = new SlideMenu.SlideMenuItem();
        item.id = valueOf(chatId);
        item.label = chatId;
        slideMenu.addMenuItem(item);

    }

}
