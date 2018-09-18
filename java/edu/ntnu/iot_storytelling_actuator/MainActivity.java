package edu.ntnu.iot_storytelling_actuator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

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

        try {
            Intent i = getIntent();
            String msg = i.getStringExtra("message");
            displayMessage(msg);
        }catch(NullPointerException e){
            Log.d("Debug", "Empty Intent");
        }
    }
    protected void onNewIntent(Intent i) {
        super.onNewIntent(i);
        try {
            String msg = i.getStringExtra("message");
            displayMessage(msg);
        }catch(NullPointerException e){
            Log.d("Debug", "Empty Intent, onNewIntent");
        }
    }
    public void displayMessage(String msg){
        TextView view = (TextView) findViewById(R.id.text_view1);
        view.setText(msg);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);
        mp.start();
    }
}
