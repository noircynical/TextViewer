package com.example.crematextviewer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TextPreference {
	
	int startIndex;
	int textSize;
	int BackgroundSet;
	int lineLeading;
	
//	public class ViewerSetting{
//		int startIndex;
//		int textSize;
//		int BackgroundSet;
//		// font
//		int lineLeading;
//		// encoding
//	}
//	
//	ViewerSetting viewSet= new ViewerSetting();
	
//	public PreferenceItem copyClone(int id) {
//		PreferenceItem clone = new PreferenceItem(id);
//		String json = this.jsonCombine();
//		clone.jsonParsor(json);
//		
//		return clone;
//	}
	
	public TextPreference(String textName){
		startIndex= 0;
		textSize= 25;
		BackgroundSet= 1;
		lineLeading= 1;
	}

	public String jsonCombine() {
		JSONArray array = new JSONArray();
		JSONObject obj = new JSONObject();
		try{
			obj.put("StartIndex", startIndex);
			obj.put("TextSize", textSize);
			obj.put("BackgroundSet", BackgroundSet);
			//obj.put("FontSet", fontSet.getValue());
			obj.put("LineLeading", lineLeading);
			array.put(obj);
			//Log.i("json Combine", array.toString());
			System.out.println(array.toString());
			
			return array.toString();
		}catch(Exception e){
			//Log.e("JSON Combine",":::::array Error "+e.toString());
			System.out.println("json combile array error: "+e.toString());
		}
		return null;
	}

	public void jsonParsor(String json){
		try {
			JSONArray ja  = new JSONArray(json);

		    JSONObject jsonRoot =ja.getJSONObject(0);
		    startIndex= jsonRoot.getInt("StartIndex");
		    textSize= jsonRoot.getInt("TextSize");
		    BackgroundSet= jsonRoot.getInt("BackgroundSet");
		    lineLeading= jsonRoot.getInt("LineLeading");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}
	
	public void setStartIndex(int startindex){
		startIndex= startindex;
	}
	public void setTextSize(int textsize){
		textSize= textsize;
	}
	public void setBackgroundSet(int background){
		BackgroundSet= background;
	}
	public void setLineLeading(int lineleading){
		lineLeading= lineleading;
	}
	
	public int getStartIndex(){
		return startIndex;
	}
	public int getTextSize(){
		return textSize;
	}
	public int getBackgroundSet(int background){
		return BackgroundSet;
	}
	public int getLineLeading(){
		return lineLeading;
	}
}
