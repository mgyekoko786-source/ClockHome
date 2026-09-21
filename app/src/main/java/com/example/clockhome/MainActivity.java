package com.example.clockhome;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private final Handler handler = new Handler();
    private TextView timeText, dateText, secondsText;
    private final SimpleDateFormat time24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat seconds = new SimpleDateFormat("ss", Locale.getDefault());
    private final SimpleDateFormat date = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.ENGLISH);

    private final Runnable clockTick = new Runnable() {
        @Override public void run() {
            Date now = new Date();
            timeText.setText(time24.format(now));
            secondsText.setText(seconds.format(now));
            dateText.setText(date.format(now));
            handler.postDelayed(this, 250);
        }
    };

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideSystemUi();
        buildUi();
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(24, 16, 24, 16);
        root.setBackgroundColor(android.graphics.Color.BLACK);

        timeText = new TextView(this);
        timeText.setTextColor(android.graphics.Color.WHITE);
        timeText.setTextSize(96);
        timeText.setGravity(Gravity.CENTER);
        timeText.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD));

        secondsText = new TextView(this);
        secondsText.setTextColor(android.graphics.Color.rgb(255,193,7));
        secondsText.setTextSize(30);
        secondsText.setGravity(Gravity.CENTER);

        dateText = new TextView(this);
        dateText.setTextColor(android.graphics.Color.LTGRAY);
        dateText.setTextSize(20);
        dateText.setGravity(Gravity.CENTER);

        root.addView(timeText, new LinearLayout.LayoutParams(-1, 0, 4f));
        root.addView(secondsText, new LinearLayout.LayoutParams(-1, 0, 1f));
        root.addView(dateText, new LinearLayout.LayoutParams(-1, 0, 1f));
        setContentView(root);
    }

    private void hideSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    @Override protected void onResume() { super.onResume(); hideSystemUi(); handler.post(clockTick); }
    @Override protected void onPause() { super.onPause(); handler.removeCallbacks(clockTick); }
}

