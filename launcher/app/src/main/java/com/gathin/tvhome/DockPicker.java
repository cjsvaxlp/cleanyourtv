package com.gathin.tvhome;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;
import org.json.JSONArray;
import java.util.*;

final class DockPicker {
 static java.util.List<String> load(Context context,java.util.List<String> fallback) {
  String saved=context.getSharedPreferences("dock_choices",0).getString("apps",null);
  if(saved==null)return fallback;
  try {JSONArray array=new JSONArray(saved);List<String> list=new ArrayList<>();for(int i=0;i<Math.min(4,array.length());i++){String pkg=array.getString(i);if(!pkg.isEmpty()&&!list.contains(pkg))list.add(pkg);}return list;}
  catch(Exception invalid){return fallback;}
 }
 static void show(Activity activity,String pkg,Runnable done) {
  List<String> apps=new ArrayList<>(Config.load(activity).dockApps);
  if(apps.contains(pkg)){apps.remove(pkg);save(activity,apps,pkg,done);return;}
  if(apps.size()<4){apps.add(pkg);save(activity,apps,pkg,done);return;}
  String[] labels=new String[4];
  for(int i=0;i<4;i++){String name=apps.get(i);try{name=activity.getPackageManager().getApplicationLabel(activity.getPackageManager().getApplicationInfo(name,0)).toString();}catch(Exception ignored){}labels[i]="Replace slot "+(i+1)+": "+name;}
  GlassActions.show(activity,activity.getCurrentFocus(),"Choose a slot to replace",labels,slot->{apps.set(slot,pkg);save(activity,apps,pkg,done);});
 }
 private static void save(Activity a,List<String> apps,String pkg,Runnable done){
  if(!LibraryApps.save(a,pkg,true)){Toast.makeText(a,"Unable to save app",Toast.LENGTH_SHORT).show();return;}
  if(!a.getSharedPreferences("dock_choices",0).edit().putString("apps",new JSONArray(apps).toString()).commit()){Toast.makeText(a,"Unable to save dock",Toast.LENGTH_SHORT).show();return;}
  Toast.makeText(a,apps.contains(pkg)?"Added to dock":"Removed from dock",Toast.LENGTH_SHORT).show();done.run();
 }
}
