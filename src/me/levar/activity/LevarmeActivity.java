package me.levar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import me.levar.R;
import me.levar.actionbar.ActionBarActivity;
import me.levar.slidingmenu.SlideMenu;
import me.levar.slidingmenu.SlideMenuInterface;
import me.levar.task.RequestPessoaTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Integer.valueOf;

/**
 * Created with IntelliJ IDEA.
 * User: Helena
 * Date: 15/08/13
 * Time: 22:58
 * To change this template use File | Settings | File Templates.
 */
public class LevarmeActivity extends ActionBarActivity implements SlideMenuInterface.OnSlideMenuItemClickListener {

    private SlideMenu slideMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        slideMenu = new SlideMenu(this, R.menu.slide, this, 333);
        slideMenu.init(this, R.menu.slide, this, 333);
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
