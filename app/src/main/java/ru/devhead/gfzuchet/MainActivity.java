package ru.devhead.gfzuchet;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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
    private ListViewAdapter adapter;
    private ListView listView;
    private CleanableEditText search;
    private  int lastPoss;
    private  Switch mode;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        db = new DatabaseHelper(this);
        try {
            db.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
                        Toast.makeText(getApplicationContext(), "You have clicked " + menuItem.getTitle(), Toast.LENGTH_LONG).show();
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

                                            }
                                        });




        listView = (ListView) findViewById(R.id.listView1);

        list = new ArrayList<HashMap<String, String>>();

        arrArticle = (ArrayList<String>) db.getTable().get(0);
        arrTitle = (ArrayList<String>) db.getTable().get(1);
        arrNote = (ArrayList<String>) db.getTable().get(2);
        arrSum = (ArrayList<String>) db.getTable().get(3);


        for (int x = 0; x < arrArticle.size(); x++) {

            HashMap<String, String> temp = new HashMap<String, String>();

            temp.put(FIRST_COLUMN, arrArticle.get(x));
            temp.put(SECOND_COLUMN, arrTitle.get(x));
            temp.put(THIRD_COLUMN, arrNote.get(x));
            temp.put(FOURTH_COLUMN, arrSum.get(x));
            list.add(temp);
            //temp.clear();
        }





        search = (CleanableEditText) findViewById(R.id.search);
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
                Toast.makeText(MainActivity.this, Integer.toString(pos) + " Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, NumActivity.class);
                intent.putExtra("article", arrArticle.get(pos-1));
                intent.putExtra("title", arrTitle.get(pos-1));
                intent.putExtra("sum", Integer.valueOf(arrSum.get(pos-1)));
                intent.putExtra("note", arrNote.get(pos-1));
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

        for (int x = 0; x < arrArticle.size(); x++) {

            HashMap<String, String> temp = new HashMap<String, String>();

            temp.put(FIRST_COLUMN, arrArticle.get(x));
            temp.put(SECOND_COLUMN, arrTitle.get(x));
            temp.put(THIRD_COLUMN, arrNote.get(x));
            temp.put(FOURTH_COLUMN, arrSum.get(x));
            list.add(temp);
            listView.setAdapter(adapter);

            //temp.clear();

        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        SearcByText();
        listView.setSelection(lastPoss);





    }
}

