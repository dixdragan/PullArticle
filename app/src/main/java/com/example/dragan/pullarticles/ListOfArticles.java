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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ListOfArticles extends AppCompatActivity {


    private class ATlistOfArticles extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... params) {
            String s = "";
            String urls = "http://android.ogosense.net/interns/ace/articles.php";
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
                JSONArray jsonArray = new JSONArray(someString);
                LinearLayout layout = (LinearLayout) findViewById(R.id.InnerView);

                final View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickTextView(""+v.getId());
                    }
                };

                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    TextView tv = new TextView(ListOfArticles.this);
                    String str = jsonObject.getString("id") + ". " + jsonObject.getString("title") ;
                    tv.setId(Integer.parseInt(jsonObject.getString("id")));
                    tv.setText(str);
                    tv.setTextSize(25);
                    tv.setOnClickListener(onClickListener);
                    if(jsonObject.getString("featured").equals("1")) tv.setTextColor(0xffff0000);
                    layout.addView(tv);
                }


            } catch (JSONException e) {
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
        new ATlistOfArticles().execute();
        setContentView(R.layout.activity_list_of_articles);
    }

    public void onClickTextView(String id){
        Intent articleIntent = new Intent(ListOfArticles.this,ArticleView.class);
        articleIntent.putExtra("id", id);
        //ListOfArticles.this.finish();
        startActivity(articleIntent);
    }

}
