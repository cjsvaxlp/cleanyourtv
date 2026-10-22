package com.gathin.tvhome;

import java.util.Calendar;
import java.util.TimeZone;

/** Local, deterministic day cycle; never changes the TV system clock. */
final class HongKongWallpaper {
    static int minuteAt(long now) {
        Calendar c=Calendar.getInstance(TimeZone.getTimeZone("Asia/Hong_Kong"));
        c.setTimeInMillis(now);
        return c.get(Calendar.HOUR_OF_DAY)*60+c.get(Calendar.MINUTE);
    }
    static boolean quietAt(int minute) { return minute>=0 && minute<360; }
    static int nightIndex(int minute,int count) {
        return count>0 && minute>=1140 && minute<1440
                ? Math.min(count-1,(minute-1140)*count/300) : -1;
    }
    static java.util.List<java.io.File> nightPhotos(java.util.List<java.io.File> files) {
        java.util.List<java.io.File> night=new java.util.ArrayList<>();
        for(java.io.File f:files){String n=f.getName().toLowerCase(java.util.Locale.ROOT);
            if(n.startsWith("hk-night")||n.startsWith("dark-"))night.add(f);
        }
        night.sort(java.util.Comparator.comparing(java.io.File::getName));
        return night;
    }
    static String at(long now) {
        Calendar clock = Calendar.getInstance(TimeZone.getTimeZone("Asia/Hong_Kong"));
        clock.setTimeInMillis(now);
        return forHour(clock.get(Calendar.HOUR_OF_DAY));
    }
    static String photoAt(long now) {
        Calendar clock = Calendar.getInstance(TimeZone.getTimeZone("Asia/Hong_Kong"));
        clock.setTimeInMillis(now);
        int hour = clock.get(Calendar.HOUR_OF_DAY);
        int minute = hour * 60 + clock.get(Calendar.MINUTE);
        if (minute >= 600 && minute < 1020) {
            String[] day = {"hk-day.png", "hk-day-kennedy.jpg", "hk-day-disney.jpg"};
            return day[(minute - 600) * day.length / 420];
        }
        if (minute >= 1140 || minute < 360) {
            String[] night = {"hk-night.png", "hk-night-peak.jpg"};
            int elapsed = (minute - 1140 + 1440) % 1440;
            return night[elapsed * night.length / 660];
        }
        return forHour(hour);
    }
    static String forHour(int hour) {
        if (hour >= 6 && hour < 10) return "hk-dawn.png";
        if (hour >= 10 && hour < 17) return "hk-day.png";
        if (hour >= 17 && hour < 19) return "hk-dusk.png";
        return "hk-night.png";
    }
}
