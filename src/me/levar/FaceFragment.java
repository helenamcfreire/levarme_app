package me.levar;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import static java.util.Arrays.asList;

public class FaceFragment extends Fragment {

    private UiLifecycleHelper uiHelper;
    private LoginButton loginButton;
    private EventFragment eventFragment;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main, container, false);

        loginButton = (LoginButton) view.findViewById(R.id.loginButton);
        loginButton.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
        loginButton.setFragment(this);
        loginButton.setReadPermissions(asList("user_events", "friends_events"));

        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
            loginButton.setVisibility(View.GONE);

            if (!session.getPermissions().contains("publish_stream")) {
                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, asList("publish_stream"))
                        .setCallback(callback)
                        .setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO));
            }

            salvarUsuario(session);
            exibirEventos();
        } else {
            loginButton.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {

        if (state.isOpened()) {
            loginButton.setVisibility(View.GONE);
            salvarUsuario(session);
            exibirEventos();

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

    private void exibirEventos() {

        //Mudar para a view de eventos
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        eventFragment = new EventFragment();
        transaction.addToBackStack(FaceFragment.class.getName());
        transaction.replace(android.R.id.content, eventFragment);
        transaction.commit();

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
                                new RequestPessoaTask(usuarioLogado).execute("http://www.levar.me/pessoa/create");
                            }
                        }
                    }
                });
        request.executeAsync();
    }

}