package com.gathin.tvhome;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Calendar;
import java.util.TimeZone;

/** Cheap translucent materials: no per-frame blur or network work. */
final class GlassPalette {
    static Boolean preview;
    static String mode = "auto";
    static void configure(android.content.Context context) { mode=context.getSharedPreferences("appearance",0).getString("mode","auto"); }
    static void choose(android.app.Activity activity,Runnable changed) {
        String[] values={"dark","light","auto"};
        int selected=java.util.Arrays.asList(values).indexOf(mode);
        new android.app.AlertDialog.Builder(activity).setTitle("主题颜色")
            .setSingleChoiceItems(new String[]{"深色","浅色","自动（跟随时间）"},selected,(dialog,which)->{
                activity.getSharedPreferences("appearance",0).edit().putString("mode",values[which]).apply();
                configure(activity);dialog.dismiss();changed.run();
            }).setNegativeButton("取消",null).show();
    }
    static boolean light() {
        if (preview != null) return preview;
        if (mode.equals("dark")) return false;
        if (mode.equals("light")) return true;
        int hour = Calendar.getInstance(TimeZone.getTimeZone("Asia/Hong_Kong")).get(Calendar.HOUR_OF_DAY);
        return lightAt(hour);
    }
    static boolean lightAt(int hour) { return hour >= 6 && hour < 19; }
    static int text() { return light() ? 0xFF17202C : 0xFFF5F7FC; }
    static GradientDrawable material(float density, float radius, boolean focused) {
        boolean light = light();
        int[] colors = light ? new int[]{focused ? 0x70FFFFFF : 0x40FFFFFF, 0x20EEF4FF}
                : new int[]{focused ? 0x995F6B82 : 0x88434C60, 0x66212B3C};
        if (focused) colors = light ? new int[]{0x40575C63, 0x30484D54} : new int[]{0xAA252A32, 0x88202730};
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        d.setCornerRadius(radius * density);
        d.setStroke(Math.max(1, (int)density), light ? 0x90FFFFFF : 0x50FFFFFF);
        return d;
    }
    static void pill(TextView view) {
        float density = view.getResources().getDisplayMetrics().density;
        StateListDrawable bg = new StateListDrawable();
        // Keep the initial material opaque in the drawable state; activity recreation
        // can interrupt StateListDrawable alpha transitions before their first frame.
        bg.addState(new int[]{android.R.attr.state_focused}, material(density, 24, true));
        bg.addState(new int[]{android.R.attr.state_activated}, material(density, 24, true));
        bg.addState(new int[]{}, material(density, 24, false));
        view.setBackground(bg);
        bg.jumpToCurrentState();
        ColorStateList foreground = new ColorStateList(new int[][]{new int[]{android.R.attr.state_focused}, new int[]{}}, new int[]{text(), text()});
        view.setTextColor(foreground);
        view.setCompoundDrawableTintList(foreground);
        view.setShadowLayer(0, 0, 0, 0);
    }
    static void popup(View view) {
        float density = view.getResources().getDisplayMetrics().density;
        view.setBackground(material(density, 22, false));
        recolor(view);
    }
    private static void recolor(View view) {
        if (view instanceof TextView) {
            TextView t = (TextView)view;
            t.setTextColor(new ColorStateList(new int[][]{new int[]{android.R.attr.state_focused},new int[]{}},
                    new int[]{0xFF17202C,text()}));
            t.setCompoundDrawableTintList(ColorStateList.valueOf(text()));
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)view;
            for(int i=0;i<group.getChildCount();i++) recolor(group.getChildAt(i));
        }
    }
}
