package me.levar;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class FriendAdapter<P> extends ArrayAdapter<Pessoa> {


    private NotificationFragment notificationFragment;
    private final FragmentManager fragmentManager;
    private String nomeEvento;

    /*
     * Used to instantiate layout XML file into its corresponding View objects
     */
    private final LayoutInflater inflater;

    /*
     * each list item layout ID
     */
    private final int resourceId;

    public FriendAdapter(Context context, int resource, List<Pessoa> objects, FragmentManager fragmentManager, String nomeEvento) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
        this.resourceId = resource;
        this.fragmentManager = fragmentManager;
        this.nomeEvento = nomeEvento;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the person from position
        Pessoa amigo = getItem(position);

        convertView = inflater.inflate(resourceId, parent, false);

        TextView pessoa = (TextView) convertView.findViewById(R.id.nameFriend);
        pessoa.setText(amigo.getNome());

        new DrawableFromUrlTask(convertView).execute(amigo.getPic_square());

        possoTeBuscarListener(convertView, amigo.getUid());

        return convertView;
    }

    public void possoTeBuscarListener(View view, final String uid) {

        Button possoBuscar = (Button) view.findViewById(R.id.possoBuscar);
        possoBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Mudar para a view de amigos
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                notificationFragment = new NotificationFragment(uid, nomeEvento);
                transaction.replace(android.R.id.content, notificationFragment);
                transaction.commit();

            }
        });
    }

}