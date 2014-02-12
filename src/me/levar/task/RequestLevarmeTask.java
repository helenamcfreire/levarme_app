package me.levar.task;


import android.os.AsyncTask;
import me.levar.entity.Pessoa;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class RequestLevarmeTask extends AsyncTask<String, String, String> {

    private Pessoa usuarioLogado;

    public RequestLevarmeTask() {
    }

    public RequestLevarmeTask(Pessoa usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    @Override
    protected String doInBackground(String... url) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {

            String urlRequest = url[0];
            if (usuarioLogado != null) {
                urlRequest = getUrlWithParams(url[0]);
            }

            response = httpclient.execute(new HttpGet(urlRequest));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseString;
    }

    private String getUrlWithParams(String url){

        if(!url.endsWith("?")){
            url += "?";
        }

        List<NameValuePair> params = new LinkedList<NameValuePair>();

        params.add(new BasicNameValuePair("uid", usuarioLogado.getUid()));
        params.add(new BasicNameValuePair("nome", usuarioLogado.getNome()));
        params.add(new BasicNameValuePair("registrationId", usuarioLogado.getRegistrationId()));

        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;

        return url;
    }

}