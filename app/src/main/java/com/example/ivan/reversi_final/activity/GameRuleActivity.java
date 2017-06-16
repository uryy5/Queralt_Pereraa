package com.example.ivan.reversi_final.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.ivan.reversi_final.R;

public class GameRuleActivity extends Activity {

	private Button backingmain;
	private Animation trans;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.game_rule);
		TextView textView = (TextView)findViewById(R.id.rule);
		textView.setText(getResources().getString(R.string.game_rule));

		trans = AnimationUtils.loadAnimation(this, R.anim.alpha);
		textView.startAnimation(trans);

		backingmain = (Button)findViewById(R.id.buttonback);
		backingmain.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i =new Intent(GameRuleActivity.this, MainActivity.class);
				startActivity(i);
				finish();
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			GameRuleActivity.this.finish();
			overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
