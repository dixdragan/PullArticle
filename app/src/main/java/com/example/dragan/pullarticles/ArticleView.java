package com.example.dragan.pullarticles;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ArticleView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_view);
        Bundle bundle=getIntent().getExtras();
        URL url = null;
        try {
            url = new URL(bundle.getString("URL"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new Task().execute(url);
    }
    private class Task extends AsyncTask<URL,Void,String>{
        @Override
        protected String doInBackground(URL... params) {
            URL url=params[0];
            String text = "";
            BufferedReader reader = null;
            try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            text = sb.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return text;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
            JSONObject jsonObject=new JSONObject(s);
            String title = jsonObject.getString("title");
            String introtext = jsonObject.getString("introtext");
            String createdToParse = jsonObject.getString("created");
            SimpleDateFormat sdfGet=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date createdDate= sdfGet.parse(createdToParse);
            SimpleDateFormat sdfDisplay=new SimpleDateFormat("dd.MM.yyyy");
            String createdToDisplay = sdfDisplay.format(createdDate).toString();
                String text=title + "\n" + introtext + "\n" + createdToDisplay;
                Html.fromHtml(text);
            TextView textView= (TextView) findViewById(R.id.textView);
            textView.setText(Html.fromHtml(text));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
