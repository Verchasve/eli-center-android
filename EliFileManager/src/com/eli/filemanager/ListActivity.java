package com.eli.filemanager;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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
import android.widget.Toast;

import com.eli.filemanager.dao.LoadSetting;
import com.eli.filemanager.pojo.Files;
import com.eli.filemanager.pojo.Users;
import com.eli.util.Util;

public class ListActivity extends Activity {

	ProcessFile process;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		LoadSetting.load(this);
		Users users = LoadSetting.users;
		if(users.getBackground()==0){			
			setTheme(R.style.Theme_Black);
		}else if(users.getBackground()==1){
			setTheme(R.style.Theme_Gray);
		}else if(users.getBackground()==2){
			setTheme(R.style.Theme_Blue);
		}else if(users.getBackground()==3){
			setTheme(R.style.Theme_Cyan);
		}else if(users.getBackground()==4){
			setTheme(R.style.Theme_Orange);
		}else if(users.getBackground()==5){
			setTheme(R.style.Theme_Green);
		}else if(users.getBackground()==6){
			setTheme(R.style.Theme_DarkGreen);
		}else{			
			setTheme(R.style.Theme_Black);
		}
		super.onCreate(savedInstanceState);
		initLocale();
		setContentView(R.layout.list);
		process = new ProcessFile(this);
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

	@Override
	protected void onResume() {
		try{
			super.onResume();
			if(getIntent() != null){
				Intent intent = getIntent();
				if(intent.getExtras() != null){
					boolean isSearch = intent.getExtras().getBoolean("isSearch");
					String src = intent.getStringExtra("src");
					if(isSearch){
						String[] temp = src.toString().split("/");
						process.paths.clear();
						for(String value : temp){
							process.paths.add(value);
						}
						process.getAllListFile(src);
						refresh();
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		process.onChangeSetting(this);
	}

	private void refreshAdapter() {
		process.fileAdapter.clear();
		if (process.list.size() > 0) {
			process.nofileImg.setVisibility(ImageView.INVISIBLE);
			for (int i = 0; i < process.list.size(); i++) {
				process.fileAdapter.add(process.list.get(i));
				process.fileAdapter.setMultiSelect(process.isMultiSelect);
				process.fileAdapter.setPositions(process.positions);
				if(process.flag_change){
					process.fileAdapter.setListView(false);
				} else {
					process.fileAdapter.setListView(true);
				}
			}
		} else {
			process.nofileImg.setVisibility(ImageView.VISIBLE);
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.newFolder:
			process.createFolder();
			return true;
		case R.id.sort:
			registerForContextMenu(getCurrentFocus());
			openContextMenu(getCurrentFocus());
			return true;
		case R.id.refresh:
			refresh();
			return true;
		case R.id.paste:
			try {
				process.paste();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		case R.id.search:
			process.search();
			return true;
		case R.id.setting:
			process.setting();
			finish();
			return true;
		case R.id.multiSelect:
			if(process.isMultiSelect == false){
				if(process.positions != null){
					process.positions.clear();
				}
				if(process.multiSelect != null && process.multiSelect.size() > 0){
					process.multiSelect.clear();
				}
				process.isMultiSelect = true;
				process.hidden_lay.setVisibility(LinearLayout.VISIBLE);
				Toast.makeText(this, "Multi select is now ON", Toast.LENGTH_SHORT).show();
			} else {
				process.isMultiSelect = false;
				if(process.positions != null){
					process.positions.clear();
				}
				if(process.multiSelect != null && process.multiSelect.size() > 0){
					process.multiSelect.clear();
				}
				process.multiSelect.clear();
				process.hidden_lay.setVisibility(LinearLayout.GONE);
				Toast.makeText(this, "Multi select is now OFF", Toast.LENGTH_SHORT).show();
			}
			refresh();
		}
		return true;
	}

	public OnItemLongClickListener itemLongClick() {
		OnItemLongClickListener longClickListener = new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Files object = (Files) parent.getItemAtPosition(position);
				if (object.isFolder()) {
					openOptionDialog(object.getName());
				} else {
					openOptionDialog(object.getName());
				}
				return true;
			}
		};
		return longClickListener;
	}

	public void openOptionDialog(final String name) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					ListActivity.this);
			builder.setTitle("Option");
			builder.setItems(R.array.option_arr,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								process.copy(name);
								break;
							case 1:
								process.move(name);
								break;
							case 2:
								process.remove(name);
								break;
							case 3:
								process.rename(name);
								break;
							case 4:
								process.details(name);
								break;
							default:
								break;
							}
						}

					});
			builder.setPositiveButton("Cancel", null);
			builder.show();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public OnItemClickListener itemClick() {
		OnItemClickListener clickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(process.isMultiSelect){
					String url = "";
					Files object = (Files) parent.getItemAtPosition(position);
					for (int i = 0; i < process.paths.size(); i++) {
						url += File.separator + process.paths.get(i);
					}
					url += File.separator + object.getName();
					process.addMultiPosition(object.getName(), url);
					refresh();
				} else {
					Files object = (Files) parent.getItemAtPosition(position);
					if (object.isFolder()) {
						process.paths.add(object.getName());
						refresh();
					} else {
						Intent intent = object.getAction();
						startActivity(intent);
					}
				}
			}
		};
		return clickListener;
	}
	
	//context menu sorting
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.sorting, menu);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.contextMenuSortingSize:
			process.sortType = 1;
			refresh();
			break;
		case R.id.contextMenuSortingLastModified:
			process.sortType = 2;
			refresh();
			break;
		case R.id.contextMenuSortingName:
			process.sortType = 0;
			refresh();
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
}
