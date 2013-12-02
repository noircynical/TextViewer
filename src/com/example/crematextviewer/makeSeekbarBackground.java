package com.example.crematextviewer;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

public class makeSeekbarBackground {
	private Context mContext = null;
	private Bitmap mSeekBarBg = null;
	private static makeSeekbarBackground mInstance = null;
	
	private makeSeekbarBackground(Context context) {
		mContext = context;
		mInstance = this;
	}
	
	public static makeSeekbarBackground makeInstance(Context context) {
		if (mInstance == null) {
			mInstance = new makeSeekbarBackground(context);
		}
		return mInstance;
	}
	
	public static makeSeekbarBackground getInstance() {
		return mInstance;
	}
	
	
	public BitmapDrawable getSeekBarBg() {
		BitmapDrawable d = makeSeekBarBg();
		
		if (d != null) {
			return d;
		}
		return null;
	}
	
	private BitmapDrawable makeSeekBarBg() {
		final int pW = 636;
		final int pH = 21;
		final int lW = 636;
		final int lH = 21;
		
		final int dotMargin = 15;
		final int dotSize = 5;
		
		Configuration conf = mContext.getResources().getConfiguration();
		int width = 0;
		int height = 0;
		if (conf.orientation == Configuration.ORIENTATION_PORTRAIT) {
			width = pW;
			height = pH;
		}
		else {
			width = lW;
			height = lH;
		}
		
		if (mSeekBarBg != null) {
			mSeekBarBg = null;
		}
		mSeekBarBg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mSeekBarBg);
		Bitmap dot = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bar_bottom_dot);
		
		int cnt = (width / (dotMargin+dotSize));
		int leftMargin = 0;
		for (int i = 0 ; i < cnt ; i++) {
			canvas.drawBitmap(dot, leftMargin, 7, null);
			leftMargin += (dotMargin+dotSize);
		}
		
		return new BitmapDrawable(mContext.getResources(), mSeekBarBg);
	}
}
