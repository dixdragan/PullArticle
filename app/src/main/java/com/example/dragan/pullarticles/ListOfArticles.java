package com.example.dragan.pullarticles;

import android.content.Intent;
import android.graphics.Color;
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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ListOfArticles extends AppCompatActivity {

    String uid=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_articles);
        Bundle bundle=getIntent().getExtras();
        uid=bundle.getString("uid");
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
            URL url=params[0];
            JSONObject object=new JSONObject();
            try {
            object.put("uid",uid);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(object.toString());
            wr.flush();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            text = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line + "\n");
            }


            text = sb.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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
                String featured = jsonObject.getString("featured");
                TextView textView=new TextView(ListOfArticles.this);
                textView.setText(id + " " + title);
                if("1".equals(featured)){
                    textView.setTextColor(Color.parseColor("#ff0000"));
                }
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
