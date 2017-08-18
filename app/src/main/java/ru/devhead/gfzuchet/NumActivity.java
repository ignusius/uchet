package ru.devhead.gfzuchet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NumActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private String cur_db;
    private boolean validate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_num);



        Bundle b = getIntent().getExtras();
        String title = b.getString("title");
        final String article = b.getString("article");
        final int sum_v = b.getInt("sum");
        final String note_v = b.getString("note");
        final int sum_r = b.getInt("reject");

        SharedPreferences sPref = getSharedPreferences("MAIN", MODE_PRIVATE);
        cur_db = sPref.getString("DB", "").toString();

        db = new DatabaseHelper(this);
        try {
            db.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getSupportActionBar().setTitle(article);

        final TextView head = (TextView) findViewById(R.id.title);
        head.setText(title);
        final TextView history = (TextView) findViewById(R.id.history);
        history.setText(note_v);
        final TextView sumView = (TextView) findViewById(R.id.SumView);
        sumView.setText(String.valueOf(sum_v));
        final TextView sumRejectView = (TextView) findViewById(R.id.SumRejectView);
        sumRejectView.setText(String.valueOf(sum_r));


        final EditText editAddSum = (EditText) findViewById(R.id.editAddSum);
        editAddSum.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            if(validate) {
                                int summa = sum_v + Integer.parseInt(editAddSum.getText().toString());
                                db.UpdateTable(article, String.valueOf(summa));

                                String note = note_v + " + " + editAddSum.getText().toString();
                                db.UpdateNote(article, note);


                                finish();
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        final  Button toReject = (Button) findViewById(R.id.bt_reject);
        toReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                AlertDialog.Builder builder = new AlertDialog.Builder(NumActivity.this);
                builder.setMessage("Вы уверенны, что хотите добавить указанное кол-во в брак?");

                builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //Log.d("++++","OK");
                        int summa = sum_v - Integer.parseInt(editAddSum.getText().toString());
                        db.UpdateTable(article, String.valueOf(summa));

                        int reject = sum_r + Integer.parseInt(editAddSum.getText().toString());
                        db.UpdateReject(article, String.valueOf(reject));

                        String note = note_v + " - " + editAddSum.getText().toString();
                        db.UpdateNote(article, note);

                        finish();

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
        });

        editAddSum.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validate=true;
                try {
                    if ((Integer.parseInt(editAddSum.getText().toString()) > 100)) {
                        if(editAddSum.getText().toString().trim().length() != 0) {
                            editAddSum.setError("Слишком большое значене > 100");
                        }
                        validate = false;
                    }

                }
                catch (Exception e){
                    if(editAddSum.getText().toString().trim().length() != 0) {
                        editAddSum.setError("Некорректное значение");
                    }
                    validate=false;

                    }



            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back_menu:

                finish();
                break;
        }

            return super.onOptionsItemSelected(item);
        }


}
