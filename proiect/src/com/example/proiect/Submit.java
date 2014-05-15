package com.example.proiect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Submit extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.submit);
	}

	protected void onStart(){
		super.onStart();
		Button submit_submit, submit_cancel;
		final EditText submit_name;
		submit_name = (EditText) findViewById(R.id.sumit_name);
		
		submit_submit = (Button) findViewById(R.id.submit_submit);
		submit_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(submit_name.getText().length() == 0){
					Toast.makeText(Submit.this,"Complete name field",Toast.LENGTH_LONG).show();
				}else{
					Global.NAME = submit_name.getText().toString();
					submit_score();
					finish();
				}
			}
		});
		
		submit_cancel = (Button) findViewById(R.id.submit_cancel);
		submit_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	
	public void submit_score() {
		Log.i("submit","submit");
		new Thread(new Runnable() {
			public void run() {
				Socket socket = null;
				DataOutputStream dataOutputStream = null;
				DataInputStream dataInputStream = null;

				try {
					socket = new Socket("192.168.56.1", 8889);
					dataOutputStream = new DataOutputStream(
							socket.getOutputStream());
					dataInputStream = new DataInputStream(
							socket.getInputStream());
					dataOutputStream.writeUTF(Global.NAME+" "+Global.SCORE+" "+Global.MOVES);
					
					//Log.i("submit",dataInputStream.readUTF());
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					if (dataOutputStream != null) {
						try {
							dataOutputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					if (dataInputStream != null) {
						try {
							dataInputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

		}).start();
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.submit, menu);
		return true;
	}

}
