package com.example.ivan.reversi_final.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.ivan.reversi_final.R;


public class ConfigureActivity extends Activity {

	private Button play;
	private EditText text_name;
	private RadioButton gridsize1, gridsize2, gridsize3;
	private CheckBox timechecked;
	private TextView label_alias;
    private TextView label_gridsize;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configuration_main);
		label_alias = (TextView)findViewById(R.id.textView2);
        label_gridsize = (TextView) findViewById(R.id.textView3);
		text_name = (EditText) findViewById(R.id.name_text);
		gridsize1 = (RadioButton) findViewById(R.id.radioButton);
		gridsize2 = (RadioButton) findViewById(R.id.radioButton1);
		gridsize3 = (RadioButton) findViewById(R.id.radioButton2);
		timechecked = (CheckBox)findViewById(R.id.checkBox);
		play = (Button)findViewById(R.id.playButton);
		play.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				String valor1 = gridsize1.getText().toString();
				String valor2 = gridsize2.getText().toString();
				String valor3 = gridsize3.getText().toString();

				Bundle bundle = new Bundle();

				bundle.putString("Name", text_name.getText().toString());
				if (gridsize1.isChecked()){
					bundle.putInt("GridSize", Integer.parseInt(valor1));
				}
				if(gridsize2.isChecked()){
					bundle.putInt("GridSize", Integer.parseInt(valor2));

				}
				if(gridsize3.isChecked()){
					bundle.putInt("GridSize", Integer.parseInt(valor3));

				}

                if(!gridsize1.isChecked() && !gridsize2.isChecked() && !gridsize3.isChecked()) {
                    label_gridsize.setTextColor(Color.RED);
                }

				if(!text_name.getText().toString().isEmpty() && gridsize1.isChecked() || gridsize2.isChecked() || gridsize3.isChecked()) {
					bundle.putBoolean("CheckTime", timechecked.isChecked());
					bundle.putInt("VS", 0);
					bundle.putInt("GAME_MODE", 0);
					Intent intent = new Intent(ConfigureActivity.this, GameScreen.class);
					intent.putExtras(bundle);
					startActivity(intent);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
				else {
					label_alias.setTextColor(Color.RED);
				}
			}
		});
	}
}
