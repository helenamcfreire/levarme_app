package me.levar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import me.levar.R;
import me.levar.entity.Pessoa;
import me.levar.lazylist.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class FriendAdapter<P> extends ArrayAdapter<Pessoa> {

    /*
     * Used to instantiate layout XML file into its corresponding View objects
     */
    private final LayoutInflater inflater;
    public List<Pessoa> participantes;
    public ImageLoader imageLoader;

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
        this.imageLoader = new ImageLoader(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the person from position
        final Pessoa amigo = getItem(position);

        convertView = inflater.inflate(resourceId, parent, false);

        TextView pessoa = (TextView) convertView.findViewById(R.id.nameFriend);
        pessoa.setText(amigo.getNome());

        ImageView foto = (ImageView) convertView.findViewById(R.id.imageFriend);
        imageLoader.DisplayImage(amigo.getPic_square(), foto);

        if (!amigo.getAmigosEmComum().isEmpty()) {

            if (amigo.getAmigosEmComum().size() >= 1) {
                Pessoa amigoEmComum = amigo.getAmigosEmComum().get(0);

                ImageView fotoAmigoEmComum1 = (ImageView) convertView.findViewById(R.id.imageMutualFriends1);
                imageLoader.DisplayImage(amigoEmComum.getPic_square(), fotoAmigoEmComum1);

                TextView nomeAmigoEmComum1 = (TextView) convertView.findViewById(R.id.nameMutualFriends1);
                nomeAmigoEmComum1.setText(amigoEmComum.getPrimeiroNome());
            }

            if (amigo.getAmigosEmComum().size() >= 2) {
                Pessoa amigoEmComum = amigo.getAmigosEmComum().get(1);

                ImageView fotoAmigoEmComum2 = (ImageView) convertView.findViewById(R.id.imageMutualFriends2);
                imageLoader.DisplayImage(amigoEmComum.getPic_square(), fotoAmigoEmComum2);

                TextView nomeAmigoEmComum2 = (TextView) convertView.findViewById(R.id.nameMutualFriends2);
                nomeAmigoEmComum2.setText(amigoEmComum.getPrimeiroNome());
            }

            if (amigo.getAmigosEmComum().size() >= 3) {
                Pessoa amigoEmComum = amigo.getAmigosEmComum().get(2);

                ImageView fotoAmigoEmComum3 = (ImageView) convertView.findViewById(R.id.imageMutualFriends3);
                imageLoader.DisplayImage(amigoEmComum.getPic_square(), fotoAmigoEmComum3);

                TextView nomeAmigoEmComum3 = (TextView) convertView.findViewById(R.id.nameMutualFriends3);
                nomeAmigoEmComum3.setText(amigoEmComum.getPrimeiroNome());
            }

        }

        return convertView;
    }

}