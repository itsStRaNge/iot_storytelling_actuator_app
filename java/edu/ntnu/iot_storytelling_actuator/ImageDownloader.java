package edu.ntnu.iot_storytelling_actuator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class ImageDownloader  extends AsyncTask<ArrayList, Integer, Void> {

    private Context m_context;

    public ImageDownloader(Context context){
        m_context = context;
    }

    protected Void doInBackground(ArrayList... lists) {
        ArrayList files = lists[0];
        try {
            for(Integer i=0;i<files.size();i++){
                String file_name = files.get(i).toString();

                // download file
                java.net.URL url = new java.net.URL("http",
                        MainActivity.m_host_ip, MainActivity.m_host_port,
                        "image/" + file_name);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap image = BitmapFactory.decodeStream(input);

                // save file
                try {
                    FileOutputStream out;
                    out = m_context.openFileOutput(file_name, Context.MODE_PRIVATE);
                    image.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // publish progress
                publishProgress((int) ((i+1 / (float) files.size()) * 100));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
        Log.d("Download", String.valueOf(progress[0]) + "%");
    }

    protected void onPostExecute(Void...  p) {
        m_context = null;
    }
}