package com.eli.filemanager;

import java.io.File;
import java.util.ArrayList;

import com.eli.filemanager.dao.LoadSetting;
import com.eli.filemanager.pojo.Files;
import com.eli.util.Util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListBookmarkAdapter extends ArrayAdapter<File> {

	LayoutInflater layoutInflater;
	int layout;
	ArrayList<File> arr;
	
	public ListBookmarkAdapter(Context context, int textViewResourceId,ArrayList<File> lst) {
		super(context, textViewResourceId,lst);
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = textViewResourceId;
		arr = lst;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if(view == null){
			view = layoutInflater.inflate(layout, null);
		}		
		File file = getItem(position);

		ImageView image = (ImageView) view.findViewById(R.id.bkImageFile);
		TextView name = (TextView) view.findViewById(R.id.bkFileName);
		TextView child = (TextView) view.findViewById(R.id.bkFilePath);
		try{
			image = setImageFile(view, file, image);
			child.setText(file.getAbsolutePath());
			name.setText(file.getName());
		}catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return view;
	}
	
	public ImageView setImageFile(View view, File f, ImageView img) {
		ImageView iView = img;
		
		if(f.isDirectory()){
			if(LoadSetting.users.getIcon()==0)
				iView.setImageDrawable(view.getResources().getDrawable(R.drawable.folder_blue));
			else if(LoadSetting.users.getIcon()==1)
				iView.setImageDrawable(view.getResources().getDrawable(R.drawable.folder_blue2));
			else if(LoadSetting.users.getIcon()==2)
				iView.setImageDrawable(view.getResources().getDrawable(R.drawable.folder_yellow));
			else if(LoadSetting.users.getIcon()==3)
				iView.setImageDrawable(view.getResources().getDrawable(R.drawable.folder_yellow2));
		} else {
			if (Util.checkExtendFile(f.getName(), ".txt")) {
				iView.setImageDrawable(view.getResources().getDrawable(R.drawable.text_file));
			} else if (Util.checkExtendFile(f.getName(), ".flv")
					|| Util.checkExtendFile(f.getName(), ".3gp")
					|| Util.checkExtendFile(f.getName(), ".avi")) {
				Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(
						f.getAbsolutePath(), Thumbnails.MICRO_KIND);
				BitmapDrawable icon = new BitmapDrawable(bitmap);
				iView.setImageDrawable(icon);
			} else if (Util.checkExtendFile(f.getName(), ".mp3")) {
				iView.setImageDrawable(view.getResources().getDrawable(R.drawable.mp3_file));
			} else if (Util.checkExtendFile(f.getName(), ".doc")
					|| Util.checkExtendFile(f.getName(), ".docx")) {
				iView.setImageDrawable(view.getResources().getDrawable(R.drawable.word_file));
			} else if (Util.checkExtendFile(f.getName(), ".ppt")
					|| Util.checkExtendFile(f.getName(), ".pptx")) {
				iView.setImageDrawable(view.getResources().getDrawable(R.drawable.pptx_file));
			} else if (Util.checkExtendFile(f.getName(), ".xls")
					|| Util.checkExtendFile(f.getName(), ".xlsx")) {
				iView.setImageDrawable(view.getResources().getDrawable(R.drawable.xlsx_file));
			} else if (Util.checkExtendFile(f.getName(), ".zip")
					|| Util.checkExtendFile(f.getName(), ".rar")) {
				iView.setImageDrawable(view.getResources().getDrawable(R.drawable.rar_file));
			} else if (Util.checkExtendFile(f.getName(), ".jpg")
					|| Util.checkExtendFile(f.getName(), ".jpeg")
					|| Util.checkExtendFile(f.getName(), ".png")
					|| Util.checkExtendFile(f.getName(), ".bmp")
					|| Util.checkExtendFile(f.getName(), ".gif")) {
				Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
				BitmapDrawable icon = new BitmapDrawable(bitmap);
				iView.setImageDrawable(icon);
			} else if (Util.checkExtendFile(f.getName(), ".apk")) {
				iView.setImageDrawable(view.getResources().getDrawable(R.drawable.apk_file));
			} else if (Util.checkExtendFile(f.getName(), ".exe")) {
				iView.setImageDrawable(view.getResources().getDrawable(R.drawable.exe_file));
			} else {
				iView.setImageDrawable(view.getResources().getDrawable(R.drawable.unknown_file));
			}
		}
		
		return iView;
	}
}
