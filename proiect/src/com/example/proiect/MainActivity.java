package com.example.proiect;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button button;
        button = (Button) findViewById(R.id.button1);
        
		button.setOnClickListener(new OnClickListener() {
 
			public void onClick(View arg0) {
				TranslateAnimation ta = new TranslateAnimation(-100, -100, 0, 0);
				ta.setDuration(5000);
				TextView t = (TextView)findViewById(R.id.t0);
				t.startAnimation(ta);
			}
 
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
