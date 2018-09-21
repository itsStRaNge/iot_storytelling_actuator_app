package edu.ntnu.iot_storytelling_actuator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements ValueEventListener{
    public static final String DEVICE_NUMBER = "0";
    public static final String DEVICE_Key = "Actuator";
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

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference host = database.child(HOST_KEY);
        host.addValueEventListener(this);
        DatabaseReference device = database.child(DEVICE_Key).child(DEVICE_NUMBER);
        device.addValueEventListener(this);
    }

    private void playAudio(String file_name){
        try {
            File directory = this.getFilesDir();
            File file = new File(directory, file_name);

            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(file.getPath());
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showImage(String file_name){
        File directory = this.getFilesDir();
        File file = new File(directory, file_name);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        ((ImageView) findViewById(R.id.image_view)).setImageBitmap(bitmap);
    }

    public void updateState(DataSnapshot state){
        String audio_file = state.child(AUDIO_Key).getValue(String.class);
        String image_file = state.child(IMAGE_Key).getValue(String.class);
        showImage(image_file);
        playAudio(audio_file);
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

                    //new DownloadManager(this, IMAGE_Key).execute(image_files);
                    new DownloadManager(this, AUDIO_Key).execute(audio_files);
                    break;
                }
                case DEVICE_NUMBER:{
                    updateState(dataSnapshot);
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
