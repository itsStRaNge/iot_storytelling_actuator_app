package edu.ntnu.iot_storytelling_actuator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("news")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.d("Debug", "Cannot subscribe to topic");
                        }
                    }
                });

        handleServerData();
    }
    protected void onNewIntent(Intent i) {
        super.onNewIntent(i);
        setIntent(i);
        handleServerData();
    }

    public void handleServerData() {
        try {
            Intent i = getIntent();
            String json_str = i.getStringExtra("message");
            Log.d("Debug", json_str);
            JSONObject json = new JSONObject(json_str);

            if(json.has("audio")) {
                try {
                    String url =json.getString("audio");
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(json.has("image")) {
                new ImageRequest().execute(json.getString("image"));
            }


        } catch (NullPointerException e) {
            Log.e("Error", "NullPointerException");
        } catch (JSONException e) {
            Log.e("Error", "JSON Exception");
        }

    }

    private class ImageRequest  extends AsyncTask<String, Integer, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            try {
                java.net.URL url = new java.net.URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Bitmap bit) {
            if(bit != null)
                ((ImageView) findViewById(R.id.image_view)).setImageBitmap(bit);
        }
    }
}
