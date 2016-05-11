package com.example.dragan.pullarticles;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ListOfArticles extends AppCompatActivity {

    private class Task extends AsyncTask<URL, Void, String> {

        protected String doInBackground(URL... params) {
            String text = "";
            BufferedReader reader;
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
                String line;
                text = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line).append("\n");
                }

                text = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return text;
        }

        protected void onPostExecute(String someString) {
            super.onPostExecute(someString);
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
    }

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

    public void onClickTextView(String id){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(!(activeNetwork != null && activeNetwork.isConnectedOrConnecting()))
            Toast.makeText(getApplicationContext(), "Server unreachable", Toast.LENGTH_SHORT).show();
        else {
            Intent articleIntent = new Intent(ListOfArticles.this, ArticleView.class);
            articleIntent.putExtra("id", id);
            //ListOfArticles.this.finish();
            startActivity(articleIntent);
        }
    }

}
