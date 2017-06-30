package ru.devhead.gfzuchet;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SettindsActivity extends AppCompatActivity {
    SharedPreferences sPref;
    private EditText email;
    private EditText server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settinds);
        getSupportActionBar().setTitle("Настройки");


        sPref = getSharedPreferences("MAIN", MODE_PRIVATE);


        email = (EditText) findViewById(R.id.email);
        server = (EditText) findViewById(R.id.server);


        email.setText(sPref.getString("email", ""));
        server.setText(sPref.getString("server", ""));





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.back_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back_menu:

                finish();
                break;
            case R.id.save_menu:
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("email", String.valueOf(email.getText()));
                ed.putString("server", String.valueOf(server.getText()));

                ed.commit();
                Toast.makeText(getApplicationContext(), "Сохранено", Toast.LENGTH_LONG).show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

