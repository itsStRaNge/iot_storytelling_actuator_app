package edu.ntnu.iot_storytelling_actuator;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseManager implements ValueEventListener {

    private static final String DEVICE_NUMBER = "0";
    private static final String DEVICE_Key = "Actuator";

    private static final String HOST_KEY = "Host";
    private static final String HOST_IP_KEY = "ip";
    private static final String HOST_PORT_KEY = "http_port";

    public static final String AUDIO_Key = "audio";
    public static final String IMAGE_Key = "image";
    public static final String TEXT_Key = "text";

    private static final String SRC_AUDIO_Key = "Audio";
    private static final String SRC_IMAGE_Key = "Images";
    private static final String SRC_TEXT_Key = "Text";

    private MainActivity m_context;

    public FirebaseManager(MainActivity context){
        m_context = context;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference host = database.child(HOST_KEY);
        host.addValueEventListener(this);
        DatabaseReference device = database.child(DEVICE_Key).child(DEVICE_NUMBER);
        device.addValueEventListener(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();

        if (key != null) {
            switch (key) {
                case HOST_KEY: {
                    DownloadManager.HOST_IP = dataSnapshot.child(HOST_IP_KEY).getValue(String.class);
                    DownloadManager.HOST_PORT = dataSnapshot.child(HOST_PORT_KEY).getValue(Integer.class);

                    ArrayList<String> audio_files =
                            (ArrayList<String>) dataSnapshot.child(SRC_AUDIO_Key).getValue();
                    ArrayList<String> image_files =
                            (ArrayList<String>) dataSnapshot.child(SRC_IMAGE_Key).getValue();
                    ArrayList<String> text_files =
                            (ArrayList<String>) dataSnapshot.child(SRC_TEXT_Key).getValue();

                    m_context.deleteCache();

                    // start downloading
                    m_context.m_progress_bar.setProgress(0);
                    m_context.m_progress_text.setText(R.string.progress_default_text);
                    m_context.m_progess_layout.setVisibility(View.VISIBLE);
                    new DownloadManager(m_context).execute(audio_files, image_files, text_files);
                    break;
                }
                case DEVICE_NUMBER:{
                    updateState(dataSnapshot);
                    break;
                }
            }
        }
    }

    private void updateState(DataSnapshot state){
        String audio_file = state.child(AUDIO_Key).getValue(String.class);
        String image_file = state.child(IMAGE_Key).getValue(String.class);
        String text_file = state.child(TEXT_Key).getValue(String.class);

        Log.d("Debug", "update State: " + audio_file
                + " - " + image_file
                + " - " + text_file);

        m_context.showImage(image_file);
        m_context.displayText(text_file);
        m_context.playAudio(audio_file);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.w("Error", "loadPost:onCancelled", databaseError.toException());
    }
}
