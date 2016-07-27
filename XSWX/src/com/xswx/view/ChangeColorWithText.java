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
	private String mText = "΢��";
	private int mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, 
			getResources().getDisplayMetrics());

	//�����������������ڴ��л�ͼ
	private Canvas mCanvas;
	private Bitmap mBitmap;
	private Paint mPaint;
	
	private float mAlpha; //�����޸Ĵ�ɫ��͸����
	
	private Rect mIconRect;//����icon�ķ�Χ
	private Rect mTextBound;//����text�ķ�Χ
	private Paint mTextPaint;//����text��paint
	

	public ChangeColorWithText(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}

	public ChangeColorWithText(Context context) {
		this(context, null);
	}
	
	public ChangeColorWithText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		//��ȡ�Զ��������Զ������Լ�
		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.ChangeColorWithText);
		
		//ѭ�����Լ���ȡ��ÿ������
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
								12, getResources().getDisplayMetrics()));//����ע����ϸ����һ�����д���
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
	 * ����TextView�Ĵ�С�����������������Icon��С
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
	 * �����������
	 */
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(mIconBitmap, null, mIconRect, null);
		int alpha = (int) Math.ceil(255 * mAlpha);
		
		setupTargetBitmap(alpha);
		drawSourceText(canvas,alpha);//����ԭ�ı�
		drawTargetText(canvas,alpha);//���ñ�ɫ�ı�
		
		canvas.drawBitmap(mBitmap,0,0,null);
	}
	
	/**
	 * ���ñ�ɫ�ı�
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
	 * ����ԭ�ı�
	 */
	private void drawSourceText(Canvas canvas,int alpha) {
		//���ƺ÷��ı���textView
		mTextPaint.setColor(0xff333333);
		mTextPaint.setAlpha(255-alpha);
		int x = getMeasuredWidth()/2 - mTextBound.width()/2;
		int y = mIconRect.bottom + mTextBound.height();
		canvas.drawText(mText, x, y, mTextPaint);
	}

	/**
	 * ���ڴ��л�����ɫ���Ա仯��icon
	 */
	private void setupTargetBitmap(int alpha){
		//1����������ɫ�������ε�
		mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredWidth(), Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mPaint = new Paint();
		mPaint.setColor(mColor);
		mPaint.setDither(true);//�����
		mPaint.setAntiAlias(true);//������
		mPaint.setAlpha(alpha);
		//����ɫ��Paint�»���icon
		mCanvas.drawRect(mIconRect, mPaint);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		mPaint.setAlpha(255);
		
		//2�������õ�icon��ʾ��mPanvas��
		mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
		
	}

	
	/**
	 * ��Activity����ʱ�����浱ǰalpha��״̬
	 */
    public final static String 	INSTANCE_STATE = "instance_state";
    public final static String 	STATE_ALPHA = "state_alpha";
	@Override
	protected Parcelable onSaveInstanceState() {
		
		//����Bunlder�洢ֵ
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
		//�ػ�
		invalidateView();
	}

	//�ػ�
	private void invalidateView() {
		if (Looper.getMainLooper() == Looper.myLooper()) {
			invalidate();
		} else{
			postInvalidate();
		}
		
	}
}
