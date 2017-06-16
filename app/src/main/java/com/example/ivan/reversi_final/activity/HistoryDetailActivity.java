package com.example.ivan.reversi_final.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.ivan.reversi_final.R;


public class HistoryDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        HistoryDetailFragment detailFragment = (HistoryDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.DetailFrag);
        int id = this.getIntent().getIntExtra("id", 0);
        detailFragment.show(id);
    }

}
