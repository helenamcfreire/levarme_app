package me.levar.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import me.levar.R;
import me.levar.entity.Evento;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter<P> extends ArrayAdapter<Evento> {

    /*
     * Used to instantiate layout XML file into its corresponding View objects
     */
    private final LayoutInflater inflater;
    public List<Evento> eventos;

    /*
     * each list item layout ID
     */
    private final int resourceId;

    public EventAdapter(Context context, int resource, List<Evento> eventos) {
        super(context, resource, eventos);
        this.inflater = LayoutInflater.from(context);
        this.resourceId = resource;
        this.eventos = new ArrayList<Evento>();
        this.eventos.addAll(eventos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the person from position
        final Evento evento = getItem(position);

        convertView = inflater.inflate(resourceId, parent, false);

        TextView eventName = (TextView) convertView.findViewById(R.id.rowEvent);
        eventName.setText(evento.getNome());

        Typeface nameEventFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/GothamLight.otf");
        eventName.setTypeface(nameEventFont);

        TextView date = (TextView) convertView.findViewById(R.id.date);
        date.setText(evento.getDate());

        Typeface dateFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/GothamMedium.otf");
        date.setTypeface(dateFont);

        return convertView;
    }

}