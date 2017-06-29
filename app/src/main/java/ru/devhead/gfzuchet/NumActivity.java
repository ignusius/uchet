package ru.devhead.gfzuchet;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.SQLException;

public class NumActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private String cur_db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_num);


        Bundle b = getIntent().getExtras();
        String title = b.getString("title");
        final String article = b.getString("article");
        final int sum_v = b.getInt("sum");
        final String note_v = b.getString("note");

        SharedPreferences sPref = getSharedPreferences("MAIN",MODE_PRIVATE);
        cur_db = sPref.getString("DB", "").toString();

        db = new DatabaseHelper(this);
        try {
            db.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final TextView artcl = (TextView) findViewById(R.id.article);
        artcl.setText(article);
        final TextView head = (TextView) findViewById(R.id.title);
        head.setText(title);
        final TextView history = (TextView) findViewById(R.id.history);
        history.setText(note_v);
        final TextView sumView = (TextView) findViewById(R.id.SumView);
        sumView.setText(String.valueOf(sum_v));
        final Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        final EditText editAddSum = (EditText) findViewById(R.id.editAddSum);
        editAddSum.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            int summa = sum_v + Integer.parseInt(editAddSum.getText().toString());
                            db.UpdateTable(article, String.valueOf(summa));

                            String note = note_v+ " + "+ editAddSum.getText().toString();
                            db.UpdateNote(article, note);



                            finish();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }
}
