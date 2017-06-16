package com.example.ivan.reversi_final.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.ivan.reversi_final.R;

public class GameScreen extends Activity implements ParrillaFrag.ParrillaListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);
        ((ParrillaFrag) getFragmentManager().findFragmentById(R.id.parrilla)).setParrillaListener(this);
    }


    @Override
    public void makeLog(String s) {
        LogFrag logFrag = (LogFrag) getFragmentManager().findFragmentById(R.id.log);
        if (logFrag != null && logFrag.isVisible()) {//Tablet
            logFrag.update(s);
        } else {//Smartphone
            //Nothing to do
        }
    }
}
