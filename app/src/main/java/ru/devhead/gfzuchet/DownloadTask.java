package ru.devhead.gfzuchet;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// usually, subclasses of AsyncTask are declared inside the activity class.
// that way, you can easily modify the UI thread from here
public class DownloadTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private PowerManager.WakeLock mWakeLock;

    public DownloadTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        Handler handler=new Handler();
        handler=  new Handler(context.getMainLooper());


        File file_template = new File(Environment.getExternalStorageDirectory() + "/GFZ/Template/template.db");
        file_template.delete();
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }


            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            //Toast.makeText(context.getApplicationContext(), fileLength,Toast.LENGTH_LONG).show();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/GFZ/Template/template.db");

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {

            return e.toString();


        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {

            }

            if (connection != null)
                handler.post( new Runnable(){
                    public void run(){
                        Toast.makeText(context.getApplicationContext(), "Возможно неправильно указан сервер или нет доступа в интернет",Toast.LENGTH_LONG).show();
                    }
                });
                connection.disconnect();


        }
        handler.post( new Runnable(){
            public void run(){
                Toast.makeText(context.getApplicationContext(), "Номенклатура успешно обнавлена",Toast.LENGTH_LONG).show();
            }
        });

        return null;
    }
}
