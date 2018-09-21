package edu.ntnu.iot_storytelling_actuator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ValueEventListener{
    public static final String DEVICE_NAME = "Actuator1";
    public static final String HOST_KEY = "Host";
    public static final String HOST_IP_KEY = "ip";
    public static final String HOST_PORT_KEY = "http_port";
    public static final String AUDIO_Key = "audio";
    public static final String IMAGE_Key = "image";;
    public static final String SRC_AUDIO_Key = "Audio";
    public static final String SRC_IMAGE_Key = "Images";

    public static String m_host_ip = "";
    public static Integer m_host_port = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseReference m_Database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference host = m_Database.child(HOST_KEY);
        host.addValueEventListener(this);
        DatabaseReference device = m_Database.child(DEVICE_NAME);
        device.addValueEventListener(this);
    }

    private void playAudio(String file){
        try {
            java.net.URL url = new java.net.URL("http",m_host_ip, m_host_port, "audio/" + file);
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url.toString());
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showImage(String file_name){
        File directory = this.getFilesDir();
        File file = new File(directory, file_name);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        ((ImageView) findViewById(R.id.image_view)).setImageBitmap(bitmap);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();

        if (key != null) {
            switch (key) {
                case HOST_KEY: {
                    m_host_ip = dataSnapshot.child(HOST_IP_KEY).getValue(String.class);
                    m_host_port = dataSnapshot.child(HOST_PORT_KEY).getValue(Integer.class);

                    ArrayList<String> audio_files =
                            (ArrayList<String>) dataSnapshot.child(SRC_AUDIO_Key).getValue();
                    ArrayList<String> image_files =
                            (ArrayList<String>) dataSnapshot.child(SRC_IMAGE_Key).getValue();

                    new ImageDownloader(this).execute(image_files);
                    break;
                }
                case DEVICE_NAME:{
                    Log.d("Firebase", "Update Image/Sound");
                    String audio_file = dataSnapshot.child(AUDIO_Key).getValue(String.class);
                    String image_file = dataSnapshot.child(IMAGE_Key).getValue(String.class);
                    showImage(image_file);
                    //playAudio(audio_file);
                    break;
                }
            }
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.w("Error", "loadPost:onCancelled", databaseError.toException());
    }
}
