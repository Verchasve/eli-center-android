package com.eli.filemanager;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.eli.filemanager.R;
import com.eli.filemanager.dao.LoadSetting;
import com.eli.filemanager.pojo.Files;
import com.eli.filemanager.pojo.Users;
import com.eli.util.Util;

public class ListActivitySmb extends Activity {

	ProcessFileSmb process;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		initLocale();
		setContentView(R.layout.list);
		process = new ProcessFileSmb(this);
	}
	
	public void initLocale(){
		String languageToLoad = Util.locale(LoadSetting.users.getLanguage());  
	    Locale locale = new Locale(languageToLoad);   
	    Locale.setDefault(locale);  
	    Configuration config = new Configuration();  
	    config.locale = locale;  
	    getBaseContext().getResources().updateConfiguration(config,   
	    getBaseContext().getResources().getDisplayMetrics());  
	}
	
	public void destroy(){
		finish();
	}


	private void refreshAdapter() {
		process.fileAdapter.clear();
		if (process.list.size() > 0) {
			for (int i = 0; i < process.list.size(); i++) {
				process.fileAdapter.add(process.list.get(i));
			}
		}
		process.fileAdapter.notifyDataSetChanged();
	}

	public void refresh() {
		try {
			String src = "";
			if (process.paths.size() > 0) {
				for (int i = 0; i < process.paths.size(); i++) {
					src += File.separator + process.paths.get(i);
				}
			} else {
				src = File.separator;
			}
			process.getAllListFile(src);
			refreshAdapter();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (process.paths.size() == 0) {
				finish();
				return true;
			} else {
				process.back_btn.performClick();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public OnItemClickListener itemClick() {
		OnItemClickListener clickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					Files object = (Files) parent.getItemAtPosition(position);					
					if (object.isFolder()) {
						process.paths.add(object.getName());
						refresh();
					} else {
						Intent intent = object.getAction();
						startActivity(intent);
					}
				}
		};
		return clickListener;
	}
}
