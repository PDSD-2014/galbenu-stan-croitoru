package com.example.proiect;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Meniu extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meniu);

		Button meniu_sp, meniu_highscore, meniu_exit;
		meniu_sp = (Button) findViewById(R.id.meniu_sp);
		meniu_sp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(Meniu.this, MainActivity.class);
				startActivity(myIntent);
			}
		});

		meniu_highscore = (Button) findViewById(R.id.meniu_hs);
		meniu_highscore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(Meniu.this, Highscore.class);
				startActivity(myIntent);
			}
		});

		meniu_exit = (Button) findViewById(R.id.meniu_exit);
		meniu_exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.meniu, menu);
		return true;
	}

}
