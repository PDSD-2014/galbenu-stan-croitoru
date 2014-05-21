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
import android.widget.TextView;

public class Highscore extends Activity {
	
	TextView highscore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.highscore);
		
		
		highscore = (TextView) findViewById(R.id.highscore);
		get_highscores();
		
	}

	public void get_highscores() {
		Log.i("submit","submit");
		new Thread(new Runnable() {
			public void run() {
				Socket socket = null;
				DataOutputStream dataOutputStream = null;
				DataInputStream dataInputStream = null;

				try {
					socket = new Socket("192.168.56.1", 8888);
					dataOutputStream = new DataOutputStream(socket.getOutputStream());
					dataInputStream = new DataInputStream(socket.getInputStream());
					
					dataOutputStream.writeUTF("get_highscores");
					String s = new String(dataInputStream.readUTF().replaceAll("`"," "));
					highscore.setText(s);
					
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
		getMenuInflater().inflate(R.menu.highscore, menu);
		return true;
	}

}
