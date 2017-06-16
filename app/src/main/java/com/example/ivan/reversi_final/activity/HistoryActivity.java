package com.example.ivan.reversi_final.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.ivan.reversi_final.R;


public class HistoryActivity extends AppCompatActivity implements
        HistoryListFragment.OnFragmentInteractionListener {

    private ListFragment listFragment;
    private SQLiteDatabase db;
    private Cursor c;
    private boolean hasDetail;
    private HistoryDetailFragment detailFragment;
    private int selectedItem = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        GamesSQLiteHelper gamesSQLiteHelper = new GamesSQLiteHelper(this, "DBGames", null, 1);
        db = gamesSQLiteHelper.getWritableDatabase();
        listFragment = (ListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.ListFrag);


        initializeAdapter();
        hasDetail = (getSupportFragmentManager().findFragmentById(R.id.DetailFrag) != null);
        if (hasDetail) {
            detailFragment =
                    (HistoryDetailFragment) getSupportFragmentManager().findFragmentById(R.id.DetailFrag);
            detailFragment.show(0);
        }
    }
    public void onBackClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void initializeAdapter() {
        String[] from = new String[]{"alias", "end_date", "result"};
        int[] to = new int[]{R.id.alias, R.id.end_date, R.id.result};
        c = db.rawQuery("SELECT * FROM Games", null);

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.history_item, c, from, to, 0);
        if (c.moveToFirst()) {
            showResult("Consulta realitzada correctament");
        } else {
            showResult("La consulta no conte resultats");
        }
        listFragment.setListAdapter(cursorAdapter);
    }

    private void showResult(CharSequence text) {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        c.close();
        db.close();
    }

    @Override
    public void onFragmentInteraction(int id) {
        if (hasDetail) {
            detailFragment.show(id);
        } else {
            Intent i = new Intent(this, HistoryDetailActivity.class);
            i.putExtra("id", id);
            startActivity(i);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        selectedItem = i;

        view.setSelected(true);
        return true;
    }



    private void setAlias(int id, String alias) {
        String sid = String.valueOf(id + 1);
        new UpdateTask(this.getApplicationContext()).execute(sid, alias);
    }

    public class UpdateTask extends AsyncTask<String, Void, Long> {
        private final Context mContext;

        public UpdateTask(Context context) {
            super();
            this.mContext = context;
        }

        protected Long doInBackground(String... values) {
            long id = -1;
            GamesSQLiteHelper gamesSQLiteHelper =
                    new GamesSQLiteHelper(mContext, "DBGames", null, 1);

            SQLiteDatabase db = gamesSQLiteHelper.getWritableDatabase();
            String[] args = new String[]{values[0]};
            ContentValues contentValues = new ContentValues();
            contentValues.put("alias", values[1]);
            if (db != null) {
                id = db.update("Games", contentValues, "_id=?", args);
            }
            return id;
        }

        protected void onPostExecute(Long id) {
            if (id == -1) {
                showResult("Error inserting register!");
            } else {
                showResult("Registre inserit correctament");
                initializeAdapter();
            }
        }

    }




}
