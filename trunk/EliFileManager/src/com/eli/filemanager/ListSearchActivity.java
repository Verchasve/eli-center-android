package com.eli.filemanager;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

import com.eli.filemanager.pojo.Files;
import com.eli.util.Util;

public class ListSearchActivity extends Activity{

	ListView list_search;
	ListFileAdapter fileAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_search);		
		list_search = (ListView)findViewById(R.id.list_search);
		fileAdapter = new ListFileAdapter(this, R.layout.list_detail,ProcessSearch.array, true);
		list_search.setAdapter(fileAdapter);
		list_search.setOnItemClickListener(onItemClick());
	}
	
	public OnItemClickListener onItemClick(){
		OnItemClickListener clickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Files object = (Files) parent.getItemAtPosition(position);
				if (object.isFolder()) {
					System.out.println(object.getChildFile());
					Intent intent = new Intent(ListSearchActivity.this,ListActivity.class);
					intent.putExtra("isSearch", true);
					intent.putExtra("src", object.getChildFile());
					startActivity(intent);
				} else {
					Intent intent = object.getAction();
					startActivity(intent);
				}
			}
		};
		return clickListener;
	}
}
