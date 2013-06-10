package me.levar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FriendAdapter<P> extends ArrayAdapter<Pessoa> {

    /*
     * Used to instantiate layout XML file into its corresponding View objects
     */
    private final LayoutInflater inflater;

    /*
     * each list item layout ID
     */
    private final int resourceId;

    public FriendAdapter(Context context, int resource, List<Pessoa> objects) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
        this.resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the person from position
        Pessoa amigo = getItem(position);

        convertView = inflater.inflate(resourceId, parent, false);

        TextView pessoa = (TextView) convertView.findViewById(R.id.nameFriend);
        pessoa.setText(amigo.getNome());

        TextView amigosEmComum = (TextView) convertView.findViewById(R.id.amigosEmComum);
        amigosEmComum.setText(amigo.getQtdAmigosEmComum() + " amigos em comum");

        new DrawableFromUrlTask(convertView).execute(amigo.getPic_square());

        return convertView;
    }

}