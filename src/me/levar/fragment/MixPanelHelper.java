package me.levar.fragment;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import me.levar.entity.Pessoa;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: Helena
 * Date: 03/02/14
 * Time: 21:36
 * To change this template use File | Settings | File Templates.
 */
public class MixPanelHelper {

    public static final String MIXPANEL_TOKEN = "571ec076911ca93d1c6c1a2e429049d1";

    public static void sendEvent(android.content.Context context, String eventTitle) {

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN);

        mixpanel.track(eventTitle, null);
    }

    public static void createUser(android.content.Context context, Pessoa pessoa) {

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN);

        mixpanel.getPeople().identify(pessoa.getUid());

        mixpanel.getPeople().set("Nome", pessoa.getNome());
        mixpanel.getPeople().set("Cidade", pessoa.getCidade());
        mixpanel.getPeople().set("Pais", pessoa.getPais());
        mixpanel.getPeople().set("Sexo", pessoa.getSexo());
        mixpanel.getPeople().set("Aniversario", pessoa.getAniversario());
        mixpanel.getPeople().set("Status de relacionamento", pessoa.getRelationship_status());
    }

}