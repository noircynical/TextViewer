package com.example.crematextviewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.crematextviewer.CremaTextViewerActivity;
import com.example.crematextviewer.HaveText;
import com.shou.ui.*;

public class ShowTextOnCanvas extends View {

	private final int VIEW = 0;
	private final int SELECT = 1;

	private int viewingMode = VIEW;

	private Context mContext = null;
	private String filePath = null;
	private String fileString = null;
	private String mEncoding = "utf-8";

	private Paint backpaint= new Paint();
	private Paint mPaint = new Paint();
	
	private int mWidth = 0;
	private int mHeight = 0;
	private int mEdge = 5;

	private int mTextSize = 30;
	private int mBackground = 0;
	private int mLineLeading = 5;
	
	private Bitmap mBackgroundBitmap= null;
	private Bitmap mFontBitmap= null;
	
	private Typeface mTypeface= null;

	private int letterCount = 0;

	private int onelineHeight;

	private int screenStart = 0;
	private int screenEnd = 0;
	private int lineStart = 0;
	private int lineEnd = 0;

	private int startX = 0;
	private int endX = 0;

	private Rect mRect = new Rect();

	public static final int SHOW = 0;
	public static final int HIDE = 1;
	private int menuMode = HIDE;

	private PointF selectStart, selectEnd;

	private int onepageLetter;
	
	private int mBackgroundThemeNum= 1;

	public ShowTextOnCanvas(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public ShowTextOnCanvas(Context context, AttributeSet attr) {
		super(context, attr);
		mContext = context;
		init();
	}

	public ShowTextOnCanvas(Context context, AttributeSet attr, int defstyle) {
		super(context, attr, defstyle);
		mContext = context;
		init();
	}

	@Override
	public void onDraw(Canvas canvas) {
		mPaint.setTextSize(mTextSize);
		getSize(canvas);
		
		if(mBackgroundBitmap== null)
			canvas.drawColor(Color.WHITE);
		else{
			backpaint.setShader(new BitmapShader(mBackgroundBitmap, TileMode.REPEAT, TileMode.REPEAT));
			canvas.drawPaint(backpaint);
		}

		int lineCurrentCount = 1;
		lineStart = screenStart;
		lineEnd = lineStart;
		
		progressUpdate();
		
		while (true) {
			while (true) {
				if(lineEnd == fileString.length()){
					break;
				}
				else if ((mPaint.measureText(fileString
						.substring(lineStart, lineEnd)) >= mWidth - mEdge * 2)) {
					lineEnd--;
					break;
				} else if (fileString.charAt(lineEnd) == '\n') {
					lineEnd++;
					break;
				}
				lineEnd++;
			}
			canvas.drawText(fileString.substring(lineStart, lineEnd), mEdge,
					(lineCurrentCount) * (mTextSize + mLineLeading), mPaint);
			lineCurrentCount++;
			if ((lineCurrentCount - 1) * (mTextSize + mLineLeading) >= mHeight
					- (mTextSize + mLineLeading)) {
				System.out.println("screen start: " + screenStart
						+ "screen end: " + screenEnd + "line start: "
						+ lineStart + "line end: " + lineEnd);
				screenEnd = lineEnd - 1;
				break;
			}
			lineStart = lineEnd;
		}
	}

	private void init() {
		mPaint = new Paint();
		backpaint= new Paint();
		
		System.out.println("call init");
	}
	
	public void setContext(Context context){
		mContext= context;
	}

	public int setLength() {
		return fileString.length();
	}
	
	public String setString(){
		return fileString;
	}

	public void getSize(Canvas canvas) {
		mWidth = (int) canvas.getWidth();
		mHeight = (int) canvas.getHeight();
		System.out.println("Width: " + mWidth + "\nHeight: " + mHeight);
	}

	public void getFile(String filepath) {
		filePath = filepath;
		fileString = fileReading(new File(filePath));
		letterCount = fileString.length();
	}

	// set setting value
	public void getTextSize(int size) {
		mTextSize = size;
		invalidate();
	}
	
	public int setTextSize(){
		return mTextSize;
	}
	
	public void getBackgroundPattern(int ThemeNum){
		mBackgroundThemeNum= ThemeNum;
		invalidate();
	}

	public void getBackgroundColor(int ThemeNum) {
		mBackground = ThemeNum;
		invalidate();
	}
	public int setBackgroundColor(){
		return mBackground;
	}
	
	public void getBitmap(Bitmap background, int color){
		mBackgroundBitmap= background;
		mPaint.setColor(color);
		invalidate();
	}

	public void getLineLeading(int ThemeNum) {
		mLineLeading = (int)((double)ThemeNum * (double)mTextSize * 0.5);
		invalidate();
	}
	
	public int setLineLeading(){
		return mLineLeading;
	}

	public void setProgress(int position) {
		//screenStart = position / 100 * fileString.length();
		screenStart= position;
		lineStart = screenStart;
		invalidate();
	}
	
	public int getProgress(){
		return screenStart;
	}
	
	public void progressUpdate(){
		//((CremaTextViewerActivity)mContext).mProgressPercentage.setText((screenStart/fileString.length()*100)+"%");
		((CremaTextViewerActivity)mContext).mProgress.setProgress(screenStart);
	}
	
//	public void getTypeface(String typeface){
//		//mTypeface= typeface;
//		
//	}
	
	public void setTypeface(Typeface typeface){
		//Toast.makeText(mContext, "set font", Toast.LENGTH_SHORT).show();
		mPaint.setTypeface(typeface);
		invalidate();
	}
	
	public boolean setBarMode(){
		if(menuMode == SHOW){
			menuMode = HIDE;
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (viewingMode) {
		case VIEW:
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = (int) event.getX();
				break;
			case MotionEvent.ACTION_UP:
				endX = (int) event.getX();
				if (Math.abs(startX - endX) < 5) {
					System.out.println("startX: " + startX + "\nleft: "
							+ mWidth / 3 + "\nright: " + mWidth / 3 * 2);
					if (startX > mWidth / 3 * 2) {
						screenStart = screenEnd + 1;
						lineStart = screenStart;
						if (menuMode == SHOW) {
							((CremaTextViewerActivity) mContext).hideButtons();
							menuMode = HIDE;
						}
						invalidate();
					} else if (startX < mWidth / 3) {
						onepageLetter = screenEnd - screenStart;
						if (onepageLetter > screenStart) {
							screenStart = 0;
							Toast.makeText(mContext, "This is first page",
									Toast.LENGTH_SHORT).show();
						} else
							screenStart = screenStart - onepageLetter;
						if (menuMode == SHOW) {
							((CremaTextViewerActivity) mContext).hideButtons();
							menuMode = HIDE;
						}
						invalidate();
					} else {
						switch (menuMode) {
						case HIDE:
							((CremaTextViewerActivity) mContext).showButtons();
//							((CremaTextViewerActivity)mContext).test(SHOW);
							menuMode = SHOW;
//							((CremaTextViewerActivity)mContext).userclicked();
							break;
						case SHOW:
							((CremaTextViewerActivity)mContext).hideButtons();
//							((CremaTextViewerActivity)mContext).test(HIDE);
//							((CremaTextViewerActivity)mContext).userclicked();
							menuMode = HIDE;
							break;
						}
					}
				} else {
					System.out.println(endX - startX);
					if ((endX - startX) > 0) {
						onepageLetter = screenEnd - screenStart;
						if (onepageLetter > screenStart) {
							screenStart = 0;
							Toast.makeText(mContext, "This is first page",
									Toast.LENGTH_SHORT).show();
						} else
							screenStart = screenStart - onepageLetter;
						if (menuMode == SHOW) {
							((CremaTextViewerActivity) mContext).hideButtons();
							menuMode = HIDE;
						}
						invalidate();
					} else {
						screenEnd = lineEnd - 1;
						screenStart = lineEnd;
						System.out.println("screenStart: " + screenStart
								+ "\nscreenEnd: " + screenEnd + "\nlineStart: "
								+ lineStart + "\nlineEnd: " + lineEnd);
						invalidate();
					}
				}
				break;
			}
			break;
//		case SELECT:
//			switch (event.getAction()) {
//			case MotionEvent.ACTION_DOWN:
//				selectStart.x = event.getX();
//				selectStart.y = event.getY();
//				break;
//			case MotionEvent.ACTION_UP:
//				selectEnd.x = event.getX();
//				selectEnd.y = event.getY();
//				viewingMode = VIEW;
//				break;
//			}
//			break;
		}

		return true;
	}

	public void setEncoder(String encoding) {
		mEncoding = encoding;
		fileString = fileReading(new File(filePath));
		letterCount = fileString.length();
		invalidate();
	}

	private String fileReading(File mFile) {
		StringBuilder stBuilder = new StringBuilder();

		try {
			if (!mFile.isFile()) {
				Toast.makeText(mContext, "file not existed", Toast.LENGTH_SHORT)
						.show();
			} else {

				try {

					Toast.makeText(mContext, "Encoding: " + mEncoding,
							Toast.LENGTH_SHORT).show();

					FileInputStream fis = new FileInputStream(mFile);
					InputStreamReader isr = new InputStreamReader(fis,
							Charset.forName(mEncoding));
					BufferedReader br = new BufferedReader(isr);
					System.out.println(isr.getEncoding());
					try {
						String line;
						while ((line = br.readLine()) != null) {
							stBuilder.append(line);
							stBuilder.append('\n');
						}
					} catch (IOException e) {
						throw e;
					} finally {
						isr.close();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("string builder: " + stBuilder.capacity());
		return stBuilder.toString();
	}
}

/*
 * if (Math.abs(startX - endX) < 5) {
 * System.out.println("startX: "+startX+"\nleft: "
 * +mWidth/3+"\nright: "+mWidth/3*2); if(startX > mWidth/3 && startX<
 * mWidth/3*2){ switch (menuMode) { case HIDE: ((CremaTextViewerActivity)
 * mContext).showButtons(); menuMode = SHOW; break; case SHOW:
 * ((CremaTextViewerActivity) mContext).hideButtons(); menuMode = HIDE; break; }
 * } else if (startX > mWidth / 3 * 2) { pageCount++; screenStart= screenEnd+1;
 * lineStart = screenStart;
 * System.out.println("screenStart: "+screenStart+"\nscreenEnd: "
 * +screenEnd+"\nlineStart: "+lineStart+"\nlineEnd: "+lineEnd); mode =
 * CALL_FRONT; if (menuMode == SHOW) { ((CremaTextViewerActivity)
 * mContext).hideButtons(); menuMode = HIDE; } invalidate(); } else if (startX <
 * mWidth / 3) { pageCount--; screenStart = lineStart + 1; screenEnd =
 * lineStart; lineEnd = screenEnd;
 * System.out.println("screenStart: "+screenStart
 * +"\nscreenEnd: "+screenEnd+"\nlineStart: "+lineStart+"\nlineEnd: "+lineEnd);
 * mode = CALL_BACK; if (menuMode == SHOW) { ((CremaTextViewerActivity)
 * mContext).hideButtons(); menuMode = HIDE; } invalidate(); } } else {
 * System.out.println(endX-startX); if ((endX - startX) > 0) { pageCount--;
 * screenStart = lineStart + 1; screenEnd = lineStart;
 * System.out.println("screenStart: "
 * +screenStart+"\nscreenEnd: "+screenEnd+"\nlineStart: "
 * +lineStart+"\nlineEnd: "+lineEnd); mode = CALL_BACK; invalidate(); } else {
 * pageCount++; screenEnd = lineEnd - 1; screenStart = lineEnd;
 * System.out.println
 * ("screenStart: "+screenStart+"\nscreenEnd: "+screenEnd+"\nlineStart: "
 * +lineStart+"\nlineEnd: "+lineEnd); mode = CALL_FRONT; invalidate(); } }
 * break;
 */
