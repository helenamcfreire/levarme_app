package me.levar.adapter;

import android.content.Context;
import android.graphics.Typeface;
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

        Typeface nameFriendFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/GothamLight.otf");
        pessoa.setTypeface(nameFriendFont);

        TextView mutual_friends = (TextView) convertView.findViewById(R.id.commonFriends);
        mutual_friends.setText(amigo.getMutual_friend_count()+" mutual friends");

        Typeface mutualFriendsFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/GothamMedium.otf");
        mutual_friends.setTypeface(mutualFriendsFont);

        ImageView foto = (ImageView) convertView.findViewById(R.id.imageFriend);
        imageLoader.DisplayImage(amigo.getPic_square(), foto);

        return convertView;
    }

}