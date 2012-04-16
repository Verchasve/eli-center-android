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
		init();
		list_search = (ListView)findViewById(R.id.list_search);
		fileAdapter = new ListFileAdapter(this, R.layout.list_detail,ProcessSearch.array);
		list_search.setAdapter(fileAdapter);
		list_search.setOnItemClickListener(onItemClick());
	}
	
	public void init(){
		Files f = null;
		for (int i = 0; i < ProcessSearch.array.size(); i++) {
			f = ProcessSearch.array.get(i);
			if (!f.isFolder()) {
				Intent action = new Intent(Intent.ACTION_VIEW);
				Bitmap bitmap;
				if (Util.checkExtendFile(f.getName(), ".txt")) {
					f.setIcon(getResources().getDrawable(
							R.drawable.text_file));
					action.setDataAndType(Uri.fromFile(new File(f.getChildFile())), "text/*");
				} else if (Util.checkExtendFile(f.getName(), ".flv")
						|| Util.checkExtendFile(f.getName(), ".3gp")
						|| Util.checkExtendFile(f.getName(), ".avi")) {
					bitmap = ThumbnailUtils.createVideoThumbnail(
							f.getChildFile(), Thumbnails.MICRO_KIND);
					f.setIcon(new BitmapDrawable(bitmap));
					action.setDataAndType(Uri.fromFile(new File(f.getChildFile())), "video/*");
				} else if (Util.checkExtendFile(f.getName(), ".mp3")) {
					f.setIcon(getResources().getDrawable(
							R.drawable.mp3_file));
					action.setDataAndType(Uri.fromFile(new File(f.getChildFile())), "audio/*");
				} else if (Util.checkExtendFile(f.getName(), ".doc")
						|| Util.checkExtendFile(f.getName(), ".docx")) {
					f.setIcon(getResources().getDrawable(
							R.drawable.word_file));
					action.setDataAndType(Uri.fromFile(new File(f.getChildFile())), "text/*");
				} else if (Util.checkExtendFile(f.getName(), ".ppt")
						|| Util.checkExtendFile(f.getName(), ".pptx")) {
					f.setIcon(getResources().getDrawable(
							R.drawable.pptx_file));
					action.setDataAndType(Uri.fromFile(new File(f.getChildFile())), "text/*");
				} else if (Util.checkExtendFile(f.getName(), ".xls")
						|| Util.checkExtendFile(f.getName(), ".xlsx")) {
					f.setIcon(getResources().getDrawable(
							R.drawable.xlsx_file));
					action.setDataAndType(Uri.fromFile(new File(f.getChildFile())), "text/*");
				} else if (Util.checkExtendFile(f.getName(), ".zip")
						|| Util.checkExtendFile(f.getName(), ".rar")) {
					f.setIcon(getResources().getDrawable(
							R.drawable.rar_file));
					action.setDataAndType(Uri.fromFile(new File(f.getChildFile())), "video/*");
				} else if (Util.checkExtendFile(f.getName(), ".jpg")
						|| Util.checkExtendFile(f.getName(), ".jpeg")
						|| Util.checkExtendFile(f.getName(), ".png")
						|| Util.checkExtendFile(f.getName(), ".bmp")
						|| Util.checkExtendFile(f.getName(), ".gif")) {
					bitmap = BitmapFactory.decodeFile(f.getChildFile());
					f.setIcon(new BitmapDrawable(bitmap));
					action.setDataAndType(Uri.fromFile(new File(f.getChildFile())), "image/*");
				} else if (Util.checkExtendFile(f.getName(), ".apk")) {
					f.setIcon(getResources().getDrawable(
							R.drawable.apk_file));
					action.setDataAndType(Uri.fromFile(new File(f.getChildFile())),
							"application/vnd.android.package-archive");
				} else if (Util.checkExtendFile(f.getName(), ".exe")) {
					f.setIcon(getResources().getDrawable(
							R.drawable.exe_file));
				} else {
					f.setIcon(getResources().getDrawable(
							R.drawable.unknown_file));
				}
				f.setAction(action);
			} else if (f.isFolder()) {
				f.setIcon(getResources().getDrawable(
						R.drawable.folder));
			}
		}
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
