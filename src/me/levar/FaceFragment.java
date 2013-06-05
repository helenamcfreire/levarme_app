package me.levar;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.facebook.*;
import com.facebook.model.GraphObject;
import com.facebook.widget.LoginButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class FaceFragment extends Fragment {

    private UiLifecycleHelper uiHelper;
    private LoginButton loginButton;
    private ListView eventsListView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main, container, false);

        loginButton = (LoginButton) view.findViewById(R.id.loginButton);
        loginButton.setFragment(this);
        loginButton.setReadPermissions(asList("user_events", "friends_events"));

        eventsListView = (ListView) view.findViewById(R.id.events);

        return view;
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            loginButton.setVisibility(View.GONE);
            carregarEventos(session);
        } else if (state.isClosed()) {
            loginButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void carregarEventos(Session session) {

        if (session != null) {

            StringBuilder builder = new StringBuilder();
            builder.append(" SELECT name, start_time FROM event WHERE eid IN ");
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
                                List<String> eventsLevarMe = getEventsLevarMe(response);

                                ArrayAdapter<String> eventsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.event, eventsLevarMe);
                                eventsListView.setAdapter(eventsAdapter);

                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    });

            Request.executeBatchAsync(request);

        }

    }

    private List<String> getEventsLevarMe(Response response) throws JSONException {

        GraphObject graphObject  = response.getGraphObject();
        JSONObject jsonObject = graphObject.getInnerJSONObject();
        JSONArray eventsByFacebook = jsonObject.getJSONArray("data");

        List<String> eventsLevarMe = new ArrayList<String>();

        for (int i = 0; i < (eventsByFacebook.length()); i++) {
            JSONObject data = eventsByFacebook.getJSONObject(i);

            String name = data.getString("name");
            eventsLevarMe.add(name);

        }

        return eventsLevarMe;
    }

}