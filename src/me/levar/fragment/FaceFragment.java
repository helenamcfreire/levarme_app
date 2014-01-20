package me.levar.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.bugsense.trace.BugSenseHandler;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import me.levar.R;
import me.levar.activity.EventActivity;
import me.levar.entity.Pessoa;
import me.levar.task.RequestPessoaTask;

import static java.util.Arrays.asList;

public class FaceFragment extends Fragment {

    private static final String MSG_ERROR_NO_INTERNET = "No internet detected :(";
    private UiLifecycleHelper uiHelper;
    private LoginButton loginButton;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main, container, false);

        loginButton = (LoginButton) view.findViewById(R.id.loginButton);
        loginButton.setFragment(this);
        loginButton.setReadPermissions(asList("user_events", "friends_events"));

        BugSenseHandler.sendEvent("Criou o botão de login com suas respectivas permissões...");

        Session session = Session.getActiveSession();

        if (session.isOpened()) {
            BugSenseHandler.sendEvent("Sessão aberta..entrou para exibir os eventos...");
            exibirEventos();
            BugSenseHandler.sendEvent("Exibiu os eventos...");
        }

        return view;
    }

    private void confirmPublishPermission(Session session) {
        if (!alreadyConfirmedPublishPermission(session)) {
            BugSenseHandler.sendEvent("Entrou para confirmar as permissões de publicação...");
            session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, asList("publish_stream"))
                    .setCallback(callback));
        }
    }

    private boolean alreadyConfirmedPublishPermission(Session session) {
        return session.getPermissions().contains("publish_stream");
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {

        if (state.isOpened()) {
            BugSenseHandler.sendEvent("Esconde o botão de login, confirma as permissões de publicação, salva o usuário e exibe os eventos do mesmo...");
            confirmPublishPermission(session);
            if (alreadyConfirmedPublishPermission(session)) {
                salvarUsuario(session);
                exibirEventos();
            }
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

        if (!isOnline()) {
            BugSenseHandler.sendEvent("Parece que a internet do usuário não está ligada...");
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
                                BugSenseHandler.sendEvent("Salvando usuário...");
                                Pessoa usuarioLogado = new Pessoa(user.getId(), user.getName());
                                new RequestPessoaTask(usuarioLogado).execute("http://www.levar.me/pessoa/create");
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