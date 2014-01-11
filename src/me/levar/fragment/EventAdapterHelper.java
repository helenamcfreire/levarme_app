package me.levar.fragment;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Helena
 * Date: 11/01/14
 * Time: 18:20
 * To change this template use File | Settings | File Templates.
 */
public class EventAdapterHelper {

    private Map<String, List<String>> mapaDataENomesEventos;
    private Map<String, String> mapaNomeEIdEvento;

    public Map<String, List<String>> getMapaDataENomesEventos() {
        return mapaDataENomesEventos;
    }

    public void setMapaDataENomesEventos(Map<String, List<String>> mapaDataENomesEventos) {
        this.mapaDataENomesEventos = mapaDataENomesEventos;
    }

    public Map<String, String> getMapaNomeEIdEvento() {
        return mapaNomeEIdEvento;
    }

    public void setMapaNomeEIdEvento(Map<String, String> mapaNomeEIdEvento) {
        this.mapaNomeEIdEvento = mapaNomeEIdEvento;
    }
}
