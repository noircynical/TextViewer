package com.example.crematextviewer;

public class SearchList {
	public int index = -1;
	public int startIndex= -1;
	public int endIndex= -1;
	public double indexPercentage= -1;
	public String searchText = null;
	public String searchContent= null;
	
	public SearchList() {
		//makeStringChapter();
	}
	
	public SearchList(int startIndex, int endIndex, double indexPercentage, String searchText, String searchContent) {
		this.startIndex= startIndex;
		this.endIndex= endIndex;
		this.indexPercentage= indexPercentage;
		this.searchText= searchText;
		this.searchContent= searchContent;
		
		//makeStringChapter();
	}
	
	public void makeStringChapter(int chapter, int position) {
		//this.sChapter = "Chapter " + String.valueOf(chapter)+ ", " + String.valueOf(position) + "%";
	}
	
	public void makeStringChapter() {
		//this.sChapter = "Chapter " + String.valueOf(chapter)+ ", " + String.valueOf(position) + "%";
	}
}
