package com.gathin.tvhome;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.util.*;
public class HongKongWallpaperTest {
 @Test public void quietHoursHaveExactBoundaries(){
  assertTrue(HongKongWallpaper.quietAt(0));
  assertTrue(HongKongWallpaper.quietAt(359));
  assertFalse(HongKongWallpaper.quietAt(360));
  assertFalse(HongKongWallpaper.quietAt(1439));
 }
 @Test public void nightSplitsTheFiveHourWindowByActualCount(){
  for(int n:new int[]{1,2,3,7,11}){
   int[] minutes=new int[n];
   for(int m=1140;m<1440;m++)minutes[HongKongWallpaper.nightIndex(m,n)]++;
   int min=300,max=0;
   for(int v:minutes){min=Math.min(min,v);max=Math.max(max,v);}
   assertTrue(max-min<=1);
   assertEquals(0,HongKongWallpaper.nightIndex(1140,n));
   assertEquals(n-1,HongKongWallpaper.nightIndex(1439,n));
  }
  assertEquals(0,HongKongWallpaper.nightIndex(1289,2));
  assertEquals(1,HongKongWallpaper.nightIndex(1290,2));
  assertEquals(-1,HongKongWallpaper.nightIndex(0,2));
  assertEquals(-1,HongKongWallpaper.nightIndex(1140,0));
 }
 @Test public void nightGroupIncludesNewFilesButNotDayImages(){
  List<File> group=HongKongWallpaper.nightPhotos(Arrays.asList(new File("hk-day.png"),new File("hk-night.png"),new File("hk-night-peak.jpg"),new File("dark-01.jpg")));
  assertEquals(3,group.size());
  assertEquals("dark-01.jpg",group.get(0).getName());
  assertEquals("hk-night-peak.jpg",group.get(1).getName());
 }
 @Test public void daylightScheduleIsUnchanged(){
  Calendar c=Calendar.getInstance(TimeZone.getTimeZone("Asia/Hong_Kong"));
  c.set(2026,Calendar.OCTOBER,22,6,0,0);
  assertEquals("hk-dawn.png",HongKongWallpaper.photoAt(c.getTimeInMillis()));
  c.set(Calendar.HOUR_OF_DAY,12);c.set(Calendar.MINUTE,20);
  assertEquals("hk-day-kennedy.jpg",HongKongWallpaper.photoAt(c.getTimeInMillis()));
  c.set(Calendar.HOUR_OF_DAY,17);c.set(Calendar.MINUTE,0);
  assertEquals("hk-dusk.png",HongKongWallpaper.photoAt(c.getTimeInMillis()));
 }
}
