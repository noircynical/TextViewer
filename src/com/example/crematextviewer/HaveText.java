package com.example.crematextviewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import android.content.Context;
import android.widget.Toast;

import com.example.crematextviewer.*;

public class HaveText {
	
	private String mEncoding;
	public String fileString;
	private String filePath;
	
	private int letterCount;

	public void HaveText(){
	}
	
	public String getString(){
		return fileString;
	}
	
	public int getLetterCount(){
		return fileString.length();
	}
	
	public void setEncoder(String encoding) {
		mEncoding = encoding;
		fileString = fileReading(new File(filePath));
		letterCount = fileString.length();
	}

	private String fileReading(File mFile) {
		StringBuilder stBuilder = new StringBuilder();

		try {
			if (!mFile.isFile()) {
				System.out.println("NO FILE IN PATH");
			} else {

				try {

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
