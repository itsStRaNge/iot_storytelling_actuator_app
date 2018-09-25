package edu.ntnu.iot_storytelling_actuator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new FirebaseManager(this);
    }

    public void playAudio(String file_name){
        try {
            File directory = this.getFilesDir();
            File file = new File(directory, file_name);

            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(file.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayText(String file_name){
        TextView text_view = findViewById(R.id.text_view);
        text_view.setText("");
        try {
            File directory = this.getFilesDir();
            File file = new File(directory, file_name);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null)
                text_view.append(st);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void showImage(String file_name){
        File directory = this.getFilesDir();
        File file = new File(directory, file_name);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        ((ImageView) findViewById(R.id.image_view)).setImageBitmap(bitmap);
    }

    public void deleteCache() {
        try {
            File dir = getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return dir != null && dir.isFile() && dir.delete();
    }
}
