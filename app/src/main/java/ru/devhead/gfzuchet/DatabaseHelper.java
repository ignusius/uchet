package ru.devhead.gfzuchet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "subject";
    private static String DB_PATH = Environment.getExternalStorageDirectory() + "/GFZ/gfzdata";
    private static SQLiteDatabase myDataBase;


    static String query;

    private Cursor data;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(DICTIONARY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void openDataBase() throws SQLException {
        try {

            myDataBase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception e) {


        }
    }


    public ArrayList getTable() {
        ArrayList<ArrayList> total = new ArrayList<ArrayList>();
        ArrayList<String> article = new ArrayList<String>();
        ArrayList<String> title = new ArrayList<String>();
        ArrayList<String> note = new ArrayList<String>();
        ArrayList<String> sum = new ArrayList<String>();


        query = "select rowid as _id, article, title, note, sum from data ";
        Log.d("++++++++", query);

        data = myDataBase.rawQuery(query, null);

        while (data.moveToNext()) {

            article.add(data.getString(data.getColumnIndex("article")));
            title.add(data.getString(data.getColumnIndex("title")));
            note.add(data.getString(data.getColumnIndex("note")));
            sum.add(data.getString(data.getColumnIndex("sum")));

        }

        total.add(article);
        total.add(title);
        total.add(note);
        total.add(sum);
        //total.add(id);

        return total;
    }


    public ArrayList searchTable(String value, boolean mode) {
        ArrayList<Long> id = new ArrayList<>();
        ArrayList<ArrayList> total = new ArrayList<ArrayList>();
        ArrayList<String> article = new ArrayList<String>();
        ArrayList<String> title = new ArrayList<String>();
        ArrayList<String> note = new ArrayList<String>();
        ArrayList<String> sum = new ArrayList<String>();


        if (mode==false) {
            query = "select rowid as _id, article, title, note, sum from data where title like  ? or article like ? ";
        }
        else{
            query = "select rowid as _id, article, title, note, sum from data where (title like  ? or article like ? ) and sum > 0 ";
            }
        Log.d("++++++++", query);

        data = myDataBase.rawQuery(query, new String[]{
                "%"+value+"%",
                 value+"%"
        });

        while (data.moveToNext()) {

            article.add(data.getString(data.getColumnIndex("article")));
            title.add(data.getString(data.getColumnIndex("title")));
            note.add(data.getString(data.getColumnIndex("note")));
            sum.add(data.getString(data.getColumnIndex("sum")));
            id.add(data.getLong(data.getColumnIndex("_id")));
        }

        total.add(article);
        total.add(title);
        total.add(note);
        total.add(sum);

        return total;
    }

    public  void UpdateTable(String num, String value) {


        query = "UPDATE data SET sum='"+value+"' WHERE article="+num+"";
        Log.d("++++++++", query);

        data = myDataBase.rawQuery(query, null);
        data.moveToFirst();
        data.close();
    }

    public  void UpdateNote(String num, String value) {


        query = "UPDATE data SET note='"+value+"' WHERE article="+num+"";
        Log.d("++++++++", query);

        data = myDataBase.rawQuery(query, null);
        data.moveToFirst();
        data.close();
    }




}