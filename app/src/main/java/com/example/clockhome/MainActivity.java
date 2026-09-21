package com.example.clockhome;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.EditText;
import android.app.AlertDialog;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private final Handler handler = new Handler();
    private SharedPreferences prefs;
    private TextView timeText, secondsText, dateText, ampmText;
    private ImageButton settingsButton;
    private LinearLayout settingsPanel;
    private Runnable hideGear;

    private static final String PREFS = "clock_settings";
    private static final int GREEN = Color.rgb(0, 210, 110);
    private static final int WHITE = Color.WHITE;
    private static final int RED = Color.rgb(240, 70, 70);
    private static final int YELLOW = Color.rgb(255, 200, 0);
    private static final int BLUE = Color.rgb(70, 150, 255);
    private static final int CYAN = Color.rgb(0, 220, 220);
    private static final int ORANGE = Color.rgb(255, 145, 40);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideSystemUi();

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        buildUi();
        updateClock();
        showGearTemporarily();
    }

    private void buildUi() {
        final FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(getBackgroundColor());

        LinearLayout clock = new LinearLayout(this);
        clock.setOrientation(LinearLayout.VERTICAL);
        clock.setGravity(Gravity.CENTER);
        clock.setPadding(24, 10, 24, 8);

        timeText = makeText(150, Typeface.BOLD, getTimeColor());
        secondsText = makeText(42, Typeface.BOLD, getSecondsColor());
        ampmText = makeText(38, Typeface.BOLD, getSecondsColor());
        dateText = makeText(34, Typeface.NORMAL, getDateColor());

        LinearLayout timeRow = new LinearLayout(this);
        timeRow.setGravity(Gravity.CENTER);
        timeRow.setOrientation(LinearLayout.HORIZONTAL);
        timeRow.addView(timeText, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        LinearLayout right = new LinearLayout(this);
        right.setOrientation(LinearLayout.VERTICAL);
        right.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        right.addView(ampmText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        right.addView(secondsText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        timeRow.addView(right, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        clock.addView(timeRow, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 6f));
        clock.addView(dateText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 2f));
        root.addView(clock, new FrameLayout.LayoutParams(-1, -1));

        settingsButton = new ImageButton(this);
        settingsButton.setImageResource(com.example.clockhome.R.drawable.ic_settings);
        settingsButton.setBackgroundColor(Color.TRANSPARENT);
        settingsButton.setPadding(12, 12, 12, 12);
        settingsButton.setContentDescription("Settings");
        settingsButton.setOnClickListener(v -> openSettings());
        FrameLayout.LayoutParams gearLp = new FrameLayout.LayoutParams(72, 72, Gravity.RIGHT | Gravity.BOTTOM);
        gearLp.setMargins(0, 0, 18, 18);
        root.addView(settingsButton, gearLp);

        settingsPanel = buildSettingsPanel();
        settingsPanel.setVisibility(View.GONE);
        FrameLayout.LayoutParams panelLp = new FrameLayout.LayoutParams(-1, -1);
        root.addView(settingsPanel, panelLp);

        root.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showGearTemporarily();
            }
            return false;
        });
        setContentView(root);
    }

    private LinearLayout buildSettingsPanel() {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(24, 18, 24, 18);
        panel.setBackgroundColor(Color.argb(245, 20, 20, 20));

        TextView title = makeText(25, Typeface.BOLD, WHITE);
        title.setText("CLOCK SETTINGS");
        title.setGravity(Gravity.CENTER);
        panel.addView(title, new LinearLayout.LayoutParams(-1, 55));

        ScrollView scroll = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        content.addView(label("TIME FORMAT"));
        LinearLayout fmt = row();
        fmt.addView(button("24 HOUR", v -> setTimeMode(false)));
        fmt.addView(button("12 HOUR AM/PM", v -> setTimeMode(true)));
        content.addView(fmt);

        content.addView(label("SECONDS"));
        LinearLayout sec = row();
        sec.addView(button("SHOW", v -> { prefs.edit().putBoolean("seconds", true).apply(); refreshColors(); }));
        sec.addView(button("HIDE", v -> { prefs.edit().putBoolean("seconds", false).apply(); refreshColors(); }));
        content.addView(sec);

        content.addView(label("DATE FORMAT"));
        LinearLayout d1 = row();
        d1.addView(button("08-12-2026 SUN", v -> setDateFormat("dd-MM-yyyy EEE")));
        d1.addView(button("Monday, 08 December 2026", v -> setDateFormat("EEEE, dd MMMM yyyy")));
        content.addView(d1);
        LinearLayout d2 = row();
        d2.addView(button("08 Dec 2026", v -> setDateFormat("dd MMM yyyy")));
        d2.addView(button("2026-12-08", v -> setDateFormat("yyyy-MM-dd")));
        content.addView(d2);

        content.addView(label("TIME COLOR"));
        content.addView(colorRow("timeColor"));
        content.addView(label("SECONDS / AM-PM COLOR"));
        content.addView(colorRow("secondsColor"));
        content.addView(label("DATE COLOR"));
        content.addView(colorRow("dateColor"));
        content.addView(label("BACKGROUND"));
        content.addView(bgColorRow());

        Button close = button("CLOSE SETTINGS", v -> closeSettings());
        content.addView(close);
        scroll.addView(content);
        panel.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1f));
        return panel;
    }

    private LinearLayout colorRow(String key) {
        LinearLayout row = row();
        int[] colors = {GREEN, RED, WHITE, YELLOW, BLUE, CYAN, ORANGE};
        String[] names = {"GREEN", "RED", "WHITE", "YELLOW", "BLUE", "CYAN", "ORANGE"};
        for (int i = 0; i < colors.length; i++) {
            final int c = colors[i];
            row.addView(button(names[i], v -> { prefs.edit().putInt(key, c).apply(); refreshColors(); }));
        }
        row.addView(button("CUSTOM HEX", v -> showHexDialog(key)));
        return row;
    }

    private LinearLayout bgColorRow() {
        LinearLayout row = row();
        int[] colors = {Color.BLACK, Color.rgb(15,15,15), Color.rgb(35,35,35), Color.rgb(0,30,20)};
        String[] names = {"BLACK", "DARK", "GRAY", "DARK GREEN"};
        for (int i = 0; i < colors.length; i++) {
            final int c = colors[i];
            row.addView(button(names[i], v -> { prefs.edit().putInt("background", c).apply(); refreshColors(); }));
        }
        row.addView(button("CUSTOM HEX", v -> showHexDialog("background")));
        return row;
    }

    private Button button(String text, View.OnClickListener listener) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(11);
        b.setOnClickListener(listener);
        return b;
    }

    private TextView label(String text) {
        TextView t = makeText(15, Typeface.BOLD, Color.LTGRAY);
        t.setText(text);
        t.setPadding(6, 12, 6, 3);
        return t;
    }

    private LinearLayout row() {
        LinearLayout r = new LinearLayout(this);
        r.setOrientation(LinearLayout.HORIZONTAL);
        r.setGravity(Gravity.CENTER_VERTICAL);
        return r;
    }

    private TextView makeText(float size, int style, int color) {
        TextView t = new TextView(this);
        t.setTextSize(size);
        t.setTypeface(Typeface.create("sans-serif", style));
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        return t;
    }

    private void showHexDialog(String key) {
        final EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setHint("#00D26A");
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.GRAY);
        input.setText(String.format("#%06X", (0xFFFFFF & prefs.getInt(key, GREEN))));
        int pad = 30;
        input.setPadding(pad, pad / 2, pad, pad / 2);
        new AlertDialog.Builder(this)
                .setTitle("Custom color")
                .setMessage("Enter HEX color, for example #00FF66")
                .setView(input)
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("APPLY", (dialog, which) -> {
                    try {
                        String value = input.getText().toString().trim();
                        if (!value.startsWith("#")) value = "#" + value;
                        int color = Color.parseColor(value);
                        prefs.edit().putInt(key, color).apply();
                        refreshColors();
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(this, "Invalid HEX color", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void setTimeMode(boolean twelve) {
        prefs.edit().putBoolean("twelve", twelve).apply();
        refreshColors();
    }

    private void setDateFormat(String format) {
        prefs.edit().putString("dateFormat", format).apply();
        refreshColors();
    }

    private void updateClock() {
        Date now = new Date();
        boolean twelve = prefs.getBoolean("twelve", false);
        boolean showSeconds = prefs.getBoolean("seconds", true);
        String timePattern = twelve ? "hh:mm" : "HH:mm";
        timeText.setText(new SimpleDateFormat(timePattern, Locale.getDefault()).format(now));
        secondsText.setText(showSeconds ? new SimpleDateFormat(":ss", Locale.getDefault()).format(now) : "");
        ampmText.setText(twelve ? new SimpleDateFormat("a", Locale.ENGLISH).format(now) : "");
        String datePattern = prefs.getString("dateFormat", "dd-MM-yyyy EEE");
        dateText.setText(new SimpleDateFormat(datePattern, Locale.ENGLISH).format(now));
        handler.postDelayed(this::updateClock, 500);
    }

    private void refreshColors() {
        timeText.setTextColor(getTimeColor());
        secondsText.setTextColor(getSecondsColor());
        ampmText.setTextColor(getSecondsColor());
        dateText.setTextColor(getDateColor());
        getWindow().getDecorView().setBackgroundColor(getBackgroundColor());
        if (settingsPanel != null && settingsPanel.getParent() != null) settingsPanel.setBackgroundColor(Color.argb(245, 20,20,20));
    }

    private int getTimeColor() { return prefs.getInt("timeColor", GREEN); }
    private int getSecondsColor() { return prefs.getInt("secondsColor", GREEN); }
    private int getDateColor() { return prefs.getInt("dateColor", GREEN); }
    private int getBackgroundColor() { return prefs.getInt("background", Color.BLACK); }

    private void openSettings() {
        settingsPanel.setVisibility(View.VISIBLE);
        settingsButton.setVisibility(View.GONE);
    }

    private void closeSettings() {
        settingsPanel.setVisibility(View.GONE);
        showGearTemporarily();
    }

    private void showGearTemporarily() {
        if (settingsPanel != null && settingsPanel.getVisibility() == View.VISIBLE) return;
        settingsButton.setVisibility(View.VISIBLE);
        if (hideGear != null) handler.removeCallbacks(hideGear);
        hideGear = () -> settingsButton.setVisibility(View.GONE);
        handler.postDelayed(hideGear, 15000);
    }

    private void hideSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    @Override protected void onResume() {
        super.onResume();
        hideSystemUi();
        showGearTemporarily();
    }

    @Override protected void onPause() {
        super.onPause();
        if (hideGear != null) handler.removeCallbacks(hideGear);
        handler.removeCallbacksAndMessages(null);
    }
}
