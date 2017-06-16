package com.example.ivan.reversi_final.activity;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ivan.reversi_final.R;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class HistoryDetailFragment extends Fragment {

    private Activity mListener;
    private ArrayList<TextView> textViews;
    private int id = 0;


    public HistoryDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int[] to = new int[]{R.id.dtl_name, R.id.dtl_date, R.id.dtl_num_cells,
                R.id.dtl_flag_time, R.id.dtl_white, R.id.dtl_black, R.id.dtl_result,
                R.id.dtl_time_total};
        textViews = new ArrayList<>();

        for (int aTo : to) {
            textViews.add((TextView) getView().findViewById(aTo));
        }
        show(id);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void show(int id) {
        this.id = id;
        if (textViews != null) {
            id++;
            GamesSQLiteHelper gamesSQLiteHelper = new GamesSQLiteHelper(mListener, "DBGames", null, 1);
            SQLiteDatabase db = gamesSQLiteHelper.getWritableDatabase();
            String[] args = new String[]{String.valueOf(id)};
            Cursor c = db.rawQuery("SELECT * FROM Games WHERE _id=?", args);
            if (c.moveToFirst()) {
                for (int i = 0; i < textViews.size(); i++) {
                    textViews.get(i).setText(c.getString(i + 1));
                }
                showResult("Consulta realitzada correctament");
            } else {
                showResult("La consulta no conte resultats");
            }
            c.close();
            db.close();
        }
    }

    private void showResult(CharSequence text) {
        Toast toast = Toast.makeText(mListener, text, Toast.LENGTH_SHORT);
        toast.show();
    }

}
