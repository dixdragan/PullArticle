package com.example.dragan.pullarticles;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ListOfArticles extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_articles);
        URL url=null;
        try {
            url = new URL("http://android.ogosense.net/interns/ace/articles.php");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new Task().execute(url);

    }

    private class Task extends AsyncTask<URL,Void,String>{
        @Override
        protected String doInBackground(URL... params) {
            String text = "";
            BufferedReader reader = null;
            try {
            URL url=params[0];
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
            JSONArray jsonArray=new JSONArray(s);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                final String id = jsonObject.getString("id");
                final String title = jsonObject.getString("title");
                TextView textView=new TextView(ListOfArticles.this);
                textView.setText(id + " " + title);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent articleIntent = new Intent(ListOfArticles.this, ArticleView.class);
                        articleIntent.putExtra("URL", "http://android.ogosense.net/interns/ace/article.php?id=" + id);
                        startActivity(articleIntent);
                    }
                });
                LinearLayout linearLayout= (LinearLayout) findViewById(R.id.linearLayout);
                linearLayout.addView(textView);
            }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
