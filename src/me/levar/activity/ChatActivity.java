package me.levar.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.bugsense.trace.BugSenseHandler;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import me.levar.R;
import me.levar.chat.Chat;
import me.levar.chat.ChatListAdapter;
import me.levar.fragment.JsonHelper;
import me.levar.fragment.MixPanelHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static org.apache.commons.lang3.StringUtils.join;

/**
 * Created with IntelliJ IDEA.
 * User: Helena
 * Date: 15/08/13
 * Time: 01:25
 * To change this template use File | Settings | File Templates.
 */
public class ChatActivity extends LevarmeActivity {


    private static final String FIREBASE_URL = "https://levarme.firebaseio.com";

    private String username;
    private Firebase ref;
    private ValueEventListener connectedListener;
    private ChatListAdapter chatListAdapter;
    private ProgressDialog spinner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat);

        Intent intent = getIntent();
        String idChat = intent.getStringExtra("idChat");
        ArrayList<String> idsParticipantes = intent.getStringArrayListExtra("idsParticipantes");

        setTitleChat(idsParticipantes);

        spinner = new ProgressDialog(this);
        spinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        spinner.setMessage(getString(com.facebook.android.R.string.com_facebook_loading));

        spinner.show();

        // Setup our Firebase ref
        ref = new Firebase(FIREBASE_URL).child(idChat); //mudar de chat para o id do user que esta começando o chat

        // Setup our input methods. Enter key on the keyboard or pushing the send button
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        BugSenseHandler.sendEvent("Inicializou o chat");
        MixPanelHelper.sendEvent(this, "Inicializou o chat");

        spinner.dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Make sure we have a username
        setupCurrentUser();

        // Finally, a little indication of connection status
        connectedListener = ref.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled() {
                // No-op
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        ref.getRoot().child(".info/connected").removeEventListener(connectedListener);
        if (chatListAdapter != null) {
            chatListAdapter.cleanup();
        }
    }

    private void setupCurrentUser() {

        try {
            SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
            username = prefs.getString("username", null);

            SharedPreferences settings = getSharedPreferences(LevarmeActivity.CURRENT_USER_FILE, 0);
            String currentUserName = settings.getString("currentUserName", "Anonymous");

            if (username == null) {
                username = currentUserName;
                prefs.edit().putString("username", username).commit();
            }

            // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
            final ListView listView = (ListView) findViewById(R.id.list);

            listView.setEmptyView(findViewById(android.R.id.empty));

            // Tell our list adapter that we only want 50 messages at a time
            chatListAdapter = new ChatListAdapter(ref.limit(50), ChatActivity.this, R.layout.rowchat, username);
            listView.setAdapter(chatListAdapter);
            chatListAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    listView.setSelection(chatListAdapter.getCount() - 1);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendMessage() {
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            // Create our 'model', a Chat object
            Chat chat = new Chat(input, username);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            ref.push().setValue(chat);
            inputText.setText("");
        }
    }

    private void setTitleChat(ArrayList<String> idsParticipantes) {

        final Session session = Session.getActiveSession();

        if (session != null) {

            String fqlQuery = getFQLQuery(idsParticipantes);

            final Bundle params = new Bundle();
            params.putString("q", fqlQuery);
            params.putString("access_token", session.getAccessToken());
            Request request = new Request(session,
                    "/fql",
                    params,
                    HttpMethod.GET,
                    new Request.Callback(){
                        public void onCompleted(Response response) {

                            if (response != null) {
                                JSONArray usersByFacebook = JsonHelper.getJsonArrayNodeData(response);

                                for (int i = 0; i < (usersByFacebook.length()); i++) {

                                    JSONObject obj = JsonHelper.getJsonObject(usersByFacebook, i);

                                    String name = JsonHelper.getString(obj, "name");

                                    setTitle("   Chat With " + name);
                                }
                            }

                        }
                    });

            Request.executeBatchAsync(request);

        }


    }

    private String getFQLQuery(ArrayList<String> idsParticipantes) {

        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT name, pic_square, uid FROM user WHERE uid IN (  ");
        builder.append(join(idsParticipantes, ","));
        builder.append(" ) ");
        builder.append(" AND uid != me() ");

        return builder.toString();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}