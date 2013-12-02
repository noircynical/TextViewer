package com.example.crematextviewer;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.crematextviewer.*;
import com.shou.ui.*;
import com.example.SystemUIHider.*;

public class CremaTextViewerActivity extends Activity {

	public static Context mContext;

	// constants
	private final int SETTING_ON = 0;
	private final int SETTING_OFF = 1;

	// for Menu
	private ViewAnimator mMainTopBar = null;
	private ViewAnimator mMainBottomBar = null;
	private ViewAnimator mSettingBar = null;
	private ViewAnimator mFontSetting = null;
	private ViewAnimator mSearchBar = null;
	private ViewAnimator mSearchResultBar = null;

	private TextView mTextName = null;
	public TextView mProgressPercentage = null;
	public SeekBar mProgress = null;
	private SeekBar mBrightness = null;

	private makeSeekbarBackground mSeekbarBg = null;

	private ImageView mSettingButton = null;
	private int mBottomBarMode = SETTING_OFF;
	private int mSettingMode = SETTING_OFF;
	private int mFontTypeDropdownMode = SETTING_OFF;
	private int mSearchBarMode = SETTING_OFF;
	private int mSearchResultMode = SETTING_OFF;
	private int mSearching = SETTING_OFF;

	private Spinner mFontSpinner = null;
	private FontManager mFontManager = null;
	Hashtable<String, Typeface> typefaceHash = new Hashtable<String, Typeface>();

	// search layout
	private ImageView mSearchBtn = null;
	private ImageView mSearchSelectedBtn = null;

	private ArrayAdapter<SearchList> mSearchArrayAdapter = null;

	private EditText mSearchText = null;
	private TextView mSearchTextResultShort = null;

	private SearchContent mSearchContent = null;

	private ListView mSearchListView = null;

	private SearchAdapter mSearchAdapter = null;

	// for encoding setting
	private PopupWindow mPopupWindow = null;

	// for Canvas
	private ShowTextOnCanvas mShowTextOnCanvas = null;
	private String filepath = "/sdcard/Download/textSample.txt";

	// selected
	private ImageView mSize01Border = null;
	private ImageView mSize02Border = null;
	private ImageView mSize03Border = null;
	private ImageView mSize04Border = null;
	private ImageView mBackground01Border = null;
	private ImageView mBackground02Border = null;
	private ImageView mBackground03Border = null;
	private ImageView mBackground04Border = null;
	private ImageView mBackground01Selected= null;
	private ImageView mBackground02Selected= null;
	private ImageView mBackground03Selected= null;
	private ImageView mBackground04Selected= null;
	private ImageView mLineLeading01Selected = null;
	private ImageView mLineLeading02Selected = null;
	private ImageView mLineLeading03Selected = null;
	private ImageView mLineLeading04Selected = null;

	private TextView mSize01Text = null;
	private TextView mSize02Text = null;
	private TextView mSize03Text = null;
	private TextView mSize04Text = null;
	private TextView mBackground01Text = null;
	private TextView mBackground02Text = null;
	private TextView mBackground03Text = null;
	private TextView mBackground04Text = null;

	// for ETC
	private int mWidth = 0;
	private int mHeight = 0;

	private Animation anim = null;

	private String mFont = null;

	private EditText mEdit;
	private Button mSearch;

	// sukdory √ﬂ∞°
	private DefaultFontList[] mFontList = { DefaultFontList.UNBATANG,
			DefaultFontList.UNDINARU, DefaultFontList.UNDOTUM,
			DefaultFontList.UNGRAPHIC, DefaultFontList.UNGUNGSEO,
			DefaultFontList.UNPILGI, DefaultFontList.KOPUBBATANG_MEDIUM,
			DefaultFontList.KOPUBDOTUM_MEDIUM, };

	//
	
	public TextPreference mPreference= null;
	
	private int mEncodingNumber= 1;
	private int mLineLeadingNumber= 1;
	private int mBackgroundThemeNumber= 1;
	private int mTextSize= 1;
	private int mTypeface= 0;
	
	private Bitmap[] mBitmapPattern= new Bitmap[3];
	
	private ArrayList<Integer> mUndoList= new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_crema_text_viewer);

		mContext = this;
		
		// create preference
		//mPreference= new TextPreference(filepath.substring(filepath.lastIndexOf('/')+1, filepath.indexOf('.')));

		createUI();
		loadPreference();
	}
	
	public void loadPreference(){
		SharedPreferences pref= mContext.getSharedPreferences(filepath.substring(filepath.lastIndexOf('/')+1, filepath.indexOf('.')), Context.MODE_PRIVATE);
		
		// encoding
		mEncodingNumber= pref.getInt("Encoding", 1);
		System.out.println("encoding: "+mEncodingNumber);
		OnEncodingSet(mEncodingNumber);
		
		// progress
		mShowTextOnCanvas.setProgress(pref.getInt("StartIndex", 0));
		
		// text size
		mTextSize= pref.getInt("TextSize", 1);
		System.out.println("textsize: "+mTextSize);
		OnTextSizeSet(mTextSize);
		
		// background
		mBackgroundThemeNumber= pref.getInt("Background", 1);
		System.out.println("background: "+mBackgroundThemeNumber);
		OnBackgroundSet(mBackgroundThemeNumber);
		
		// typeface
		mTypeface= pref.getInt("Typeface", 0);
		System.out.println("GET typeface value: "+mTypeface);
		OnTypefaceSet(mTypeface);
		mFontSpinner.setSelection(mTypeface);
		
		// line leading
		mLineLeadingNumber= pref.getInt("LineLeading", 1);
		System.out.println("line leading: "+mLineLeadingNumber);
		OnLineLeadingSet(mLineLeadingNumber);
	}
	public void savePreference(){
		SharedPreferences pref = getSharedPreferences(filepath.substring(filepath.lastIndexOf('/')+1, filepath.indexOf('.')), Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putInt("Encoding", mEncodingNumber);
        editor.putInt("StartIndex", mShowTextOnCanvas.getProgress());
        editor.putInt("TextSize", mTextSize);
        editor.putInt("Background", mBackgroundThemeNumber);
        editor.putInt("LineLeading", mLineLeadingNumber);
        editor.putInt("Typeface", mTypeface);
        editor.commit();
	}
	
	public void createUI() {

		mWidth = getWindowManager().getDefaultDisplay().getWidth();
		mHeight = getWindowManager().getDefaultDisplay().getHeight();

		// widget setting
		mSettingBar = (ViewAnimator) findViewById(R.id.SettingMenu);
		mSettingBar.bringToFront();
		mSettingBar.setVisibility(View.INVISIBLE);
		mMainTopBar = (ViewAnimator) findViewById(R.id.TopSwitcher);
		mMainTopBar.bringToFront();
		mMainTopBar.setVisibility(View.INVISIBLE);
		mMainBottomBar = (ViewAnimator) findViewById(R.id.BottomSwitcher);
		mMainBottomBar.bringToFront();
		mMainBottomBar.setVisibility(View.INVISIBLE);
		mFontSetting = (ViewAnimator) findViewById(R.id.FontTypeSwitcher);
		mFontSetting.bringToFront();
		mFontSetting.setVisibility(View.INVISIBLE);
		mSearchBar = (ViewAnimator) findViewById(R.id.SearchBarSwitcher);
		mSearchBar.bringToFront();
		mSearchBar.setVisibility(View.INVISIBLE);
		mSearchResultBar = (ViewAnimator) findViewById(R.id.SearchResultSwitcher);
		mSearchResultBar.bringToFront();
		mSearchResultBar.setVisibility(View.INVISIBLE);

		mSettingButton = (ImageView) findViewById(R.id.SettingDetail);
		mSearchBtn = (ImageView) findViewById(R.id.SearchBtn);
		mSearchSelectedBtn = (ImageView) findViewById(R.id.SearchBtnSelected);
		mSearchSelectedBtn.setVisibility(View.INVISIBLE);

		mShowTextOnCanvas = (ShowTextOnCanvas) findViewById(R.id.drawingText);
		mShowTextOnCanvas.setContext(this);
		mShowTextOnCanvas.getFile(filepath);
		mShowTextOnCanvas.setOnLongClickListener(longClick);

		mTextName = (TextView) findViewById(R.id.TextName);
		mTextName.setText(filepath.substring(filepath.lastIndexOf('/') + 1));

		mFontSpinner = (Spinner) findViewById(R.id.FontTypeSettingSpinner);
		mFontSpinner.setOnItemSelectedListener(fontSelectListener);
		mFontManager = FontManager.getInstance(this);

		mProgressPercentage = (TextView) findViewById(R.id.progressPercentage);
		mProgress = (SeekBar) findViewById(R.id.ContentsSeekBar);
		mProgress.setMax(mShowTextOnCanvas.setLength());
		makeSeekbarBackground.makeInstance(CremaTextViewerActivity.this
				.getApplicationContext());
		mSeekbarBg = makeSeekbarBackground.getInstance();
		mProgress.setBackgroundDrawable(mSeekbarBg.getSeekBarBg());

		System.out.println(mShowTextOnCanvas.setLength());
		mProgress
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					public void onStopTrackingTouch(SeekBar seekBar) {
						System.out.println(seekBar.getProgress());
						
						if(mUndoList.size()>20){
							mUndoList.remove(0);
						}
						//mUndoList.add(seekBar.getProgress());
						mUndoList.add(mShowTextOnCanvas.getProgress());
						
						mShowTextOnCanvas.setProgress(seekBar.getProgress());
					}

					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						System.out.println("progress: " + progress
								+ "\nlength: " + mShowTextOnCanvas.setLength());
						System.out.println((int) (progress
								/ mShowTextOnCanvas.setLength() * 100)
								+ "%");
						mProgressPercentage.setText((int) ((double) progress
								/ (double) mShowTextOnCanvas.setLength() * 100.d)
								+ "%");
					}
				});

		mBrightness = (SeekBar) findViewById(R.id.brightnessSetting);
		mBrightness
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					public void onStopTrackingTouch(SeekBar seekBar) {
						System.out.println(seekBar.getProgress());
						SettingBrightness(seekBar.getProgress());
					}

					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						SettingBrightness(seekBar.getProgress());
					}
				});
		
		try{
			mBrightness.setProgress(android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS));
		}
		catch(SettingNotFoundException e){
			System.out.println("Can't get value");
		}

		// search view layout
		mSearchText = (EditText) findViewById(R.id.searchText);
		mSearchTextResultShort = (TextView) findViewById(R.id.SearchResultKeyword);

		mSearchContent = new SearchContent(mContext, mShowTextOnCanvas);
		mSearchListView = (ListView) findViewById(R.id.ListViewScreenSearchList);
		mSearchListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView parentView, View arg1,
							int position, long id) {
						SearchAdapter.CellItem item = (SearchAdapter.CellItem) mSearchAdapter
								.getItem(position);
						SearchList listItem = (SearchList) mSearchContent
								.setVector().get(position);

						if(mUndoList.size()>20){
							mUndoList.remove(0);
						}
						//mUndoList.add(listItem.startIndex);
						mUndoList.add(mShowTextOnCanvas.getProgress());
						
						System.out.println("undo last ("+(mUndoList.size()-1)+")"+": "+mUndoList.get(mUndoList.size()-1));
						hideButtons();
						mShowTextOnCanvas.setBarMode();
						mShowTextOnCanvas.setProgress(listItem.startIndex);
						//hideButtons();
					}
				});

		System.out.println("list view find");
		
		// create preference

		// setting item selected
		mSize01Border = (ImageView) findViewById(R.id.TextSize01Border);
		mSize02Border = (ImageView) findViewById(R.id.TextSize02Border);
		mSize03Border = (ImageView) findViewById(R.id.TextSize03Border);
		mSize04Border = (ImageView) findViewById(R.id.TextSize04Border);

		mSize01Text = (TextView) findViewById(R.id.TextSize01Text);
		mSize02Text = (TextView) findViewById(R.id.TextSize02Text);
		mSize03Text = (TextView) findViewById(R.id.TextSize03Text);
		mSize04Text = (TextView) findViewById(R.id.TextSize04Text);

		mSize01Text.setTextColor(Color.argb(50, 76, 51, 23));
		mSize02Text.setTextColor(Color.argb(50, 76, 51, 23));
		mSize03Text.setTextColor(Color.argb(100, 0, 0, 0));
		mSize04Text.setTextColor(Color.argb(50, 76, 51, 23));

		mSize01Border.setVisibility(View.INVISIBLE);
		mSize02Border.setVisibility(View.INVISIBLE);
		mSize03Border.setVisibility(View.VISIBLE);
		mSize04Border.setVisibility(View.INVISIBLE);
		
		mBackground01Selected= (ImageView)findViewById(R.id.Background01Selected);
		mBackground02Selected= (ImageView)findViewById(R.id.Background02Selected);
		mBackground03Selected= (ImageView)findViewById(R.id.Background03Selected);
		mBackground04Selected= (ImageView)findViewById(R.id.Background04Selected);

		mBackground01Border = (ImageView) findViewById(R.id.Background01Border);
		mBackground02Border = (ImageView) findViewById(R.id.Background02Border);
		mBackground03Border = (ImageView) findViewById(R.id.Background03Border);
		mBackground04Border = (ImageView) findViewById(R.id.Background04Border);

		mBackground01Text = (TextView) findViewById(R.id.Background01Text);
		mBackground02Text = (TextView) findViewById(R.id.Background02Text);
		mBackground03Text = (TextView) findViewById(R.id.Background03Text);
		mBackground04Text = (TextView) findViewById(R.id.Background04Text);
		
		mBackground01Selected.setVisibility(View.VISIBLE);
		mBackground02Selected.setVisibility(View.INVISIBLE);
		mBackground03Selected.setVisibility(View.INVISIBLE);
		mBackground04Selected.setVisibility(View.INVISIBLE);

		mBackground01Text.setTextColor(Color.argb(100, 0, 0, 0));
		mBackground02Text.setTextColor(Color.argb(50, 0, 0, 0));
		mBackground03Text.setTextColor(Color.argb(50, 0, 0, 0));
		mBackground04Text.setTextColor(Color.argb(50, 206, 199, 182));

		mBackground01Border.setVisibility(View.VISIBLE);
		mBackground02Border.setVisibility(View.INVISIBLE);
		mBackground03Border.setVisibility(View.INVISIBLE);
		mBackground04Border.setVisibility(View.INVISIBLE);

		mLineLeading01Selected = (ImageView) findViewById(R.id.LineLeading01Selected);
		mLineLeading02Selected = (ImageView) findViewById(R.id.LineLeading02Selected);
		mLineLeading03Selected = (ImageView) findViewById(R.id.LineLeading03Selected);
		mLineLeading04Selected = (ImageView) findViewById(R.id.LineLeading04Selected);

		mLineLeading01Selected.setVisibility(View.VISIBLE);
		mLineLeading02Selected.setVisibility(View.INVISIBLE);
		mLineLeading03Selected.setVisibility(View.INVISIBLE);
		mLineLeading04Selected.setVisibility(View.INVISIBLE);

		mSearchAdapter = new SearchAdapter(mContext); // , mSearchListView);
		
		mBitmapPattern[0]= BitmapFactory.decodeResource(getResources(), R.drawable.background_pattern2);
		mBitmapPattern[1]= BitmapFactory.decodeResource(getResources(), R.drawable.background_pattern3);
		mBitmapPattern[2]= BitmapFactory.decodeResource(getResources(), R.drawable.background_pattern4);
	}
	
	public void userclicked(){
		System.out.println("user clicked");
	}

	@Override
	public void onResume() {
		loadPreference();
		super.onResume();
	}

	@Override
	public void onPause() {
		savePreference();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		savePreference();
		super.onDestroy();
	}

	public View.OnKeyListener keyListener = new View.OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			System.out.println("key pressed");
			if (keyCode == EditorInfo.IME_ACTION_SEARCH
					|| keyCode == EditorInfo.IME_ACTION_DONE
					|| event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

				if (!event.isShiftPressed()) {
					Log.v("AndroidEnterKeyActivity", "Enter Key Pressed!");
					Toast.makeText(mContext, "keyword entered",
							Toast.LENGTH_SHORT).show();
					System.out.println("keyword entered");
				}
			}
			return false;
		}
	};

	OnItemSelectedListener fontSelectListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			//Typeface typeface;
			// sukdory ºˆ¡§
			int position = arg0.getSelectedItemPosition();
			if (position >= 0 && position <= mFontList.length) {
				OnTypefaceSet(position);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};
	
	public int setTypefaceNumber(){
		return mTypeface;
	}
	
	public void OnTypefaceSet(int getNumber){
		
		mTypeface= getNumber;
		System.out.println("TYPEFACE : "+mTypeface);
		DefaultFontList fontName = mFontList[mTypeface];
		try {
			Typeface typeface = FontManager.getInstance().createTypefaceFromFile(fontName);
			if (typeface == null) {
				System.out.println("typaface null");
				return;
			}
			mShowTextOnCanvas.setTypeface(typeface);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public View.OnLongClickListener longClick = new View.OnLongClickListener() {
		public boolean onLongClick(View arg0) {
			Toast.makeText(getApplicationContext(), "SELECTING MODE",
					Toast.LENGTH_SHORT).show();
			return false;
		}
	};
	
	public void test(int mode){
		if(mode == mShowTextOnCanvas.SHOW){
			System.out.println("MENU SHOW");
			showButtons();
		}
		else if(mode == mShowTextOnCanvas.HIDE){
			System.out.println("MENU HIDE");
			hideButtons();
		}
	}

	public void showButtons() {
//		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mMainTopBar.bringToFront();
		mMainBottomBar.bringToFront();
		
		anim = new TranslateAnimation(0, 0, -mMainTopBar.getHeight(), 0);
		anim.setDuration(200);
		anim.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
				System.out.println("topbar visible");
				mMainTopBar.setVisibility(View.VISIBLE);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
			}
		});
		
		mMainTopBar.startAnimation(anim);

		anim = new TranslateAnimation(0, 0, mMainBottomBar.getHeight(), 0);
		anim.setDuration(200);
		anim.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
				mMainBottomBar.setVisibility(View.VISIBLE);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
			}
		});
		mMainBottomBar.startAnimation(anim);
		mBottomBarMode = SETTING_ON;
	}

	public void hideButtons() {
		
		anim = new TranslateAnimation(0, 0, 0, -mMainTopBar.getHeight());
		anim.setDuration(200);
		anim.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				mMainTopBar.setVisibility(View.INVISIBLE);
			}
		});
		mMainTopBar.startAnimation(anim);

		anim = new TranslateAnimation(0, 0, 0, mMainBottomBar.getHeight());
		anim.setDuration(200);
		anim.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}

			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}

			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mMainBottomBar.setVisibility(View.INVISIBLE);
			}
		});
		mMainBottomBar.startAnimation(anim);
		mBottomBarMode = SETTING_OFF;

		if (mSettingMode == SETTING_ON) {
			anim = new TranslateAnimation(0, 0, 0, -mSettingBar.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					mSettingBar.setVisibility(View.INVISIBLE);
				}
			});
			mSettingBar.startAnimation(anim);
			mSettingMode = SETTING_OFF;
		}

		if (mFontTypeDropdownMode == SETTING_ON) {
			anim = new TranslateAnimation(0, 0, 0, -mFontSetting.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					mFontSetting.setVisibility(View.INVISIBLE);
				}
			});
			mFontSetting.startAnimation(anim);
			mFontTypeDropdownMode = SETTING_OFF;
		}

		if (mSearchBarMode == SETTING_ON) {
			anim = new TranslateAnimation(0, 0, 0, -mSearchBar.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					mSearchBar.setVisibility(View.INVISIBLE);
				}
			});
			mSearchBar.startAnimation(anim);
			mSearchBarMode = SETTING_OFF;
		}

		if (mSearchResultMode == SETTING_ON) {
			anim = new TranslateAnimation(0, 0, 0,
					-mSearchResultBar.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					mSearchResultBar.setVisibility(View.INVISIBLE);
				}
			});
			mSearchResultBar.startAnimation(anim);
			mSearchResultMode = SETTING_OFF;
		}
	}

	public void OnSettingClick(View v) {
		mMainTopBar.bringToFront();

		TextView mt = (TextView) findViewById(R.id.Brightness);
		System.out.println("height: " + mt.getHeight());

		switch (mSettingMode) {
		case SETTING_OFF:
			anim = new TranslateAnimation(0, 0, -mSettingBar.getHeight(), 0);
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mSettingBar.setVisibility(View.VISIBLE);
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
				}
			});
			mSettingBar.startAnimation(anim);
			mSettingMode = SETTING_ON;
			break;
		case SETTING_ON:
			anim = new TranslateAnimation(0, 0, 0, -mSettingBar.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					mSettingBar.setVisibility(View.INVISIBLE);
				}
			});
			mSettingBar.startAnimation(anim);
			mSettingMode = SETTING_OFF;
			break;
		}

		if (mSearchBarMode == SETTING_ON) {
			anim = new TranslateAnimation(0, 0, 0, -mSearchBar.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					mSearchBar.setVisibility(View.INVISIBLE);
				}
			});
			mSearchBar.startAnimation(anim);
			mSearchBarMode = SETTING_OFF;
		}

		if (mSearchResultMode == SETTING_ON) {
			anim = new TranslateAnimation(0, 0, 0,
					-mSearchResultBar.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					mSearchResultBar.setVisibility(View.INVISIBLE);
				}
			});
			mSearchResultBar.startAnimation(anim);
			mSearchResultMode = SETTING_OFF;
		}
	}

	public void OnSearchView() {
		mMainTopBar.bringToFront();
		mSearchBar.bringToFront();
		
		anim = new TranslateAnimation(0, 0, -mSearchResultBar.getHeight(), 0);
		anim.setDuration(200);
		anim.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
				mSearchResultBar.setVisibility(View.VISIBLE);
			}
			public void onAnimationRepeat(Animation animation) {}
			public void onAnimationEnd(Animation animation) {}
		});
		mSearchResultBar.startAnimation(anim);
		mSearchResultMode = SETTING_ON;

		anim = new TranslateAnimation(0, 0, 0, mMainBottomBar.getHeight());
		anim.setDuration(200);
		anim.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {}
			public void onAnimationRepeat(Animation animation) {}
			public void onAnimationEnd(Animation animation) {
				mMainBottomBar.setVisibility(View.INVISIBLE);
			}
		});
		mMainBottomBar.startAnimation(anim);
		mBottomBarMode = SETTING_OFF;
		mSearchResultBar.bringToFront();

		mSearchTextResultShort.setText(Html.fromHtml(mSearchContent.makeBold(
				mSearchText.getText().toString()
						+ getResources().getString(R.string.searchResult) + "("
						+ mSearchContent.searchResultCount + ")", mSearchText
						.getText().toString())));
		
		mMainTopBar.bringToFront();
		mSearchBar.bringToFront();
	}
	
	public void OnTextSizeClicked(View v){
		switch(v.getId()){
		case R.id.TextSize01:
			OnTextSizeSet(1);
			break;
		case R.id.TextSize02:
			OnTextSizeSet(2);
			break;
		case R.id.TextSize03:
			OnTextSizeSet(3);
			break;
		case R.id.TextSize04:
			OnTextSizeSet(4);
			break;
		}
	}

	public void OnTextSizeSet(int getNumber) {
		mTextSize= getNumber;
		switch (getNumber) {
		case 1:
			mShowTextOnCanvas.getTextSize(30);
			mSize01Border.setVisibility(View.VISIBLE);
			mSize02Border.setVisibility(View.INVISIBLE);
			mSize03Border.setVisibility(View.INVISIBLE);
			mSize04Border.setVisibility(View.INVISIBLE);
			mSize01Text.setTextColor(Color.argb(100, 0, 0, 0));
			mSize02Text.setTextColor(Color.argb(50, 76, 51, 23));
			mSize03Text.setTextColor(Color.argb(50, 76, 51, 23));
			mSize04Text.setTextColor(Color.argb(50, 76, 51, 23));
			break;
		case 2:
			mShowTextOnCanvas.getTextSize(35);
			mSize01Border.setVisibility(View.INVISIBLE);
			mSize02Border.setVisibility(View.VISIBLE);
			mSize03Border.setVisibility(View.INVISIBLE);
			mSize04Border.setVisibility(View.INVISIBLE);
			mSize01Text.setTextColor(Color.argb(50, 76, 51, 23));
			mSize02Text.setTextColor(Color.argb(100, 0, 0, 0));
			mSize03Text.setTextColor(Color.argb(50, 76, 51, 23));
			mSize04Text.setTextColor(Color.argb(50, 76, 51, 23));
			break;
		case 3:
			mShowTextOnCanvas.getTextSize(40);
			mSize01Border.setVisibility(View.INVISIBLE);
			mSize02Border.setVisibility(View.INVISIBLE);
			mSize03Border.setVisibility(View.VISIBLE);
			mSize04Border.setVisibility(View.INVISIBLE);
			mSize01Text.setTextColor(Color.argb(50, 76, 51, 23));
			mSize02Text.setTextColor(Color.argb(50, 76, 51, 23));
			mSize03Text.setTextColor(Color.argb(100, 0, 0, 0));
			mSize04Text.setTextColor(Color.argb(50, 76, 51, 23));
			break;
		case 4:
			mShowTextOnCanvas.getTextSize(45);
			mSize01Border.setVisibility(View.INVISIBLE);
			mSize02Border.setVisibility(View.INVISIBLE);
			mSize03Border.setVisibility(View.INVISIBLE);
			mSize04Border.setVisibility(View.VISIBLE);
			mSize01Text.setTextColor(Color.argb(50, 76, 51, 23));
			mSize02Text.setTextColor(Color.argb(50, 76, 51, 23));
			mSize03Text.setTextColor(Color.argb(50, 76, 51, 23));
			mSize04Text.setTextColor(Color.argb(100, 0, 0, 0));
			break;
		}
	}
	
	public void OnBackgroundClicked(View v){
		switch(v.getId()){
		case R.id.Background01:
			OnBackgroundSet(1);
			break;
		case R.id.Background02:
			OnBackgroundSet(2);
			break;
		case R.id.Background03:
			OnBackgroundSet(3);
			break;
		case R.id.Background04:
			OnBackgroundSet(4);
			break;
		}
	}

//	private Bitmap[] mBackgroundPattern={
//			BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.background_pattern2),
//			BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.background_pattern3),
//			BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.background_pattern4),
//	};
	
	public void OnBackgroundSet(int getNumber) {
		mBackgroundThemeNumber= getNumber;
		System.out.println("background: "+getNumber+ "/ "+mBackgroundThemeNumber);
		switch (getNumber) {
		case 1:
			//mShowTextOnCanvas.getBackgroundPattern(getNumber);
			mShowTextOnCanvas.getBitmap(null, Color.BLACK);
			mBackground01Selected.setVisibility(View.VISIBLE);
			mBackground02Selected.setVisibility(View.INVISIBLE);
			mBackground03Selected.setVisibility(View.INVISIBLE);
			mBackground04Selected.setVisibility(View.INVISIBLE);
			mBackground01Border.setVisibility(View.VISIBLE);
			mBackground02Border.setVisibility(View.INVISIBLE);
			mBackground03Border.setVisibility(View.INVISIBLE);
			mBackground04Border.setVisibility(View.INVISIBLE);
			mBackground01Text.setTextColor(Color.rgb(0, 0, 0));
			mBackground02Text.setTextColor(Color.argb(50, 0, 0, 0));
			mBackground03Text.setTextColor(Color.argb(50, 0, 0, 0));
			mBackground04Text.setTextColor(Color.argb(70, 206, 199, 182));
			break;
		case 2:
			//mShowTextOnCanvas.getBackgroundPattern(getNumber);
			mShowTextOnCanvas.getBitmap(mBitmapPattern[0], Color.BLACK);
			mBackground01Selected.setVisibility(View.INVISIBLE);
			mBackground02Selected.setVisibility(View.VISIBLE);
			mBackground03Selected.setVisibility(View.INVISIBLE);
			mBackground04Selected.setVisibility(View.INVISIBLE);
			mBackground01Border.setVisibility(View.INVISIBLE);
			mBackground02Border.setVisibility(View.VISIBLE);
			mBackground03Border.setVisibility(View.INVISIBLE);
			mBackground04Border.setVisibility(View.INVISIBLE);
			mBackground01Text.setTextColor(Color.argb(50, 0, 0, 0));
			mBackground02Text.setTextColor(Color.rgb(0, 0, 0));
			mBackground03Text.setTextColor(Color.argb(50, 0, 0, 0));
			mBackground04Text.setTextColor(Color.argb(70, 206, 199, 182));
			break;
		case 3:
			//mShowTextOnCanvas.getBackgroundPattern(getNumber);
			mShowTextOnCanvas.getBitmap(mBitmapPattern[1], Color.BLACK);
			mBackground01Selected.setVisibility(View.INVISIBLE);
			mBackground02Selected.setVisibility(View.INVISIBLE);
			mBackground03Selected.setVisibility(View.VISIBLE);
			mBackground04Selected.setVisibility(View.INVISIBLE);
			mBackground01Border.setVisibility(View.INVISIBLE);
			mBackground02Border.setVisibility(View.INVISIBLE);
			mBackground03Border.setVisibility(View.VISIBLE);
			mBackground04Border.setVisibility(View.INVISIBLE);
			mBackground01Text.setTextColor(Color.argb(50, 0, 0, 0));
			mBackground02Text.setTextColor(Color.argb(50, 0, 0, 0));
			mBackground03Text.setTextColor(Color.rgb(0, 0, 0));
			mBackground04Text.setTextColor(Color.argb(70, 206, 199, 182));
			break;
		case 4:
			//mShowTextOnCanvas.getBackgroundPattern(getNumber);
			mShowTextOnCanvas.getBitmap(mBitmapPattern[2], Color.rgb(206, 199, 182));
			mBackground01Selected.setVisibility(View.INVISIBLE);
			mBackground02Selected.setVisibility(View.INVISIBLE);
			mBackground03Selected.setVisibility(View.INVISIBLE);
			mBackground04Selected.setVisibility(View.VISIBLE);
			mBackground01Border.setVisibility(View.INVISIBLE);
			mBackground02Border.setVisibility(View.INVISIBLE);
			mBackground03Border.setVisibility(View.INVISIBLE);
			mBackground04Border.setVisibility(View.VISIBLE);
			mBackground01Text.setTextColor(Color.argb(50, 0, 0, 0));
			mBackground02Text.setTextColor(Color.argb(50, 0, 0, 0));
			mBackground03Text.setTextColor(Color.argb(50, 0, 0, 0));
			mBackground04Text.setTextColor(Color.rgb(206, 199, 182));
			break;
		}
	}
	
	public void OnLineLeadingClicked(View v){
		switch(v.getId()){
		case R.id.LineLeading01:
			OnLineLeadingSet(1);
			break;
		case R.id.LineLeading02:
			OnLineLeadingSet(2);
			break;
		case R.id.LineLeading03:
			OnLineLeadingSet(3);
			break;
		case R.id.LineLeading04:
			OnLineLeadingSet(4);
			break;
		}
	}

	public void OnLineLeadingSet(int getNumber) {
		mLineLeadingNumber= getNumber;
		switch (getNumber) {
		case 1:
			mShowTextOnCanvas.getLineLeading(1);
			mLineLeading01Selected.setVisibility(View.VISIBLE);
			mLineLeading02Selected.setVisibility(View.INVISIBLE);
			mLineLeading03Selected.setVisibility(View.INVISIBLE);
			mLineLeading04Selected.setVisibility(View.INVISIBLE);
			break;
		case 2:
			mShowTextOnCanvas.getLineLeading(2);
			mLineLeading01Selected.setVisibility(View.INVISIBLE);
			mLineLeading02Selected.setVisibility(View.VISIBLE);
			mLineLeading03Selected.setVisibility(View.INVISIBLE);
			mLineLeading04Selected.setVisibility(View.INVISIBLE);
			break;
		case 3:
			mShowTextOnCanvas.getLineLeading(3);
			mLineLeading01Selected.setVisibility(View.INVISIBLE);
			mLineLeading02Selected.setVisibility(View.INVISIBLE);
			mLineLeading03Selected.setVisibility(View.VISIBLE);
			mLineLeading04Selected.setVisibility(View.INVISIBLE);
			break;
		case 4:
			mShowTextOnCanvas.getLineLeading(4);
			mLineLeading01Selected.setVisibility(View.INVISIBLE);
			mLineLeading02Selected.setVisibility(View.INVISIBLE);
			mLineLeading03Selected.setVisibility(View.INVISIBLE);
			mLineLeading04Selected.setVisibility(View.VISIBLE);
			break;
		}
	}

	// Encoding popup
	public void OnEncodingPopup(View v) {
		View popupView = getLayoutInflater().inflate(R.layout.encoding_popup,
				null);
		mPopupWindow = new PopupWindow(popupView,
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		mPopupWindow.setContentView(popupView);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
		mPopupWindow.showAsDropDown(v);
	}

	public void OnSearchPopup(View v) {

		mMainTopBar.bringToFront();

		switch (mSearchBarMode) {
		case SETTING_OFF:
			anim = new TranslateAnimation(0, 0, -mSearchBar.getHeight(), 0);
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mSearchBar.setVisibility(View.VISIBLE);
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
				}
			});
			mSearchBar.startAnimation(anim);
			mSearchBarMode = SETTING_ON;
			
			if (mSettingMode == SETTING_ON) {
				anim = new TranslateAnimation(0, 0, 0, -mSettingBar.getHeight());
				anim.setDuration(200);
				anim.setAnimationListener(new Animation.AnimationListener() {
					public void onAnimationStart(Animation animation) {
					}

					public void onAnimationRepeat(Animation animation) {
					}

					public void onAnimationEnd(Animation animation) {
						mSettingBar.setVisibility(View.INVISIBLE);
					}
				});
				mSettingBar.startAnimation(anim);
				mSettingMode = SETTING_OFF;
			}

			if (mFontTypeDropdownMode == SETTING_ON) {
				anim = new TranslateAnimation(0, 0, 0, -mFontSetting.getHeight());
				anim.setDuration(200);
				anim.setAnimationListener(new Animation.AnimationListener() {
					public void onAnimationStart(Animation animation) {
					}

					public void onAnimationRepeat(Animation animation) {
					}

					public void onAnimationEnd(Animation animation) {
						mFontSetting.setVisibility(View.INVISIBLE);
					}
				});
				mFontSetting.startAnimation(anim);
				mFontTypeDropdownMode = SETTING_OFF;
			}
			
			if (mSearching == SETTING_ON && mSearchResultMode == SETTING_OFF) {
				System.out.println("search result mode == setting off && search text is not null");
				anim = new TranslateAnimation(0, 0, -mSearchResultBar.getHeight(),
						0);
				anim.setDuration(200);
				anim.setAnimationListener(new Animation.AnimationListener() {
					public void onAnimationStart(Animation animation) {
						mSearchResultBar.setVisibility(View.VISIBLE);
					}

					public void onAnimationRepeat(Animation animation) {
					}

					public void onAnimationEnd(Animation animation) {
					}
				});
				mSearchResultBar.startAnimation(anim);
				mSearchResultMode = SETTING_ON;
			}
			break;
		case SETTING_ON:
			anim = new TranslateAnimation(0, 0, 0, -mSearchBar.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					mSearchBar.setVisibility(View.INVISIBLE);
				}
			});
			mSearchBar.startAnimation(anim);
			mSearchBarMode = SETTING_OFF;
			
			if (mSearchResultMode == SETTING_ON) {
				anim = new TranslateAnimation(0, 0, 0,
						-mSearchResultBar.getHeight());
				anim.setDuration(200);
				anim.setAnimationListener(new Animation.AnimationListener() {
					public void onAnimationStart(Animation animation) {
					}

					public void onAnimationRepeat(Animation animation) {
					}

					public void onAnimationEnd(Animation animation) {
						mSearchResultBar.setVisibility(View.INVISIBLE);
					}
				});
				mSearchResultBar.startAnimation(anim);
				mSearchResultMode = SETTING_OFF;
				
				anim = new TranslateAnimation(0, 0, mMainBottomBar.getHeight(), 0);
				anim.setDuration(200);
				anim.setAnimationListener(new Animation.AnimationListener() {
					public void onAnimationStart(Animation animation) {
						mMainBottomBar.setVisibility(View.VISIBLE);
					}

					public void onAnimationRepeat(Animation animation) {
					}

					public void onAnimationEnd(Animation animation) {
					}
				});
				mMainBottomBar.startAnimation(anim);
				mBottomBarMode = SETTING_ON;
			}
			break;
		}

		forSearch();
	}

	public void forSearch() {
		mSearchText.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					Toast.makeText(getApplicationContext(), v.getText(),
							Toast.LENGTH_SHORT).show();
					System.out
							.println("search text: " + v.getText().toString());
					mSearchContent.searchInContent(v.getText().toString());
					mSearchAdapter.refreshPage(mSearchContent.setVector());
					mSearchListView.setAdapter(mSearchAdapter);
					mSearching= SETTING_ON;
					OnSearchView();
				}
				return false;
			}
		});
		mSearchText.requestFocus();
		mSearchText.setSelection(0);
	}

	public void OnSearchDeleteClicked(View v) {
		mSearchText.setText("");
		System.out.println("delete clicked");
		
		mSearching= SETTING_OFF;
		
		if (mSearchResultMode == SETTING_ON) {
			anim = new TranslateAnimation(0, 0, 0,
					-mSearchResultBar.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					mSearchResultBar.setVisibility(View.INVISIBLE);
				}
			});
			mSearchResultBar.startAnimation(anim);
			mSearchResultMode = SETTING_OFF;
			
			anim = new TranslateAnimation(0, 0, mMainBottomBar.getHeight(), 0);
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mMainBottomBar.setVisibility(View.VISIBLE);
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
				}
			});
			mMainBottomBar.startAnimation(anim);
			mBottomBarMode = SETTING_ON;
		}
	}

	public void OnCancelClicked(View v) {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
	}

	public void OnFontTypeClick(View v) {
		mMainTopBar.bringToFront();
		mSettingBar.bringToFront();

		switch (mFontTypeDropdownMode) {
		case SETTING_OFF:
			anim = new TranslateAnimation(0, 0, -mFontSetting.getHeight(), 0);
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mFontSetting.setVisibility(View.VISIBLE);
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
				}
			});
			mFontSetting.startAnimation(anim);
			mFontTypeDropdownMode = SETTING_ON;
			break;
		case SETTING_ON:
			anim = new TranslateAnimation(0, 0, 0, -mFontSetting.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					mFontSetting.setVisibility(View.INVISIBLE);
				}
			});
			mFontSetting.startAnimation(anim);
			mFontTypeDropdownMode = SETTING_OFF;
			break;
		}
	}

	public void upDatePageNumberView(int index) {
		mProgressPercentage.setText((int) index / mShowTextOnCanvas.setLength()
				* 100 + "%");
		// mProgress.setProgress((int)index/mShowTextOnCanvas.setLength()*100);
	}

	public void getProgress(int index) {
		mProgress.setProgress(index / mShowTextOnCanvas.setLength());
	}

	public void SettingBrightness(int index) {
		android.provider.Settings.System.putInt(getContentResolver(),
				"screen_brightness", index);
	}
	
	public void OnUndoClicked(View v){
		System.out.println("size: "+mUndoList.size());
		if(!mUndoList.isEmpty()){
			int position= mUndoList.get(mUndoList.size()-1);
			System.out.println("position: "+position);
			if(mUndoList.size()-1 == 0)
				mUndoList.clear();
			else{
				mUndoList.remove(mUndoList.size()-1);
				System.out.println("mUndoList last: "+mUndoList.get(mUndoList.size()-1));
			}
			mShowTextOnCanvas.setProgress(position);
		}
	}
	
	public void OnEncodingClick(View v){
		switch (v.getId()) {
		case R.id.encoding01:
			OnEncodingSet(1);
			break;
		case R.id.encoding02:
			OnEncodingSet(2);
			break;
		case R.id.encoding03:
			OnEncodingSet(3);
			break;
		case R.id.encoding04:
			OnEncodingSet(4);
			break;
		case R.id.encoding05:
			OnEncodingSet(5);
			break;
		case R.id.encoding06:
			OnEncodingSet(6);
			break;
		case R.id.encoding07:
			OnEncodingSet(7);
			break;
		case R.id.encoding08:
			OnEncodingSet(8);
			break;
		case R.id.encoding09:
			OnEncodingSet(9);
			break;
		case R.id.encoding10:
			OnEncodingSet(10);
			break;
		case R.id.encoding11:
			OnEncodingSet(11);
			break;
		case R.id.encoding12:
			OnEncodingSet(12);
			break;
		case R.id.encoding13:
			OnEncodingSet(13);
			break;
		case R.id.encoding14:
			OnEncodingSet(14);
			break;
		case R.id.encoding15:
			OnEncodingSet(15);
			break;
		case R.id.encoding16:
			OnEncodingSet(16);
			break;
		case R.id.encoding17:
			OnEncodingSet(17);
			break;
		case R.id.encoding18:
			OnEncodingSet(18);
			break;
		case R.id.encoding19:
			OnEncodingSet(19);
			break;
		case R.id.encoding20:
			OnEncodingSet(20);
			break;
		case R.id.encoding21:
			OnEncodingSet(21);
			break;
		case R.id.encoding22:
			OnEncodingSet(22);
			break;
		case R.id.encoding23:
			OnEncodingSet(23);
			break;
		case R.id.encoding24:
			OnEncodingSet(24);
			break;
		case R.id.encoding25:
			OnEncodingSet(25);
			break;
		case R.id.encoding26:
			OnEncodingSet(26);
			break;
		case R.id.encoding27:
			OnEncodingSet(27);
			break;
		case R.id.encoding28:
			OnEncodingSet(28);
			break;
		}
		mPopupWindow.dismiss();
	}

	public void OnEncodingSet(int getNumber) {
		mEncodingNumber= getNumber;
		switch (getNumber) {
		case 1:
			mShowTextOnCanvas.setEncoder("utf-8");
			break;
		case 2:
			mShowTextOnCanvas.setEncoder("euc-kr");
			break;
		case 3:
			mShowTextOnCanvas.setEncoder("shift_jis");
			break;
		case 4:
			mShowTextOnCanvas.setEncoder("utf-16");
			break;
		case 5:
			mShowTextOnCanvas.setEncoder("euc-jp");
			break;
		case 6:
			mShowTextOnCanvas.setEncoder("big5");
			break;
		case 7:
			mShowTextOnCanvas.setEncoder("iso-2022-jp");
			break;
		case 8:
			mShowTextOnCanvas.setEncoder("iso-2022-cn");
			break;
		case 9:
			mShowTextOnCanvas.setEncoder("iso-2022-kr");
			break;
		case 10:
			mShowTextOnCanvas.setEncoder("iso-8859-1");
			break;
		case 11:
			mShowTextOnCanvas.setEncoder("iso-8859-2");
			break;
		case 12:
			mShowTextOnCanvas.setEncoder("iso-8859-3");
			break;
		case 13:
			mShowTextOnCanvas.setEncoder("iso-8859-4");
			break;
		case 14:
			mShowTextOnCanvas.setEncoder("iso-8859-5");
			break;
		case 15:
			mShowTextOnCanvas.setEncoder("iso-8859-6");
			break;
		case 16:
			mShowTextOnCanvas.setEncoder("iso-8859-7");
			break;
		case 17:
			mShowTextOnCanvas.setEncoder("iso-8859-8");
			break;
		case 18:
			mShowTextOnCanvas.setEncoder("iso-8859-9");
			break;
		case 19:
			mShowTextOnCanvas.setEncoder("koi8-r");
			break;
		case 20:
			mShowTextOnCanvas.setEncoder("koi8-u");
			break;
		case 21:
			mShowTextOnCanvas.setEncoder("utf-16be");
			break;
		case 22:
			mShowTextOnCanvas.setEncoder("utf-16le");
			break;
		case 23:
			mShowTextOnCanvas.setEncoder("big5-hkscs");
			break;
		case 24:
			mShowTextOnCanvas.setEncoder("cesu-8");
			break;
		case 25:
			mShowTextOnCanvas.setEncoder("cp864");
			break;
		case 26:
			mShowTextOnCanvas.setEncoder("gb18030");
			break;
		case 27:
			mShowTextOnCanvas.setEncoder("gbk");
			break;
		case 28:
			mShowTextOnCanvas.setEncoder("hz-gb-2312");
			break;
		}
	}

	public void onNothing(View v) {
	}
}