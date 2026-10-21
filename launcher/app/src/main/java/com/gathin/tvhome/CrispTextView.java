package com.gathin.tvhome;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/** Draws compact control labels without relying on TextView's cached display list. */
public class CrispTextView extends TextView {
 private Bitmap label;
 private String signature="";
 public CrispTextView(Context c){super(c);}
 public CrispTextView(Context c,AttributeSet a){super(c,a);}
 @Override protected void onDraw(Canvas canvas){
  int w=getWidth(),h=getHeight();if(w<1||h<1)return;
  Drawable icon=getCompoundDrawablesRelative()[0];
  String value=getText()==null?"":getText().toString();
  String key=w+":"+h+":"+value+":"+getCurrentTextColor()+":"+getTextSize()+":"+System.identityHashCode(icon)+":"+java.util.Arrays.toString(getDrawableState());
  try{
   if(label==null||!key.equals(signature)){
    Bitmap next=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);next.setDensity(canvas.getDensity());Canvas offscreen=new Canvas(next);
    android.text.TextPaint paint=new android.text.TextPaint(Paint.ANTI_ALIAS_FLAG|Paint.SUBPIXEL_TEXT_FLAG);
    paint.setTypeface(getTypeface());paint.setTextSize(getTextSize());paint.setColor(getCurrentTextColor());paint.setStyle(Paint.Style.FILL);paint.clearShadowLayer();
    int iw=icon==null?0:Math.max(0,icon.getIntrinsicWidth()),ih=icon==null?0:Math.max(0,icon.getIntrinsicHeight());
    int gap=icon!=null&&!value.isEmpty()?getCompoundDrawablePadding():0;
    float available=Math.max(0,w-getPaddingLeft()-getPaddingRight()-iw-gap);
    String text=TextUtils.ellipsize(value,paint,available,TextUtils.TruncateAt.END).toString();
    float content=iw+gap+paint.measureText(text);int gravity=getGravity()&Gravity.HORIZONTAL_GRAVITY_MASK;
    float x=gravity==Gravity.CENTER_HORIZONTAL?(w-content)/2f:gravity==Gravity.RIGHT?w-getPaddingRight()-content:getPaddingLeft();
    if(icon!=null){icon.setState(getDrawableState());icon.setTint(getCurrentTextColor());int y=(h-ih)/2;icon.setBounds((int)x,y,(int)x+iw,y+ih);icon.draw(offscreen);x+=iw+gap;}
    Paint.FontMetrics fm=paint.getFontMetrics();float baseline=getPaddingTop()+(h-getPaddingTop()-getPaddingBottom()-fm.bottom+fm.top)/2f-fm.top;
    Path glyphs=new Path();paint.getTextPath(text,0,text.length(),x,baseline,glyphs);
    offscreen.drawPath(glyphs,paint);label=next;signature=key;
   }
   canvas.drawBitmap(label,0,0,null);
  }catch(OutOfMemoryError error){label=null;super.onDraw(canvas);}
 }
 @Override public void scrollTo(int x,int y){super.scrollTo(0,0);}
 @Override protected void onDetachedFromWindow(){label=null;signature="";super.onDetachedFromWindow();}
}
