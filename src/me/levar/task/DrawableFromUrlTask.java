package me.levar.task;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.URL;

public class DrawableFromUrlTask extends AsyncTask<String, Void, Object> {

    @Override
    protected Object doInBackground(String... urls) {
        return getDrawableFromUrl(urls[0]);
    }

    private Drawable getDrawableFromUrl(String url)
    {
        try
        {
            InputStream stream = (InputStream) new URL(url).getContent();
            return Drawable.createFromStream(stream, "src name");
        } catch (Exception e) {
            return null;
        }
    }

}