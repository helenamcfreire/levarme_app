package me.levar.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import me.levar.R;
import me.levar.entity.Pessoa;
import me.levar.task.DrawableFromUrlTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

        try {
            Drawable drawableAmigo = (Drawable) new DrawableFromUrlTask().execute(amigo.getPic_square()).get();
            ImageView foto = (ImageView) convertView.findViewById(R.id.imageFriend);
            foto.setImageDrawable(drawableAmigo);

            if (!amigo.getAmigosEmComum().isEmpty()) {

                Drawable drawableAmigoEmComum1 = (Drawable) new DrawableFromUrlTask().execute(amigo.getAmigosEmComum().get(0).getPic_square()).get();
                ImageView fotoAmigoEmComum1 = (ImageView) convertView.findViewById(R.id.imageMutualFriends1);
                fotoAmigoEmComum1.setImageDrawable(drawableAmigoEmComum1);

                Drawable drawableAmigoEmComum2 = (Drawable) new DrawableFromUrlTask().execute(amigo.getAmigosEmComum().get(1).getPic_square()).get();
                ImageView fotoAmigoEmComum2 = (ImageView) convertView.findViewById(R.id.imageMutualFriends2);
                fotoAmigoEmComum2.setImageDrawable(drawableAmigoEmComum2);

                Drawable drawableAmigoEmComum3 = (Drawable) new DrawableFromUrlTask().execute(amigo.getAmigosEmComum().get(2).getPic_square()).get();
                ImageView fotoAmigoEmComum3 = (ImageView) convertView.findViewById(R.id.imageMutualFriends3);
                fotoAmigoEmComum3.setImageDrawable(drawableAmigoEmComum3);

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return convertView;
    }

}