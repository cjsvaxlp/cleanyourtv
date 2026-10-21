package com.gathin.tvhome;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import java.util.function.IntConsumer;

final class GlassActions {
 static void show(Activity activity,View anchor,String title,String[] actions,IntConsumer action){
  float d=activity.getResources().getDisplayMetrics().density;
  LinearLayout panel=new LinearLayout(activity);panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding((int)(10*d),(int)(10*d),(int)(10*d),(int)(10*d));
  GradientDrawable surface=GlassPalette.material(d,22,false);
  surface.setColors(GlassPalette.light()?new int[]{0xE6FAFCFF,0xDCEBF0F7}:new int[]{0xEB303946,0xE6232B37});panel.setBackground(surface);
  CrispTextView heading=new CrispTextView(activity);heading.setText(title);heading.setTextColor(GlassPalette.text());heading.setTextSize(14);heading.setPadding((int)(12*d),(int)(6*d),(int)(12*d),(int)(12*d));panel.addView(heading);
  int width=Math.min((int)(310*d),activity.getResources().getDisplayMetrics().widthPixels-(int)(32*d));
  PopupWindow popup=new PopupWindow(panel,width,ViewGroup.LayoutParams.WRAP_CONTENT,true);popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));popup.setOutsideTouchable(true);popup.setElevation(5*d);
  for(int i=0;i<actions.length;i++){
   final int index=i;CrispTextView row=new CrispTextView(activity);row.setText(actions[i]);row.setTextSize(18);row.setTextColor(GlassPalette.text());row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding((int)(12*d),0,(int)(12*d),0);row.setFocusable(true);
   row.setOnFocusChangeListener((v,focus)->{row.setBackground(GlassPalette.material(d,14,focus));});row.setOnClickListener(v->{popup.dismiss();action.accept(index);});
   final boolean[] pressed={false};row.setOnKeyListener((v,key,event)->{if(key!=KeyEvent.KEYCODE_DPAD_CENTER&&key!=KeyEvent.KEYCODE_ENTER)return false;if(event.getAction()==KeyEvent.ACTION_DOWN){if(event.getRepeatCount()==0)pressed[0]=true;return true;}boolean activate=pressed[0];pressed[0]=false;if(activate)row.performClick();return true;});
   panel.addView(row,new LinearLayout.LayoutParams(-1,(int)(50*d)));
  }
  panel.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.UNSPECIFIED);
  int sw=activity.getResources().getDisplayMetrics().widthPixels,sh=activity.getResources().getDisplayMetrics().heightPixels;
  int[] pos={sw/2,sh/2};if(anchor!=null)anchor.getLocationOnScreen(pos);
  int x=Math.max((int)(16*d),Math.min(pos[0]+(anchor==null?0:anchor.getWidth())+(int)(10*d),sw-width-(int)(16*d)));
  int y=Math.max((int)(16*d),Math.min(pos[1],sh-panel.getMeasuredHeight()-(int)(16*d)));
  popup.showAtLocation(activity.getWindow().getDecorView(),Gravity.TOP|Gravity.LEFT,x,y);
  panel.getChildAt(1).requestFocus();panel.setAlpha(0);panel.animate().alpha(1).setDuration(140).start();
 }
}
