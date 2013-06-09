package me.levar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;

public class NotificationFragment extends Fragment {

    private String uid;
    private String nomeEvento;

    public NotificationFragment(String uid, String nomeEvento) {
        this.uid = uid;
        this.nomeEvento = nomeEvento;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.notifications, container, false);

        sendRequestDialog();

        return view;
    }

    private void sendRequestDialog() {
        Bundle params = new Bundle();
        params.putString("message", "Quer ir para o evento " + nomeEvento + " comigo ?");

        WebDialog requestsDialog = (
                new WebDialog.RequestsDialogBuilder(getActivity(),
                        Session.getActiveSession(),
                        params))
                .setTo(uid)
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {
                        if (error != null) {
                            if (error instanceof FacebookOperationCanceledException) {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Request cancelled",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Network Error",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            final String requestId = values.getString("request");
                            if (requestId != null) {

                                RelativeLayout relativeLayout = (RelativeLayout) getView().findViewById(R.id.notificationView);

                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

                                TextView boaDiversao = new TextView(getActivity());
                                boaDiversao.setText("Sua notificação foi enviada. Boa diversão !");
                                boaDiversao.setLayoutParams(layoutParams);

                                relativeLayout.addView(boaDiversao);

                            } else {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Request cancelled",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                })
                .build();
        requestsDialog.show();
    }

}