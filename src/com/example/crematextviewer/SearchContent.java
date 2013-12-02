package com.example.crematextviewer;

import java.util.List;
import java.util.Vector;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.crematextviewer.*;

public class SearchContent {
	
	private final int NORMAL= 0;
	private final int SMALL_POINT= 1;
	private final int BIG_POINT= 2;

	private Context mContext = null;
	public ShowTextOnCanvas mShowTextOnCanvas;

	public int searchResultCount = 0;
	//public SearchAdapter mSearchAdapter = null;
	public SearchList mSearchList = null;

	public ListView mListView;

	public Vector mSearchVector = new Vector();
	
	public int sentenceFlag=NORMAL;

	private ProgressDialog proDial;

	public SearchContent(Context context, ShowTextOnCanvas textCanvas) {
		mContext = context;
		mShowTextOnCanvas = textCanvas;
	}

	public void refreshPage() {
		// int cnt = searchResultCount;
		// Log.i(this.getClass().getName(), "MODE_CONTENT : " + cnt);
		// if (cnt == 0) {
		// textViewResult.setText(getResources().getString(R.string.viewer_text_search_empty));
		// }
		// else {
		// textViewResult.setText(Html.fromHtml(getResources().getString(R.string.viewer_text_search_result,
		// textSearch, String.valueOf(cnt))));
		// }
		// mAdapterContent.refreshPage(mAItem);
		// mAdapterContent.notifyDataSetChanged();
	}

	public void searchInContent(final String keyword) {
		searchResultCount = 0;
		mSearchVector.clear();
		if (keyword == null) {
			// refreshPage();
			return;
		}
		if (keyword.length() == 0) {
			// refreshPage();
			return;
		}
//
//		System.out.println("for search, function entered");
//
//		System.out.println("make progress dialog");

		int i, j;
		for (i = 0; i < (mShowTextOnCanvas.setLength() - keyword.length()); i++) {
			if (mShowTextOnCanvas.setString().charAt(i) == keyword.charAt(0)) {
				if (keyword.equals(mShowTextOnCanvas.setString().substring(i, i + keyword.length()))
						|| keyword.toLowerCase().equals(mShowTextOnCanvas.setString().substring(i, i + keyword.length()).toLowerCase())) {
//					System.out.println("index: " + i);
//					System.out.println(keyword + ": " + keyword.length());
//					System.out.println(mShowTextOnCanvas.setString().substring(i,i + keyword.length())+ ": "
//							+ mShowTextOnCanvas.setString().substring(i, i + keyword.length()).length());
					searchResultCount++;

					SearchList mSearchItem = new SearchList();

					for (j = i; j >= 0; j--) {
						if (j == 0)
							break;
						else if (mShowTextOnCanvas.setString().charAt(j) == '.'
								|| mShowTextOnCanvas.setString().charAt(j) == '\n') {
							j++;
							break;
						}
					}

					mSearchItem.startIndex = j;
					mSearchItem.index = i;

					for (j = i + keyword.length() + 1; j < mShowTextOnCanvas.setLength() - 1; j++) {
						//if (mShowTextOnCanvas.setString().charAt(j) == '.' || mShowTextOnCanvas.setString().charAt(j) == '\n') {
						if(mShowTextOnCanvas.setString().charAt(j) == '.'){
							i = j+2;
							break;
						}
						else if(mShowTextOnCanvas.setString().charAt(j) == '\n'){
							i= j+2;
							break;
						}
					}
					mSearchItem.endIndex = j+1;
					mSearchItem.searchContent = mShowTextOnCanvas.setString()
							.substring(mSearchItem.startIndex,
									mSearchItem.endIndex);
					
					mSearchItem.searchContent= makeBold(mSearchItem.searchContent, keyword);
					mSearchItem.indexPercentage= (double)mSearchItem.index/(double)mShowTextOnCanvas.setLength()*100;
					//System.out.println(mSearchItem.searchContent);

					mSearchVector.add(mSearchItem);
				}
			}
		}
		// for search

		System.out.println("result: " + mSearchVector.size());
		Toast.makeText(mContext, "result: " + mSearchVector.size(),
				Toast.LENGTH_SHORT).show();
	}
	
	public String makeBold(String text, String keyword) {
		final String BS = "<b><u>";
		final String BE = "</b></u>";
		StringBuffer sb = new StringBuffer();
		
		int idx = text.indexOf(keyword);
		if (idx == -1) return text;
		idx = 0;
		int pIdx = 0;
		
		while ((idx = text.indexOf(keyword, pIdx)) != -1) {
			sb.append(text.substring(pIdx, idx));
			sb.append(BS);
			sb.append(keyword);
			sb.append(BE);
			pIdx = idx+keyword.length();
		}
		if (pIdx < text.length()) {
			sb.append(text.substring(pIdx, text.length()));
		}
		
		return sb.toString();
	}

	public void getListView(ListView lv) {
		mListView = lv;
	}

	public Vector setVector() {
		return mSearchVector;
	}
}
