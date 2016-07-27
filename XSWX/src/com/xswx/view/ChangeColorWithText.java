package com.xswx.view;


import com.xswx.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class ChangeColorWithText extends View {
	
	private int mColor = 0xFF45C01A;
	private Bitmap mIconBitmap;
	private String mText = "微信";
	private int mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, 
			getResources().getDisplayMetrics());

	//这三个变量是用来内存中绘图
	private Canvas mCanvas;
	private Bitmap mBitmap;
	private Paint mPaint;
	
	private float mAlpha; //用于修改纯色的透明度
	
	private Rect mIconRect;//绘制icon的范围
	private Rect mTextBound;//绘制text的范围
	private Paint mTextPaint;//绘制text的paint
	

	public ChangeColorWithText(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}

	public ChangeColorWithText(Context context) {
		this(context, null);
	}
	
	public ChangeColorWithText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		//获取自定义所有自定义属性集
		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.ChangeColorWithText);
		
		//循环属性集，取出每个参数
		int length = a.getIndexCount();
		for(int i=0;i<length;i++){
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.ChangeColorWithText_icon_:
				BitmapDrawable drawable = (BitmapDrawable) a.getDrawable(attr);
				mIconBitmap  = drawable.getBitmap();
				break;
			case R.styleable.ChangeColorWithText_color:
				mColor = a.getColor(attr, 0xFF45C01A);
				break;
				
			case R.styleable.ChangeColorWithText_text:
				mText = a.getString(attr);
				break;
				
			case R.styleable.ChangeColorWithText_text_size:
				mTextSize = (int) a.getDimension(attr, 
						TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
								12, getResources().getDisplayMetrics()));//等下注意仔细分析一下这行代码
				break;

			default:
				break;
			}
		}
		a.recycle();
		
		mTextBound = new Rect();
		mTextPaint = new Paint();
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setColor(0Xff555555);
		mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
		
	}
	
	/**
	 * 根据TextView的大小，绘制置于它上面的Icon大小
	 */
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = Math.min(getMeasuredHeight()-getPaddingBottom()-getPaddingTop()-mTextBound.height(), getMeasuredWidth()-getPaddingLeft()-getPaddingRight());
		int left = getMeasuredWidth()/2 - width/2;
		int top = getMeasuredHeight()/2 - mTextBound.height()/2 - width/2;
		
		mIconRect = new Rect(left, top, left+width, top+width);
	}
	
	/**
	 * 组件绘制内容
	 */
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(mIconBitmap, null, mIconRect, null);
		int alpha = (int) Math.ceil(255 * mAlpha);
		
		setupTargetBitmap(alpha);
		drawSourceText(canvas,alpha);//设置原文本
		drawTargetText(canvas,alpha);//设置变色文本
		
		canvas.drawBitmap(mBitmap,0,0,null);
	}
	
	/**
	 * 设置变色文本
	 * @param canvas
	 * @param alpha
	 */
	private void drawTargetText(Canvas canvas, int alpha) {
		mTextPaint.setColor(mColor);
		mTextPaint.setAlpha(alpha);
		int x = getMeasuredWidth()/2 - mTextBound.width()/2;
		int y = mIconRect.bottom + mTextBound.height();
		canvas.drawText(mText, x, y, mTextPaint);
	}

	/**
	 * 绘制原文本
	 */
	private void drawSourceText(Canvas canvas,int alpha) {
		//绘制好放文本的textView
		mTextPaint.setColor(0xff333333);
		mTextPaint.setAlpha(255-alpha);
		int x = getMeasuredWidth()/2 - mTextBound.width()/2;
		int y = mIconRect.bottom + mTextBound.height();
		canvas.drawText(mText, x, y, mTextPaint);
	}

	/**
	 * 在内存中绘制颜色可以变化的icon
	 */
	private void setupTargetBitmap(int alpha){
		//1、先设置绿色的正方形底
		mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredWidth(), Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mPaint = new Paint();
		mPaint.setColor(mColor);
		mPaint.setDither(true);//防锯齿
		mPaint.setAntiAlias(true);//防抖动
		mPaint.setAlpha(alpha);
		//在绿色的Paint下绘制icon
		mCanvas.drawRect(mIconRect, mPaint);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		mPaint.setAlpha(255);
		
		//2、将画好的icon显示在mPanvas中
		mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
		
	}

	
	/**
	 * 当Activity回收时，保存当前alpha的状态
	 */
    public final static String 	INSTANCE_STATE = "instance_state";
    public final static String 	STATE_ALPHA = "state_alpha";
	@Override
	protected Parcelable onSaveInstanceState() {
		
		//利用Bunlder存储值
		Bundle bundle = new Bundle();
		bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
		bundle.putFloat(STATE_ALPHA, mAlpha);
		
		return bundle;
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if( state instanceof Bundle){
			Bundle bundle = (Bundle) state;
			mAlpha = bundle.getFloat(STATE_ALPHA);
			super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
			return;
		}
		
		super.onRestoreInstanceState(state);
	}

	
	public void setIconAlpha(float f){
		this.mAlpha = f;
		//重绘
		invalidateView();
	}

	//重绘
	private void invalidateView() {
		if (Looper.getMainLooper() == Looper.myLooper()) {
			invalidate();
		} else{
			postInvalidate();
		}
		
	}
}
