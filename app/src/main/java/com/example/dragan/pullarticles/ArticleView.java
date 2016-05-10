package com.example.dragan.pullarticles;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ArticleView extends AppCompatActivity {

    URL imageURL = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_view);
        Bundle bundle = getIntent().getExtras();
        URL url = null;
        try {
            url = new URL(bundle.getString("URL"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new Task().execute(url);

    }



    private class GetPictureTask extends AsyncTask<URL, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(URL... params) {
            URL url = params[0];
            Bitmap image=null;
            try {
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView= (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(bitmap);
        }
    }

    private class Task extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            String text = "";
            BufferedReader reader = null;
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
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
                JSONObject jsonObject = new JSONObject(s);
                String title = jsonObject.getString("title");
                String introtext = jsonObject.getString("introtext");
                String createdToParse = jsonObject.getString("created");
                String images = jsonObject.getString("images");
                JSONObject image_introJSON = new JSONObject(images);
                String image_intro = image_introJSON.getString("image_intro");
                imageURL = new URL("http://android.ogosense.net/" + image_intro);
                SimpleDateFormat sdfGet = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date createdDate = sdfGet.parse(createdToParse);
                SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd.MM.yyyy");
                String createdToDisplay = sdfDisplay.format(createdDate).toString();
                TextView textView = (TextView) findViewById(R.id.titleTextView);
                textView.setText(Html.fromHtml(title));
                TextView introtextTextView = (TextView) findViewById(R.id.introtextTextView);
                introtextTextView.setText(Html.fromHtml(introtext));
                TextView createdDateTextView = (TextView) findViewById(R.id.createdDateTextView);
                createdDateTextView.setText(Html.fromHtml(createdToDisplay));
                new GetPictureTask().execute(imageURL);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
}
