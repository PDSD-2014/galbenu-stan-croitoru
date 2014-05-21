package com.example.proiect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.SumPathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	public int color0 = Color.parseColor("#CDC9C9");
	public int color2 = Color.parseColor("#0066FF");
	public int color4 = Color.CYAN;
	public int color8 = Color.GREEN;
	public int color16 = Color.YELLOW;
	public int color32 = Color.MAGENTA;
	public int color64 = Color.parseColor("#CC9933");
	public int color128 = Color.parseColor("#CC9999");
	public int color256 = Color.parseColor("#FF7722");
	public int color512 = Color.parseColor("#7EB6FF");
	public int color1024 = Color.parseColor("#F6A4D5");
	public int color2048 = Color.parseColor("#551011");

	float swipe_dist = 50;
	public int slide_dist;
	public int margin;
	public int duration = 500;
	public int score = 0;
	public int score_ant = 0;
	public int nr_moves = 0;
	public float startX;
	public float startY;
	public int action = 0;
	final int[][] m = new int[4][4];
	final int[][] m_back = new int[4][4];
	final int[][] moves = new int[4][4];
	final TextView[][] t = new TextView[4][4];
	final TextView[][] marks = new TextView[4][4];
	boolean win_game = false;
	boolean end_game = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final EditText verificare = (EditText) findViewById(R.id.verificare);

		score = 0;
		nr_moves = 0;

		clear_matrix(m);
		test();
		put_random();

		int i, j;
		for (i = 0; i < 4; i++)
			for (j = 0; j < 4; j++)
				m_back[i][j] = m[i][j];

		t[0][0] = (TextView) findViewById(R.id.t00);
		t[0][1] = (TextView) findViewById(R.id.t01);
		t[0][2] = (TextView) findViewById(R.id.t02);
		t[0][3] = (TextView) findViewById(R.id.t03);
		t[1][0] = (TextView) findViewById(R.id.t10);
		t[1][1] = (TextView) findViewById(R.id.t11);
		t[1][2] = (TextView) findViewById(R.id.t12);
		t[1][3] = (TextView) findViewById(R.id.t13);
		t[2][0] = (TextView) findViewById(R.id.t20);
		t[2][1] = (TextView) findViewById(R.id.t21);
		t[2][2] = (TextView) findViewById(R.id.t22);
		t[2][3] = (TextView) findViewById(R.id.t23);
		t[3][0] = (TextView) findViewById(R.id.t30);
		t[3][1] = (TextView) findViewById(R.id.t31);
		t[3][2] = (TextView) findViewById(R.id.t32);
		t[3][3] = (TextView) findViewById(R.id.t33);

		marks[0][0] = (TextView) findViewById(R.id.m00);
		marks[0][1] = (TextView) findViewById(R.id.m01);
		marks[0][2] = (TextView) findViewById(R.id.m02);
		marks[0][3] = (TextView) findViewById(R.id.m03);
		marks[1][0] = (TextView) findViewById(R.id.m10);
		marks[1][1] = (TextView) findViewById(R.id.m11);
		marks[1][2] = (TextView) findViewById(R.id.m12);
		marks[1][3] = (TextView) findViewById(R.id.m13);
		marks[2][0] = (TextView) findViewById(R.id.m20);
		marks[2][1] = (TextView) findViewById(R.id.m21);
		marks[2][2] = (TextView) findViewById(R.id.m22);
		marks[2][3] = (TextView) findViewById(R.id.m23);
		marks[3][0] = (TextView) findViewById(R.id.m30);
		marks[3][1] = (TextView) findViewById(R.id.m31);
		marks[3][2] = (TextView) findViewById(R.id.m32);
		marks[3][3] = (TextView) findViewById(R.id.m33);

		Display display = getWindowManager().getDefaultDisplay();
		@SuppressWarnings("deprecation")
		int width = display.getWidth();
		margin = 0;
		android.view.ViewGroup.LayoutParams p = null;

		RelativeLayout.LayoutParams lpar = (android.widget.RelativeLayout.LayoutParams) t[0][0]
				.getLayoutParams();
		margin = (int) lpar.leftMargin;

		for (i = 0; i < 4; i++)
			for (j = 0; j < 4; j++) {
				p = t[i][j].getLayoutParams();
				p.width = (int) (width / 4) - 5 * margin;
				p.height = (int) (width / 4) - 5 * margin;
				p = marks[i][j].getLayoutParams();
				p.width = (int) (width / 4) - 5 * margin;
				p.height = (int) (width / 4) - 5 * margin;
			}

		slide_dist = t[0][0].getLayoutParams().width;
		update_matrix();

		Button reset, undo, exit;
		reset = (Button) findViewById(R.id.reset);

		reset.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				reset_puzzle();
			}
		});

		undo = (Button) findViewById(R.id.undo);

		undo.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				undo();
			}
		});

		exit = (Button) findViewById(R.id.exit);

		exit.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				finish();
			}
		});

		RelativeLayout rl = (RelativeLayout) this.findViewById(R.id.rl);
		rl.setOnTouchListener(new OnTouchListener() {

			public void LeftRightSwipe() {

				int i, j = 0, k, l;
				boolean modified = false;

				RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
				rl.setEnabled(false);

				verificare.setText("LR");

				// stergere matrice mutari

				clear_matrix(moves);

				// numarare patrate goale din dreapta => ? mutari

				for (i = 0; i < 4; i++)
					for (j = 0; j < 3; j++) {
						for (k = j + 1; k < 4; k++) {
							if ((m[i][k] == 0) && (m[i][j] != 0)) {
								moves[i][j]++;
								modified = true;
							}
						}
					}

				// numarare patrate de unit => ? mutari

				for (i = 0; i < 4; i++) {
					boolean empty = false;
					label1: for (j = 3; j > 0; j--) {
						if (m[i][j] == 0) {
							empty = true;
							continue;
						}
						for (k = j - 1; k >= 0; k--) {
							if (m[i][k] == 0) {
								empty = true;
								continue;
							}
							if ((m[i][k] != m[i][j]) && (m[i][k] != 0))
								break;
							if (m[i][k] == m[i][j]) {
								for (l = 0; l <= k; l++)
									if (m[i][l] != 0)
										moves[i][l]++;
								modified = true;
								if (empty)
									break label1;
								else
									j--;
								break;
							}
						}
					}
				}

				if (modified == true) {
					score_ant = score;
					for (i = 0; i < 4; i++)
						for (j = 0; j < 4; j++)
							m_back[i][j] = m[i][j];
				}

				// actualizare matrice

				for (i = 0; i < 4; i++)
					for (j = 2; j >= 0; j--) {
						if ((moves[i][j] != 0) && (m[i][j] != 0)) {
							if (m[i][j + moves[i][j]] != 0)
								score = score + m[i][j] + m[i][j + moves[i][j]];
							m[i][j + moves[i][j]] = m[i][j]
									+ m[i][j + moves[i][j]];
							m[i][j] = 0;
						}
					}

				// daca am modificat ceva, mai punem un element

				if (modified == true) {
					nr_moves++;
					TextView mv = (TextView) findViewById(R.id.nr_moves);
					mv.setText("" + nr_moves);
					put_random();

					// realizare animatie

					final TranslateAnimation[][] ta = new TranslateAnimation[4][4];

					int firsti = -1, firstj = -1;

					for (i = 0; i < 4; i++)
						for (j = 0; j < 3; j++) {
							if (moves[i][j] == 0)
								continue;
							ta[i][j] = new TranslateAnimation(0, moves[i][j]
									* slide_dist + moves[i][j] * margin, 0, 0);
							ta[i][j].setDuration(duration);
							t[i][j].startAnimation(ta[i][j]);
							if ((firsti == -1) && (firstj == -1)) {
								firsti = i;
								firstj = j;
							}
						}

					if ((firsti != -1) && (firstj != -1))
						ta[firsti][firstj]
								.setAnimationListener(new Animation.AnimationListener() {
									@Override
									public void onAnimationStart(
											Animation animation) {
									}

									@Override
									public void onAnimationEnd(
											Animation animation) {

										update_matrix();
										RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
										rl.setEnabled(true);
									}

									@Override
									public void onAnimationRepeat(
											Animation animation) {
									}
								});
				}
				if (!modified) {
					rl.setEnabled(true);
				}
			}

			public void RightLeftSwipe() {
				int i, j = 0, k, l;
				boolean modified = false;

				RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
				rl.setEnabled(false);

				verificare.setText("RL");

				// stergere matrice mutari

				clear_matrix(moves);

				// numarare patrate goale din stanga => ? mutari

				for (i = 0; i < 4; i++)
					for (j = 3; j > 0; j--) {
						for (k = j - 1; k >= 0; k--) {
							if ((m[i][k] == 0) && (m[i][j] != 0)) {
								moves[i][j]++;
								modified = true;
							}
						}
					}

				// numarare patrate de unit => ? mutari

				for (i = 0; i < 4; i++) {
					boolean empty = false;
					label1: for (j = 0; j < 3; j++) {
						if (m[i][j] == 0) {
							empty = true;
							continue;
						}
						for (k = j + 1; k <= 3; k++) {
							if (m[i][k] == 0) {
								empty = true;
								continue;
							}
							if ((m[i][k] != m[i][j]) && (m[i][k] != 0))
								break;
							if (m[i][k] == m[i][j]) {
								for (l = 3; l >= k; l--)
									if (m[i][l] != 0)
										moves[i][l]++;
								modified = true;
								if (empty)
									break label1;
								else
									j++;
								break;
							}
						}
					}
				}

				if (modified == true) {
					score_ant = score;
					for (i = 0; i < 4; i++)
						for (j = 0; j < 4; j++)
							m_back[i][j] = m[i][j];
				}

				// actualizare matrice

				for (i = 0; i < 4; i++)
					for (j = 1; j < 4; j++) {
						if ((moves[i][j] != 0) && (m[i][j] != 0)) {
							if (m[i][j - moves[i][j]] != 0)
								score = score + m[i][j] + m[i][j - moves[i][j]];
							m[i][j - moves[i][j]] = m[i][j]
									+ m[i][j - moves[i][j]];
							m[i][j] = 0;
						}
					}

				// daca am modificat ceva, mai punem un element

				if (modified == true) {
					nr_moves++;
					TextView mv = (TextView) findViewById(R.id.nr_moves);
					mv.setText("" + nr_moves);
					put_random();

					// realizare animatie

					TranslateAnimation[][] ta = new TranslateAnimation[4][4];

					int firsti = -1, firstj = -1;

					for (i = 0; i < 4; i++)
						for (j = 3; j > 0; j--) {
							if (moves[i][j] == 0)
								continue;
							ta[i][j] = new TranslateAnimation(0, -moves[i][j]
									* slide_dist - moves[i][j] * margin, 0, 0);
							ta[i][j].setDuration(duration);
							t[i][j].startAnimation(ta[i][j]);
							if ((firsti == -1) && (firstj == -1)) {
								firsti = i;
								firstj = j;
							}
						}

					if ((firsti != -1) && (firstj != -1))
						ta[firsti][firstj]
								.setAnimationListener(new Animation.AnimationListener() {
									@Override
									public void onAnimationStart(
											Animation animation) {
									}

									@Override
									public void onAnimationEnd(
											Animation animation) {
										update_matrix();
										RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
										rl.setEnabled(true);
									}

									@Override
									public void onAnimationRepeat(
											Animation animation) {
									}
								});
				}
				if (!modified) {
					rl.setEnabled(true);
				}
			}

			public void UpDownSwipe() {
				int i, j = 0, k, l;
				boolean modified = false;

				RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
				rl.setEnabled(false);

				verificare.setText("UD");

				// stergere matrice mutari

				clear_matrix(moves);

				// numarare patrate goale din dreapta => ? mutari

				for (i = 0; i < 3; i++)
					for (j = 0; j < 4; j++) {
						for (k = i + 1; k < 4; k++) {
							if ((m[k][j] == 0) && (m[i][j] != 0)) {
								moves[i][j]++;
								modified = true;
							}
						}
					}

				// numarare patrate de unit => ? mutari

				for (j = 0; j < 4; j++) {
					boolean empty = false;
					label1: for (i = 3; i > 0; i--) {
						if (m[i][j] == 0) {
							empty = true;
							continue;
						}
						for (k = i - 1; k >= 0; k--) {
							if (m[k][j] == 0) {
								empty = true;
								continue;
							}
							if ((m[k][j] != m[i][j]) && (m[k][j] != 0))
								break;
							if (m[k][j] == m[i][j]) {
								for (l = 0; l <= k; l++)
									if (m[l][j] != 0)
										moves[l][j]++;
								modified = true;
								if (empty)
									break label1;
								else
									i--;
								break;
							}
						}
					}
				}

				if (modified == true) {
					score_ant = score;
					for (i = 0; i < 4; i++)
						for (j = 0; j < 4; j++)
							m_back[i][j] = m[i][j];
				}

				// actualizare matrice

				for (i = 2; i >= 0; i--)
					for (j = 0; j < 4; j++) {
						if ((moves[i][j] != 0) && (m[i][j] != 0)) {
							if (m[i + moves[i][j]][j] != 0)
								score = score + m[i][j] + m[i + moves[i][j]][j];
							m[i + moves[i][j]][j] = m[i][j]
									+ m[i + moves[i][j]][j];
							m[i][j] = 0;
						}
					}

				// daca am modificat ceva, mai punem un element

				if (modified == true) {
					nr_moves++;
					TextView mv = (TextView) findViewById(R.id.nr_moves);
					mv.setText("" + nr_moves);
					put_random();

					// realizare animatie

					final TranslateAnimation[][] ta = new TranslateAnimation[4][4];

					int firsti = -1, firstj = -1;

					for (i = 0; i < 3; i++)
						for (j = 0; j < 4; j++) {
							if (moves[i][j] == 0)
								continue;
							ta[i][j] = new TranslateAnimation(0, 0, 0,
									moves[i][j] * slide_dist + moves[i][j]
											* margin);
							ta[i][j].setDuration(duration);
							t[i][j].startAnimation(ta[i][j]);
							if ((firsti == -1) && (firstj == -1)) {
								firsti = i;
								firstj = j;
							}
						}

					if ((firsti != -1) && (firstj != -1))
						ta[firsti][firstj]
								.setAnimationListener(new Animation.AnimationListener() {
									@Override
									public void onAnimationStart(
											Animation animation) {
									}

									@Override
									public void onAnimationEnd(
											Animation animation) {

										update_matrix();
										RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
										rl.setEnabled(true);
									}

									@Override
									public void onAnimationRepeat(
											Animation animation) {
									}
								});
				}
				if (!modified) {
					rl.setEnabled(true);
				}
			}

			public void DownUpSwipe() {
				int i, j = 0, k, l;
				boolean modified = false;

				RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
				rl.setEnabled(false);

				verificare.setText("DU");

				// stergere matrice mutari

				clear_matrix(moves);

				// numarare patrate goale din stanga => ? mutari

				for (i = 3; i > 0; i--)
					for (j = 0; j < 4; j++) {
						for (k = i - 1; k >= 0; k--) {
							if ((m[k][j] == 0) && (m[i][j] != 0)) {
								moves[i][j]++;
								modified = true;
							}
						}
					}

				// numarare patrate de unit => ? mutari

				for (j = 0; j < 4; j++) {
					boolean empty = false;
					label1: for (i = 0; i < 3; i++) {
						if (m[i][j] == 0) {
							empty = true;
							continue;
						}
						for (k = i + 1; k <= 3; k++) {
							if (m[k][j] == 0) {
								empty = true;
								continue;
							}
							if ((m[k][j] != m[i][j]) && (m[k][j] != 0))
								break;
							if (m[k][j] == m[i][j]) {
								for (l = 3; l >= k; l--)
									if (m[l][j] != 0)
										moves[l][j]++;
								modified = true;
								if (empty)
									break label1;
								else
									i++;
								break;
							}
						}
					}
				}

				if (modified == true) {
					score_ant = score;
					for (i = 0; i < 4; i++)
						for (j = 0; j < 4; j++)
							m_back[i][j] = m[i][j];
				}

				// actualizare matrice

				for (i = 1; i < 4; i++)
					for (j = 0; j < 4; j++) {
						if ((moves[i][j] != 0) && (m[i][j] != 0)) {
							if (m[i - moves[i][j]][j] != 0)
								score = score + m[i][j] + m[i - moves[i][j]][j];
							m[i - moves[i][j]][j] = m[i][j]
									+ m[i - moves[i][j]][j];
							m[i][j] = 0;
						}
					}

				// daca am modificat ceva, mai punem un element

				if (modified == true) {
					nr_moves++;
					TextView mv = (TextView) findViewById(R.id.nr_moves);
					mv.setText("" + nr_moves);
					put_random();

					// realizare animatie

					TranslateAnimation[][] ta = new TranslateAnimation[4][4];

					int firsti = -1, firstj = -1;

					for (i = 3; i > 0; i--)
						for (j = 0; j < 4; j++) {
							if (moves[i][j] == 0)
								continue;
							ta[i][j] = new TranslateAnimation(0, 0, 0,
									-moves[i][j] * slide_dist - moves[i][j]
											* margin);
							ta[i][j].setDuration(duration);
							t[i][j].startAnimation(ta[i][j]);
							if ((firsti == -1) && (firstj == -1)) {
								firsti = i;
								firstj = j;
							}
						}

					if ((firsti != -1) && (firstj != -1))
						ta[firsti][firstj]
								.setAnimationListener(new Animation.AnimationListener() {
									@Override
									public void onAnimationStart(
											Animation animation) {
									}

									@Override
									public void onAnimationEnd(
											Animation animation) {
										update_matrix();
										RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
										rl.setEnabled(true);
									}

									@Override
									public void onAnimationRepeat(
											Animation animation) {
									}
								});
				}
				if (!modified) {
					rl.setEnabled(true);
				}
			}

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					startX = event.getX();
					startY = event.getY();
					action = 0;
					break;
				}

				case MotionEvent.ACTION_UP: {
					int endX = (int) event.getX();
					int endY = (int) event.getY();

					if (Math.abs(endX - startX) > Math.abs(endY - startY)) {
						if (endX - startX > swipe_dist)
							action = 1;
						else if (startX - endX > swipe_dist)
							action = 2;
					} else if (Math.abs(endY - startY) > Math
							.abs(endX - startX)) {
						if (endY - startY > swipe_dist)
							action = 3;
						else if (startY - endY > swipe_dist)
							action = 4;
					} else
						action = 0;
					break;
				}
				default:
					break;
				}
				switch (action) {
				case 1: {
					LeftRightSwipe();
					break;
				}
				case 2: {
					RightLeftSwipe();
					break;
				}
				case 3: {
					UpDownSwipe();
					break;
				}
				case 4: {
					DownUpSwipe();
					break;
				}
				}

				return true;
			}
		});
	}

	public void clear_matrix(int[][] m) {
		int i, j;
		for (i = 0; i < 4; i++)
			for (j = 0; j < 4; j++)
				m[i][j] = 0;
	}

	public int empty_squares() {
		int i, j, nr = 0;
		for (i = 0; i < 4; i++)
			for (j = 0; j < 4; j++)
				if (m[i][j] == 0)
					nr++;
		return nr;
	}

	public void test() {
		m[0][0] = 2;
		m[0][1] = 4;
		m[0][2] = 8;
		m[0][3] = 16;
		m[1][0] = 16;
		m[1][1] = 8;
		m[1][2] = 4;
		m[1][3] = 2;
		m[2][0] = 128;
		m[2][1] = 512;
		m[2][2] = 8;
		m[2][3] = 512;
		m[3][0] = 0;
		m[3][1] = 0;
		m[3][2] = 1024;
		m[3][3] = 0;
	}

	public boolean game_over() {
		int i, j;

		boolean ok = false;
		for (i = 0; i < 4; i++)
			for (j = 0; j < 4; j++)
				if (m[i][j] == 0) {
					ok = true;
					break;
				}

		if (empty_squares() == 0) {
			for (i = 0; i <= 3; i++)
				for (j = 0; j <= 3; j++) {
					if ((i - 1) >= 0)
						if ((m[i - 1][j] == m[i][j]) && (m[i][j] != 0))
							return false;
					if ((j - 1) >= 0)
						if ((m[i][j - 1] == m[i][j]) && (m[i][j] != 0))
							return false;
					if ((i + 1) <= 3)
						if ((m[i + 1][j] == m[i][j]) && (m[i][j] != 0))
							return false;
					if ((j + 1) <= 3)
						if ((m[i][j + 1] == m[i][j]) && (m[i][j] != 0))
							return false;
				}

			return true;
		}
		return false;
	}

	public void put_random() {

		int nr = 0, i, j, value, rand;

		nr = empty_squares();
		
		Random random1 = new Random();
		Random random2 = new Random();

		rand = random1.nextInt(1000);

		if (rand < 900)
			value = 2;
		else
			value = 4;

		rand = random2.nextInt(nr);

		int ct = 0;
		boolean added = false;

		for (i = 0; i < 4; i++) {
			for (j = 0; j < 4; j++) {
				if (m[i][j] == 0) {
					if (ct == rand) {
						m[i][j] = value;
						added = true;
						break;
					} else
						ct++;
				}
			}
			if (added == true)
				break;
		}
	}

	public void update_matrix() {

		int i, j;
		for (i = 0; i < 4; i++)
			for (j = 0; j < 4; j++) {
				switch (m[i][j]) {
				case 0: {
					t[i][j].setVisibility(View.INVISIBLE);
					t[i][j].setBackgroundColor(color0);
					t[i][j].setText("");
					break;
				}
				case 2: {
					if (t[i][j].getVisibility() == View.INVISIBLE)
						t[i][j].setVisibility(View.VISIBLE);
					t[i][j].setBackgroundColor(color2);
					t[i][j].setText("" + m[i][j]);
					break;
				}
				case 4: {
					if (t[i][j].getVisibility() == View.INVISIBLE)
						t[i][j].setVisibility(View.VISIBLE);
					t[i][j].setBackgroundColor(color4);
					t[i][j].setText("" + m[i][j]);
					break;
				}
				case 8: {
					if (t[i][j].getVisibility() == View.INVISIBLE)
						t[i][j].setVisibility(View.VISIBLE);
					t[i][j].setBackgroundColor(color8);
					t[i][j].setText("" + m[i][j]);
					break;
				}
				case 16: {
					if (t[i][j].getVisibility() == View.INVISIBLE)
						t[i][j].setVisibility(View.VISIBLE);
					t[i][j].setBackgroundColor(color16);
					t[i][j].setText("" + m[i][j]);
					break;
				}
				case 32: {
					if (t[i][j].getVisibility() == View.INVISIBLE)
						t[i][j].setVisibility(View.VISIBLE);
					t[i][j].setBackgroundColor(color32);
					t[i][j].setText("" + m[i][j]);
					break;
				}
				case 64: {
					if (t[i][j].getVisibility() == View.INVISIBLE)
						t[i][j].setVisibility(View.VISIBLE);
					t[i][j].setBackgroundColor(color64);
					t[i][j].setText("" + m[i][j]);
					break;
				}
				case 128: {
					if (t[i][j].getVisibility() == View.INVISIBLE)
						t[i][j].setVisibility(View.VISIBLE);
					t[i][j].setBackgroundColor(color128);
					t[i][j].setText("" + m[i][j]);
					break;
				}
				case 256: {
					if (t[i][j].getVisibility() == View.INVISIBLE)
						t[i][j].setVisibility(View.VISIBLE);
					t[i][j].setBackgroundColor(color256);
					t[i][j].setText("" + m[i][j]);
					break;
				}
				case 512: {
					if (t[i][j].getVisibility() == View.INVISIBLE)
						t[i][j].setVisibility(View.VISIBLE);
					t[i][j].setBackgroundColor(color512);
					t[i][j].setText("" + m[i][j]);
					break;
				}
				case 1024: {
					if (t[i][j].getVisibility() == View.INVISIBLE)
						t[i][j].setVisibility(View.VISIBLE);
					t[i][j].setBackgroundColor(color1024);
					t[i][j].setText("" + m[i][j]);
					break;
				}
				case 2048: {
					if (t[i][j].getVisibility() == View.INVISIBLE)
						t[i][j].setVisibility(View.VISIBLE);
					t[i][j].setBackgroundColor(color2048);
					t[i][j].setTextColor(Color.YELLOW);
					t[i][j].setText("" + m[i][j]);
					win_game = true;
					break;
				}
				}

			}
		TextView sc = (TextView) findViewById(R.id.score);
		sc.setText("" + score);
		TextView mv = (TextView) findViewById(R.id.nr_moves);
		mv.setText("" + nr_moves);

		if (game_over()) {

			for (i = 0; i <= 3; i++) {
				for (j = 0; j <= 3; j++)
					System.out.print(m[i][j] + " ");

				System.out.println();
			}

			Intent myIntent = new Intent(this, Submit.class);
			startActivity(myIntent);
			Global.SCORE = score;
			Global.MOVES = nr_moves;
		}
	}

	public void undo() {
		int i, j;
		boolean changed = false;

		score = score_ant;

		for (i = 0; i < 4; i++)
			for (j = 0; j < 4; j++) {
				if (m[i][j] != m_back[i][j])
					changed = true;
				m[i][j] = m_back[i][j];
			}

		if (changed == true)
			nr_moves--;

		update_matrix();

		TextView sc = (TextView) findViewById(R.id.score);
		sc.setText("" + score);

		TextView mv = (TextView) findViewById(R.id.nr_moves);
		mv.setText("" + nr_moves);

	}

	public void reset_puzzle() {
		score = 0;
		score_ant = 0;
		nr_moves = 0;
		win_game = false;
		int i, j;

		clear_matrix(m);		

		put_random();
		put_random();

		update_matrix();

		for (i = 0; i < 4; i++)
			for (j = 0; j < 4; j++) {
				m_back[i][j] = m[i][j];
			}

		TextView sc = (TextView) findViewById(R.id.score);
		sc.setText("" + score);

		TextView mv = (TextView) findViewById(R.id.nr_moves);
		mv.setText("" + nr_moves);

		clear_matrix(moves);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
