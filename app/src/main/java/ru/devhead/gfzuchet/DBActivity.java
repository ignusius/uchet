package ru.devhead.gfzuchet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DBActivity extends AppCompatActivity {
    private SharedPreferences mSettings;
    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);


        ListView lvMain = (ListView) findViewById(R.id.listDB);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                          @Override
                                          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                              final String file = (String) parent.getAdapter().getItem(position);
                                              Toast.makeText(DBActivity.this,  file, Toast.LENGTH_SHORT).show();
                                              sPref = getSharedPreferences("MAIN",MODE_PRIVATE);
                                              SharedPreferences.Editor ed = sPref.edit();
                                              ed.putString("DB", file);
                                              ed.commit();
                                              DatabaseHelper.cur_db= sPref.getString("DB", "").toString();
                                              Intent intent = new Intent(DBActivity.this, MainActivity.class);
                                              startActivity(intent);
                                              finishAffinity();

                                          }
                                      });

        // создаем адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, DirDB());

        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);

    }


    private List<String> DirDB() {

        ArrayList<String> tFileList = new ArrayList<String>();
        //It have to be matched with the directory in SDCard
        File f = new File(Environment.getExternalStorageDirectory() + "/GFZ/DB");

        File[] files = f.listFiles();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            String filePath = file.getPath();
            if (filePath.endsWith(".db")) {
                String[] tmparr = filePath.split("/");
                tFileList.add(tmparr[tmparr.length - 1]);
            }
        }
            Collections.reverse(tFileList);

            return tFileList;
        }



}
