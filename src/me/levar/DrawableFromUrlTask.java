package me.levar;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class DrawableFromUrlTask extends AsyncTask<String, Void, Object> {

    private View view;
    private Drawable drawable;

    public DrawableFromUrlTask(View view){
        this.view = view;
    }

    @Override
    protected Object doInBackground(String... urls) {
        drawable = getDrawableFromUrl(urls[0]);
        return null; // here you can pass any string on response as on error or on success
    }

    public void onPostExecute(Object obj){

        if(drawable != null){

            ImageView foto = (ImageView) view.findViewById(R.id.imageFriend);
            foto.setImageDrawable(drawable);
        }

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