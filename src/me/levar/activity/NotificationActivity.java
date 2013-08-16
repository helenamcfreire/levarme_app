package me.levar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import me.levar.R;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Helena
 * Date: 15/08/13
 * Time: 21:03
 * To change this template use File | Settings | File Templates.
 */
public class NotificationActivity extends LevarmeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notifications);

//        new RequestPessoaTask().execute("http://www.levar.me/pessoa/add_pessoas_chat?"+getIdsParticipantes().toString());

        enviarNotificacao();
    }

    private void enviarNotificacao() {

        Bundle params = new Bundle();
        params.putString("message", "Learn how to make your Android apps social");

        WebDialog requestsDialog = (
                new WebDialog.RequestsDialogBuilder(this,
                        Session.getActiveSession(),
                        params))
                .setTo(getIdsParticipantes().toString().replace("[", "").replace("]", ""))
                .setTitle("Convide seus amigos")
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {
                        if (error != null) {
                            if (error instanceof FacebookOperationCanceledException) {
                                Toast.makeText(NotificationActivity.this.getApplicationContext(),
                                        "Request cancelled",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(NotificationActivity.this.getApplicationContext(),
                                        "Network Error",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            final String requestId = values.getString("request");
                            if (requestId != null) {
                                Toast.makeText(NotificationActivity.this.getApplicationContext(),
                                        "Request sent",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(NotificationActivity.this.getApplicationContext(),
                                        "Request cancelled",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                })
                .build();
        requestsDialog.show();
    }

    private List<String> getIdsParticipantes() {
        Intent intent = getIntent();
        return intent.getStringArrayListExtra("idsParticipantes");
    }

}