package me.levar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.bugsense.trace.BugSenseHandler;
import com.facebook.Settings;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import me.levar.R;
import me.levar.adapter.EventAdapter;
import me.levar.entity.Evento;
import me.levar.fragment.MixPanelHelper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Helena
 * Date: 15/08/13
 * Time: 00:22
 * To change this template use File | Settings | File Templates.
 */
public class EventListActivity extends LevarmeActivity {

    private EventAdapter<Evento> eventosAdapter;
    private ListView eventsListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.events);

        setTitle("   Which party?");

        eventsListView = (ListView) findViewById(R.id.eventList);

        carregarEventos();

    }

    @Override
    protected void onResume() {
        Settings.publishInstallAsync(this, getResources().getString(R.string.app_id));
        super.onResume();
    }

    private void carregarEventos() {


        final List<Evento> eventos = getIntent().getParcelableArrayListExtra("eventos");

        eventosAdapter = new EventAdapter<Evento>(EventListActivity.this, R.layout.rowevent, eventos);
        eventsListView.setAdapter(eventosAdapter);
        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

                BugSenseHandler.sendEvent("Clicou no evento");
                MixPanelHelper.sendEvent(EventListActivity.this, "Clicou no evento");

                Evento evento = (Evento) eventsListView.getItemAtPosition(position);

                if (evento != null) {
                    irParaTelaDeParticipantesDoEvento(evento.getEid(), evento.getNome());
                }
            }
        });


    }

    private void irParaTelaDeParticipantesDoEvento(String idEvento, String nomeEvento) {

        //Mudar para a view de amigos
        Intent intent = new Intent(this, FriendActivity.class);
        intent.putExtra("idEvento", idEvento);
        intent.putExtra("nomeEvento", nomeEvento);
        startActivity(intent);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, MixPanelHelper.MIXPANEL_TOKEN);
            mixpanel.flush();

            moveTaskToBack(true);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}