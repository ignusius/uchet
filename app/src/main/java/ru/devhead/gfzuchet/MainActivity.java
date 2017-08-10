package ru.devhead.gfzuchet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static ru.devhead.gfzuchet.Constants.FIRST_COLUMN;
import static ru.devhead.gfzuchet.Constants.FOURTH_COLUMN;
import static ru.devhead.gfzuchet.Constants.SECOND_COLUMN;
import static ru.devhead.gfzuchet.Constants.THIRD_COLUMN;

public class MainActivity extends Activity {

    private ArrayList<HashMap<String, String>> list;
    private DatabaseHelper db;
    private ArrayList<String> arrArticle;
    private ArrayList<String> arrTitle;
    private ArrayList<String> arrNote;
    private ArrayList<String> arrSum;
    private ArrayList<String> arrReject;
    private ListViewAdapter adapter;
    private ListView listView;
    private CleanableEditText search;
    private  int lastPoss;
    private  Switch mode;
    String cur_db;
    private  SharedPreferences sPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        sPref = getSharedPreferences("MAIN", MODE_PRIVATE);
        cur_db = sPref.getString("DB", "");


        File file_chck = new File(Environment.getExternalStorageDirectory() + "/GFZ/DB/"+cur_db);
        if(!file_chck.exists()) {

            CreateDir("/GFZ");
            CreateDir("/GFZ/Template");
            CreateDir("/GFZ/Reports");
            CreateDir("/GFZ/DB");

            copyAssets();

            File src = new File(Environment.getExternalStorageDirectory() + "/GFZ/Template/test.db");
            File dst = new File(Environment.getExternalStorageDirectory() + "/GFZ/DB/test.db");
            try {
                copy(src,dst);
            } catch (IOException e) {
                e.printStackTrace();
            }
            cur_db = "test.db";
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("DB", cur_db);
            ed.commit();

        }


        DatabaseHelper.cur_db = cur_db;
        if (cur_db==""){

            DatabaseHelper.cur_db= "test.db";
        }


        db = new DatabaseHelper(this);
        try {
            db.openDataBase();
        }

        catch (SQLException e) {
            e.printStackTrace();



        }


        final TextView textCurDB = (TextView) findViewById(R.id.textСurDB);
        textCurDB.setText(cur_db);




        final ImageButton settings = (ImageButton) findViewById(R.id.settings);



        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), settings);
                dropDownMenu.getMenuInflater().inflate(R.menu.menu, dropDownMenu.getMenu());
                //etText("DropDown Menu");
                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        //Toast.makeText(getApplicationContext(), "You have clicked " + menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        if (menuItem.getTitle().equals("Открыть базу")){
                            Intent intent = new Intent(MainActivity.this, DBActivity.class);
                            startActivity(intent);

                        }
                        if (menuItem.getTitle().equals("Создать базу")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Вы уверенны, что хотитете создать новую базу данных?");

                            builder.setPositiveButton("Создать", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button
                                    File src = new File(Environment.getExternalStorageDirectory() + "/GFZ/Template/template.db");
                                    File dst = new File(Environment.getExternalStorageDirectory() + "/GFZ/DB/"+
                                            new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Calendar.getInstance().getTime()).toString()
                                            +".db");
                                    try {
                                        copy(src,dst);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    SharedPreferences.Editor ed = sPref.edit();
                                    ed.putString("DB", new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Calendar.getInstance().getTime()).toString() +".db");
                                    ed.commit();


                                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finishAffinity();


                                }
                            });
                            builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();


                        }
                        if (menuItem.getTitle().equals("Выгрузить отчёт")) {

                            try {
                                db.exportCSV();
                            } catch (IOException e) {
                                Toast.makeText(getApplicationContext(), "Ошибка выгрузки", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                            String fileName = Environment.getExternalStorageDirectory() +  "/GFZ/Reports/Отчёт_"+cur_db.substring(0, cur_db.length() - 3)+".csv";
                            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                            emailIntent.setType("*/*");
                            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {sPref.getString("email", "")});
                            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Отчёт");
                            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(fileName)));
                            startActivity(Intent.createChooser(emailIntent, "Отправить отчёт"));

                        }

                        if (menuItem.getTitle().equals("Обновить номенклатуру")){

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Вы уверенны, что хотите обновить номенклатуру? Перед скачиванием теущая номенклатура будет удалена!");

                            builder.setPositiveButton("Обновить", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // execute this when the downloader must be fired
                                    final DownloadTask downloadTask = new DownloadTask(MainActivity.this);
                                    String DownloadUrl = sPref.getString("server", "");
                                    downloadTask.execute(DownloadUrl);

                                }


                            });
                            builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();


                        }
                        if (menuItem.getTitle().equals("Настройки")) {
                            Intent intent = new Intent(MainActivity.this, SettindsActivity.class);
                            startActivity(intent);
                        }




                        return true;
                    }
                });
                dropDownMenu.show();

            }
        });

         mode  = (Switch) findViewById(R.id.mode);
        mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                                            @Override
                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                                                SearcByText();
                                                listView.setSelection(lastPoss);
                                                listView.setAdapter(adapter);


                                            }
                                        });




        listView = (ListView) findViewById(R.id.listView1);

        list = new ArrayList<HashMap<String, String>>();

        try {

            arrArticle = (ArrayList<String>) db.getTable().get(0);
            arrTitle = (ArrayList<String>) db.getTable().get(1);
            arrNote = (ArrayList<String>) db.getTable().get(2);
            arrSum = (ArrayList<String>) db.getTable().get(3);
            arrReject = (ArrayList<String>) db.getTable().get(4);
        }
        catch (Exception e){
            File file_template = new File(Environment.getExternalStorageDirectory() + "/GFZ/DB/"+cur_db);
            file_template.delete();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);


        }


        for (int x = 0; x < arrArticle.size(); x++) {

            HashMap<String, String> temp = new HashMap<String, String>();

            temp.put(FIRST_COLUMN, arrArticle.get(x));
            temp.put(SECOND_COLUMN, arrTitle.get(x));
            //temp.put(THIRD_COLUMN, arrNote.get(x));
            temp.put(FOURTH_COLUMN, arrSum.get(x));
            list.add(temp);
            //temp.clear();
        }





        search = (CleanableEditText) findViewById(R.id.search);


        //search.setHint(cur_db);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SearcByText();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        adapter = new ListViewAdapter(this, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {


                int pos = position + 1;

                lastPoss =  pos-5;

                //Toast.makeText(MainActivity.this, " Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, NumActivity.class);
                intent.putExtra("article", arrArticle.get(pos-1));
                intent.putExtra("title", arrTitle.get(pos-1));
                intent.putExtra("sum", Integer.valueOf(arrSum.get(pos-1)));
                intent.putExtra("note", arrNote.get(pos-1));
                intent.putExtra("reject", Integer.valueOf(arrReject.get(pos-1)));
                startActivity(intent);
            }



        });

    }

    private  void  SearcByText(){
        list.clear();
        arrArticle = (ArrayList<String>) db.searchTable(String.valueOf(search.getText()),mode.isChecked()).get(0);
        arrTitle = (ArrayList<String>) db.searchTable(String.valueOf(search.getText()),mode.isChecked()).get(1);
        arrNote = (ArrayList<String>) db.searchTable(String.valueOf(search.getText()),mode.isChecked()).get(2);
        arrSum = (ArrayList<String>) db.searchTable(String.valueOf(search.getText()),mode.isChecked()).get(3);
        arrReject = (ArrayList<String>) db.searchTable(String.valueOf(search.getText()),mode.isChecked()).get(4);

        for (int x = 0; x < arrArticle.size(); x++) {

            HashMap<String, String> temp = new HashMap<String, String>();

            temp.put(FIRST_COLUMN, arrArticle.get(x));
            temp.put(SECOND_COLUMN, arrTitle.get(x));
            //temp.put(THIRD_COLUMN, arrNote.get(x));
            temp.put(FOURTH_COLUMN, arrSum.get(x));
            list.add(temp);
            listView.setAdapter(adapter);

            //temp.clear();

        }

    }

    public static void copy(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    private void copyFileStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("2");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(Environment.getExternalStorageDirectory()+"/GFZ/Template/", filename);
                out = new FileOutputStream(outFile);
                copyFileStream(in, out);
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    }


    private void CreateDir(String path){
        File folder = new File(Environment.getExternalStorageDirectory()+ path);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            Toast.makeText(getApplicationContext(), "Успешное создание директории: "+path, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Ошибка создания директории" + path, Toast.LENGTH_LONG).show();
        }
    }


        @Override
    protected void onResume() {
        super.onResume();
        SearcByText();
        listView.setSelection(lastPoss);



    }
}



