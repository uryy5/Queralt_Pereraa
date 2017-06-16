package com.example.ivan.reversi_final.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ivan.reversi_final.R;
import com.example.ivan.reversi_final.activity.ConfigureActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ResultsActivity extends Activity {

    private EditText mailText;
    private Bundle bundle;
    private String name;
    private int size;
    private int player;
    private int oponent;
    private int diferencia;
    private String time;
    private String log;
    private String result;
    private Date date;
    private String datess;
    private boolean timechecked;
    private String temps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        if (savedInstanceState == null) {
            bundle = this.getIntent().getExtras();
        } else {
            bundle = savedInstanceState.getBundle("Bundle");
        }
        initializeResults();
        saveResults();
        date = new Date();
        DateFormat dates = new SimpleDateFormat("E d, MMM, y k:m:s:a");
        TextView dtTextView = (TextView) findViewById(R.id.date_and_time);
        TextView logTextView = (TextView) findViewById(R.id.log);
        mailText = (EditText) findViewById(R.id.email);
        datess=dates.format(date);
        dtTextView.setText("" + datess);
        if(player > oponent){
            diferencia = player -oponent;
        }
        else{
            diferencia = oponent -player;
        }

        log = result + diferencia +" "+ "caselles de diferencia \n";
        String logTemplate = getString(R.string.log_template);
        log += String.format(logTemplate, name,oponent ,player, size, time);

        logTextView.setText(log);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.menu_main, m);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsFragment.class);
                startActivity(intent);
                return true;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showResult(CharSequence text) {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBundle("Bundle", bundle);
        super.onSaveInstanceState(outState);
    }

    private void initializeResults() {
        player = bundle.getInt("jugador1");
        oponent = bundle.getInt("jugador2");
        name = bundle.getString("name");
        size = bundle.getInt("gridsize");
        time = bundle.getString("time");
        result = bundle.getString("log");
        timechecked = bundle.getBoolean("checktime");


    }

    private void saveResults() {
        timechecked = bundle.getBoolean("checktime");
        //System.out.println("HOLAAAAAAAAAAAAAAAAAAAAA " + timechecked);
        ContentValues newRegister = new ContentValues();
        if(timechecked){
            temps="Esta activat el temps";

        }
        else{
            temps="No esta activat el temps";
        }
        newRegister.put("alias", name);
        DateFormat dates = new SimpleDateFormat("E d, MMM, y k:m:s:a");
        date = new Date();
        datess=" "+ dates.format(date);;
        newRegister.put("end_date", datess);
        newRegister.put("num_cells", size);
        newRegister.put("flag_time", temps);
        newRegister.put("white", player);
        newRegister.put("black", oponent);
        newRegister.put("result", result);
        newRegister.put("time_total",time);

        new InsertTask(this.getApplicationContext()).execute(newRegister);
    }
    public class InsertTask extends AsyncTask<ContentValues, Void, Long> {
        private final Context mContext;

        public InsertTask(Context context) {
            super();
            this.mContext = context;
        }

        protected Long doInBackground(ContentValues... newRegister) {
            long id = -1;
            GamesSQLiteHelper gamesSQLiteHelper =
                    new GamesSQLiteHelper(mContext, "DBGames", null, 1);

            SQLiteDatabase db = gamesSQLiteHelper.getWritableDatabase();
            if (db != null) {
                id = db.insert("Games", null, newRegister[0]);
            }
            return id;
        }

        protected void onPostExecute(Long id) {
            if (id == -1) {
                showResult("Error inserting register!");
            } else {
                showResult("Registre inserit correctament");
            }
        }

    }

    public void sendMail(View v) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode(mailText.getText().toString()) + "?subject=" +
                Uri.encode(getString(R.string.subject)) + "&body=" + Uri.encode(log);
        Uri uri = Uri.parse(uriText);
        intent.setData(uri);
        startActivity(Intent.createChooser(intent, "Send mail..."));
    }

    public void onBackClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onExitClick(View v) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


}
