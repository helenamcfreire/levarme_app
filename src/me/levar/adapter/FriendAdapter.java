package me.levar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import me.levar.R;
import me.levar.entity.Pessoa;
import me.levar.task.DrawableFromUrlTask;

import java.util.ArrayList;
import java.util.List;

public class FriendAdapter<P> extends ArrayAdapter<Pessoa> {

    /*
     * Used to instantiate layout XML file into its corresponding View objects
     */
    private final LayoutInflater inflater;
    public List<Pessoa> participantes;

    /*
     * each list item layout ID
     */
    private final int resourceId;

    public FriendAdapter(Context context, int resource, List<Pessoa> participantes) {
        super(context, resource, participantes);
        this.inflater = LayoutInflater.from(context);
        this.resourceId = resource;
        this.participantes = new ArrayList<Pessoa>();
        this.participantes.addAll(participantes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the person from position
        final Pessoa amigo = getItem(position);

        convertView = inflater.inflate(resourceId, parent, false);

        TextView pessoa = (TextView) convertView.findViewById(R.id.nameFriend);
        pessoa.setText(amigo.getNome());

        TextView amigosEmComum = (TextView) convertView.findViewById(R.id.amigosEmComum);
        amigosEmComum.setText(amigo.getQtdAmigosEmComum() + " amigos em comum");

        new DrawableFromUrlTask(convertView).execute(amigo.getPic_square());

        return convertView;
    }

}