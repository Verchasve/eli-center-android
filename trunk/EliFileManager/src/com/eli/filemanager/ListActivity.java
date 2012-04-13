package com.eli.filemanager;

import java.io.File;
import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;

import com.eli.filemanager.pojo.Files;

public class ListActivity extends Activity {

	ProcessFile process;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		process = new ProcessFile(this);
	}
	
	private void refreshAdapter() {
		process.fileAdapter.clear();
		if (process.list.size() > 0) {
			process.nofileImg.setVisibility(ImageView.INVISIBLE);
			for (int i = 0; i < process.list.size(); i++) {
				process.fileAdapter.add(process.list.get(i));
			}
		} else {
			process.nofileImg.setVisibility(ImageView.VISIBLE);
		}
		process.fileAdapter.notifyDataSetChanged();
	}
	
	public void refresh() {
		try {
			String src = "";
			for (int i = 0; i < process.paths.size(); i++) {
				src += File.separator + process.paths.get(i);
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
		case R.id.changeView:
			process.changeView();
			return true;
		case R.id.paste:
			try {
				process.paste();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
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
			AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
			builder.setTitle("Option");
			builder.setItems(R.array.option_arr,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								process.copy(name);
								System.out.println("Copy");
								break;
							case 1:
								process.move(name);
								System.out.println("move");
								break;
							case 2:
								process.remove(name);
								System.out.println("remove");
								break;
							case 3:
								process.rename(name);
								System.out.println("rename");
								break;
							case 4:
								process.details(name);
								System.out.println("detail");
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
				Files object = (Files) parent.getItemAtPosition(position);
				if (object.isFolder()) {
					String src = "";
					process.paths.add(object.getName());
					for (int i = 0; i < process.paths.size(); i++) {
						src += File.separator + process.paths.get(i);
					}
					process.getAllListFile(src);
					refreshAdapter();
				} else {
					Intent intent = object.getAction();
					startActivity(intent);
				}
			}
		};
		return clickListener;
	}
}
