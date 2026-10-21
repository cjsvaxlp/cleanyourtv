package com.gathin.tvhome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/** Remote-friendly settings owned by the launcher; system settings stay one row away. */
public final class LauncherSettingsActivity extends Activity {
    private SharedPreferences choices;
    private final TextView[] columnChoices = new TextView[4];
    private TextView animationsChoice;
    private TextView advancedChoice;
    private TextView followThemeChoice;
    private int columns;
    private boolean animations;
    private boolean advancedEffects;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_launcher_settings);
        GlassPalette.configure(this);
        LinearLayout container=(LinearLayout)((android.widget.ScrollView)((android.view.ViewGroup)findViewById(android.R.id.content)).getChildAt(0)).getChildAt(0);
        float density=getResources().getDisplayMetrics().density;
        CrispTextView themeLabel=new CrispTextView(this);themeLabel.setText("主题颜色");themeLabel.setTextSize(20);themeLabel.setTextColor(0xFFF5F7FC);themeLabel.setPadding(0,(int)(24*density),0,(int)(10*density));int themeIndex=container.indexOfChild(findViewById(R.id.followThemeChoice));container.addView(themeLabel,themeIndex);
        LinearLayout themeTabs=new LinearLayout(this);themeTabs.setOrientation(LinearLayout.HORIZONTAL);themeTabs.setPadding((int)(5*density),(int)(5*density),(int)(5*density),(int)(5*density));
        android.graphics.drawable.GradientDrawable track=new android.graphics.drawable.GradientDrawable();track.setColor(0xFF202833);track.setCornerRadius(18*density);themeTabs.setBackground(track);
        String[] modes={"dark","light","auto"};String[] titles={"深色","浅色","自动"};CrispTextView[] tabs=new CrispTextView[3];
        Runnable refreshTabs=()->{for(int i=0;i<tabs.length;i++)if(tabs[i]!=null){boolean selected=modes[i].equals(GlassPalette.mode);boolean focus=tabs[i].hasFocus();android.graphics.drawable.GradientDrawable bg=new android.graphics.drawable.GradientDrawable();bg.setCornerRadius(14*density);bg.setColor(selected?0xFF536171:focus?0xFF364352:android.graphics.Color.TRANSPARENT);if(focus)bg.setStroke((int)density,0x99FFFFFF);tabs[i].setBackground(bg);tabs[i].setSelected(selected);}};
        for(int i=0;i<3;i++){final int index=i;tabs[i]=new CrispTextView(this);tabs[i].setText(titles[i]);tabs[i].setTextColor(0xFFFFFFFF);tabs[i].setTextSize(18);tabs[i].setGravity(android.view.Gravity.CENTER);tabs[i].setFocusable(true);tabs[i].setOnFocusChangeListener((v,f)->refreshTabs.run());tabs[i].setOnClickListener(v->{getSharedPreferences("appearance",0).edit().putString("mode",modes[index]).apply();GlassPalette.configure(this);refreshTabs.run();setResult(RESULT_OK);});themeTabs.addView(tabs[i],new LinearLayout.LayoutParams(0,(int)(48*density),1));}
        container.addView(themeTabs,themeIndex+1);refreshTabs.run();
        choices = getSharedPreferences(Config.SETTINGS_NAME, MODE_PRIVATE);
        Config config = Config.load(this);
        columns = config.columns;
        animations = config.animations;
        advancedEffects = config.animations && config.advancedEffects;

        int[] ids = {R.id.columns4, R.id.columns5, R.id.columns6, R.id.columns7};
        for (int i = 0; i < ids.length; i++) {
            final int value = i + 4;
            columnChoices[i] = findViewById(ids[i]);
            columnChoices[i].setOnClickListener(v -> {
                if (columns == value) return;
                columns = value;
                choices.edit().putInt("columns", value).apply();
                setResult(RESULT_OK);
                refresh();
            });
        }
        // Keep the numeric choices increasing in the Hebrew reading direction. Reorder the
        // actual views so D-pad focus follows their positions without changing the saved values.
        if (getResources().getBoolean(R.bool.rtl_app_grid)) {
            LinearLayout row = (LinearLayout) columnChoices[0].getParent();
            row.removeAllViews();
            for (int i = columnChoices.length - 1; i >= 0; i--) row.addView(columnChoices[i]);
        }
        animationsChoice = findViewById(R.id.animationsChoice);
        animationsChoice.setOnClickListener(v -> {
            animations = !animations;
            if (!animations) advancedEffects = false;
            choices.edit().putBoolean("animations", animations)
                    .putBoolean("advanced_effects", advancedEffects).apply();
            setResult(RESULT_OK);
            refresh();
        });
        advancedChoice = findViewById(R.id.advancedChoice);
        advancedChoice.setOnClickListener(v -> {
            if (advancedEffects) {
                setAdvanced(false);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.advanced_effects)
                        .setMessage(R.string.advanced_effects_warning)
                        .setPositiveButton(R.string.enable, (dialog, which) -> setAdvanced(true))
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });
        followThemeChoice = findViewById(R.id.followThemeChoice);
        followThemeChoice.setOnClickListener(v -> {
            if (!hasOnTvChoices()) return;
            choices.edit().remove("columns").remove("animations").remove("advanced_effects").commit();
            Config current = Config.load(this);
            columns = current.columns;
            animations = current.animations;
            advancedEffects = current.animations && current.advancedEffects;
            setResult(RESULT_OK);
            refresh();
        });
        findViewById(R.id.defaultHomeChoice).setOnClickListener(v -> {
            Toast.makeText(this, R.string.home_settings_help, Toast.LENGTH_LONG).show();
            open(new Intent(Settings.ACTION_HOME_SETTINGS));
        });
        findViewById(R.id.tvSettingsChoice).setOnClickListener(v ->
                open(new Intent(Settings.ACTION_SETTINGS)));
        refresh();
        columnChoices[Math.max(0, Math.min(columns - 4, 3))].requestFocus();
    }

    private void setAdvanced(boolean enabled) {
        advancedEffects = enabled;
        choices.edit().putBoolean("advanced_effects", enabled).apply();
        setResult(RESULT_OK);
        refresh();
    }

    private void refresh() {
        for (int i = 0; i < columnChoices.length; i++) {
            columnChoices[i].setSelected(columns == i + 4);
        }
        animationsChoice.setText(getString(R.string.animations_state,
                getString(animations ? R.string.on : R.string.off)));
        advancedChoice.setText(getString(R.string.advanced_effects_state,
                getString(advancedEffects ? R.string.on : R.string.off)));
        advancedChoice.setEnabled(animations);
        advancedChoice.setAlpha(animations ? 1f : 0.5f);
        followThemeChoice.setSelected(!hasOnTvChoices());
    }

    private boolean hasOnTvChoices() {
        return choices.contains("columns") || choices.contains("animations")
                || choices.contains("advanced_effects");
    }

    private void open(Intent intent) {
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException | SecurityException e) {
            Toast.makeText(this, R.string.not_available, Toast.LENGTH_LONG).show();
        }
    }
}
