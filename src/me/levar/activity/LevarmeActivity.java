package me.levar.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import me.levar.entity.Pessoa;

/**
 * Created with IntelliJ IDEA.
 * User: Helena
 * Date: 15/08/13
 * Time: 22:58
 * To change this template use File | Settings | File Templates.
 */
public class LevarmeActivity extends FragmentActivity {

    private Pessoa currentUser = new Pessoa();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}