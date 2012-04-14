package com.eli.filemanager;

import com.eli.filemanager.pojo.Files;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends Activity{

	ProcessSearch process;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		process = new ProcessSearch(this);
	}
	
	public OnItemClickListener itemClick() {
		OnItemClickListener clickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Files object = (Files) parent.getItemAtPosition(position);
				if (object.isFolder()) {
					Intent intent = new Intent(SearchActivity.this,ListActivity.class);
					intent.putExtra("isSearch", true);
					intent.putExtra("src", object.getName());
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
