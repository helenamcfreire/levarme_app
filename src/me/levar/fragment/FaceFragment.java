package me.levar.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.*;
import com.facebook.model.GraphLocation;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import me.levar.R;
import me.levar.activity.EventActivity;
import me.levar.entity.Pessoa;
import me.levar.task.RequestPessoaTask;

import java.util.List;

import static java.util.Arrays.asList;
import static me.levar.activity.LevarmeActivity.CURRENT_USER_FILE;

public class FaceFragment extends Fragment {

    private static final String MSG_ERROR_NO_INTERNET = "No internet detected :(";
    private UiLifecycleHelper uiHelper;
    private LoginButton loginButton;
    private TextView logo;
    private TextView slogan;
    private static final List<String> READ_PERMISSIONS = asList("user_events", "friends_events", "user_birthday", "user_location", "user_relationships");
    private static final List<String> PUBLISH_PERMISSIONS = asList("publish_stream");

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main, container, false);

        logo = (TextView) view.findViewById(R.id.logo);
        slogan = (TextView) view.findViewById(R.id.slogan);

        Typeface logoFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GothamMedium.otf");
        logo.setTypeface(logoFont);

        Typeface sloganFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GothamLight.otf");
        slogan.setTypeface(sloganFont);

        loginButton = (LoginButton) view.findViewById(R.id.loginButton);
        loginButton.setFragment(this);
        loginButton.setReadPermissions(READ_PERMISSIONS);

        Session session = Session.getActiveSession();

        boolean jaPodeListarOsEventos = session.isOpened() && confirmedPublishPermission(session) && confirmedAllReadPermissions(session);

        if (jaPodeListarOsEventos) {
            salvarUsuario(session);
            exibirEventos();
        }

        return view;
    }

    private void confirmPublishPermission(Session session) {
        if (!confirmedPublishPermission(session)) {
            session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PUBLISH_PERMISSIONS).setCallback(callback));
        }
    }

    private boolean confirmedPublishPermission(Session session) {
        return session.getPermissions().containsAll(PUBLISH_PERMISSIONS);
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {

        if (state.isOpened()) {
            confirmPublishPermission(session);
            confirmReadPermissions(session);
            if (confirmedPublishPermission(session) && confirmedAllReadPermissions(session)) {
                salvarUsuario(session);
                exibirEventos();
            }
        }
    }

    private void confirmReadPermissions(Session session) {
        if (!confirmedAllReadPermissions(session)) {
            session.requestNewReadPermissions(new Session.NewPermissionsRequest(this, READ_PERMISSIONS).setCallback(callback));
        }
    }

    private boolean confirmedAllReadPermissions(Session session) {
        return session.getPermissions().containsAll(READ_PERMISSIONS);
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

        if (!isOnline()) {
            Toast.makeText(getActivity(), MSG_ERROR_NO_INTERNET, Toast.LENGTH_SHORT).show();
        }
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

    private void exibirEventos() {

        //Mudar para a view de eventos
        Intent intent = new Intent(getActivity(), EventActivity.class);
        startActivity(intent);

    }

    private void salvarUsuario(final Session session) {

        // Make an API call to get user data and define a
        // new callback to handle the response.
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {
                                Pessoa usuarioLogado = new Pessoa(user.getId(), user.getName());
                                String sexo = (String) user.getProperty("gender");
                                String relationship_status = (String) user.getProperty("relationship_status");

                                GraphLocation location = user.getLocation();

                                new RequestPessoaTask(usuarioLogado).execute("http://www.levar.me/pessoa/create");

                                SharedPreferences settings = getActivity().getSharedPreferences(CURRENT_USER_FILE, 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("currentUserId", usuarioLogado.getUid());
                                editor.putString("currentUserName", usuarioLogado.getNome());
                                editor.commit();

                                Pessoa usuarioMixpanel = new Pessoa(user.getId(), user.getName(), location.getCity(), location.getCountry(), location.getState(), sexo, user.getBirthday(), relationship_status);
                                MixPanelHelper.createUser(getActivity(), usuarioMixpanel);
                            }
                        }
                    }
                });
        request.executeAsync();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}