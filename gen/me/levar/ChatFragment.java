package me.levar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.facebook.Session;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class ChatFragment extends Fragment {

    private EditText chatText;
    private Button sendButton;
    private XMPPConnection connection;
    private ListView chatLines;
    private ArrayList<String> messages = new ArrayList<String>();
    private ArrayAdapter<String> adapterMessages;

    public ChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.chat, container, false);

        chatText = (EditText) view.findViewById(R.id.sendText);
        chatLines = (ListView) view.findViewById(R.id.chatLines);
        sendButton = (Button) view.findViewById(R.id.sendButton);

        setMessagesAdapter();

        try {
            connectToFacebook();
        } catch (XMPPException e) {
            e.printStackTrace();
        }

        sendMessages();

        return view;
    }

    public void connectToFacebook() throws XMPPException {

        String appID = getResources().getString(R.string.app_id);
        String accessToken = Session.getActiveSession().getAccessToken();

        connection = createXMPPConnection();
        try {
            connection.connect();
            connection.login(appID, accessToken);
        } catch(XMPPException e) {
            throw new RuntimeException(e);
        }


    }

    private static synchronized XMPPConnection createXMPPConnection() {
        SASLAuthentication.registerSASLMechanism(SASLXFacebookPlatformMechanism.NAME, SASLXFacebookPlatformMechanism.class);
        SASLAuthentication.supportSASLMechanism(SASLXFacebookPlatformMechanism.NAME, 0);

        ConnectionConfiguration configuration = new ConnectionConfiguration("chat.facebook.com", 5222);
        configuration.setSASLAuthenticationEnabled(true);

        return new XMPPConnection(configuration);
    }

    private void sendMessages() {

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                *  {'msg_threads':
                        'SELECT thread_id,message_count FROM thread WHERE viewer_id = me() AND folder_id = 0',
                      'msg_messages':
                        'SELECT message_id,body,viewer_id, thread_id FROM message
                           WHERE thread_id IN (SELECT thread_id FROM #msg_threads)
                           AND viewer_id = me() LIMIT 200'
                      }


                      SELECT thread_id, message_count, recipients FROM thread WHERE folder_id = 0 and (100000926094106 in recipients or 100000223935894 in recipients)

                *
                * */

                //String to = String.format("-%d@chat.facebook.com", targetFacebookId);

                String to_thais = "-100000223935894@chat.facebook.com";
                String to_glaucia = "-100000917675848@chat.facebook.com";

                List<String> destinatarios = new ArrayList<String>();
                destinatarios.add(to_thais);
                destinatarios.add(to_glaucia);

                String message = chatText.getText().toString();

                Chat chat =  null;
                for (String dest : destinatarios) {
                    chat = connection.getChatManager().createChat(dest, null);

                    try {
                        chat.sendMessage(message);
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }


    private void setMessagesAdapter() {
        adapterMessages = new ArrayAdapter<String>(getActivity(), R.layout.rowchat, messages);
        chatLines.setAdapter(adapterMessages);
    }


}