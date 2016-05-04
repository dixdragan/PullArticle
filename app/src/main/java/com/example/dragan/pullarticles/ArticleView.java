package com.example.dragan.pullarticles;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ArticleView extends AppCompatActivity {


    private class ATArticles extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... params) {
            Bundle extras = getIntent().getExtras();
            String receivedId = extras.getString("id");
            String s = "";
            String urls = "http://android.ogosense.net/interns/ace/article.php?id=" + receivedId ;
            try {

                URL url = new URL(urls);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                //s  += connection.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String r;
                while((r = reader.readLine()) != null) s += r + "\n";

            }catch (Exception e) {
                System.out.println(e);
            }
            return s;
        }

        protected void onPostExecute(String someString) {

            try {
                JSONObject jsonObject = new JSONObject(someString);
                LinearLayout layout = (LinearLayout) findViewById(R.id.ArticleViewz);

                TextView title      = new TextView(ArticleView.this);
                TextView introtext  = new TextView(ArticleView.this);
                TextView created    = new TextView(ArticleView.this);

                title.setText(jsonObject.getString("title"));
                title.setTextSize(30);

                String introString          = Html.fromHtml(jsonObject.getString("introtext")).toString();
                introtext.setText(introString);

                String createdToParse       = jsonObject.getString("created");
                SimpleDateFormat sdfGet     = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date createdDate            = sdfGet.parse(createdToParse);
                SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd.MM.yyyy");
                String createdToDisplay     = sdfDisplay.format(createdDate);

                created.setText("Created: " + createdToDisplay + "\n");
                created.setTextSize(8);

                layout.addView(title);
                layout.addView(created);
                layout.addView(introtext);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        protected void onProgressUpdate(String... progress) {
        }
        protected void publishProgress(String result) {
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ATArticles().execute();
        setContentView(R.layout.activity_article_view);
    }
}
