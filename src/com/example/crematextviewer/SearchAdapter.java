package com.example.crematextviewer;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SearchAdapter extends BaseAdapter {
	private Context mContext;
	private int _selectedIndex=-1;
	
	private Vector<CellItem> vItems= new Vector<CellItem>();
	
	public class CellItem {
		public int searchIndex= -1;
		public int searchPosition= -1;
		public int searchStartIndex= -1;
		public int searchEndIndex= -1;
		public String stringForSearchText= null;
		public String stringForSearchContent= null;
		public double searchItemIndexPercentage= -1;
		
		public CellItem() {
			searchIndex = -1;
		}
		
		public CellItem(int position, int startIndex, int endIndex, String searchText, String searchContent, double indexPercentage) {
			searchPosition= position;
			searchStartIndex= startIndex;
			searchEndIndex= endIndex;
			stringForSearchText= searchText;
			stringForSearchContent= searchContent;
			searchItemIndexPercentage= indexPercentage;
		}
	}
	private class CellRendererView extends LinearLayout {
		private TextView mSearchContent;
		private TextView mSearchPosition;

		public CellRendererView() {
			super( mContext);
			createUI();
		}
		
		protected void createUI() {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();			
			LinearLayout linearLayout = (LinearLayout )inflater.inflate(R.layout.search_content_layout, this);
			mSearchContent = (TextView)linearLayout.findViewById(R.id.SearchItemContent);
			mSearchPosition = (TextView)linearLayout.findViewById(R.id.SearchItemPosition);
		}
		
		public void display(int index, boolean selected) {
			CellItem item = vItems.get(index);

			//mSearchContent.setText(item.stringForSearchContent);
			mSearchContent.setText(Html.fromHtml(item.stringForSearchContent));
			mSearchPosition.setText(Integer.toString((int)Math.floor(item.searchItemIndexPercentage))+"%");
		}
		
	} // CellRendererView

	
	public SearchAdapter(Context a) {
		this.mContext = a;
	}
	
	public void refreshPage(Vector items) {
		int len = items.size();
		
		this.vItems.clear();
		CellItem ci;
		SearchList item;
		System.out.println("refresh page vector");
		for ( int i=0; i<len; i++) {
			ci = new CellItem();
			item = (SearchList)items.get(i);
			if( item != null ) {
				ci.searchStartIndex= item.startIndex;
				ci.searchEndIndex= item.endIndex;
				ci.searchItemIndexPercentage= item.indexPercentage;
				ci.stringForSearchText= item.searchText;
				ci.stringForSearchContent= item.searchContent;
			}
			System.out.println(i);
			vItems.add( ci );
		}
	}
	
	@Override
	public int getCount() {
		return this.vItems.size();
	}

	@Override
	public Object getItem(int position) {
		return vItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return true;
		
	}

	@Override
	public View getView(int position, View cellRenderer, ViewGroup parent) {
		CellRendererView cellRendererView = null;

		if (cellRenderer == null) {
			cellRendererView = new CellRendererView();
		} else {
			cellRendererView = (CellRendererView) cellRenderer;
		}
		
		// update the cell renderer, and handle selection state
		cellRendererView.display(position, _selectedIndex == position);

		return cellRendererView;
	}

}

