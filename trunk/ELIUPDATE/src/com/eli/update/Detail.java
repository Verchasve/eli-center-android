package com.eli.update;

import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

public class Detail extends Activity{

	ImageView icon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		try{
			icon = (ImageView)findViewById(R.id.icon);
			Intent intent = getIntent();
			String[] item = intent.getExtras().getStringArray("item");
			
			Drawable drawable = Drawable.createFromStream(new URL(item[3]).openStream(),null);
			icon.setImageDrawable(drawable);

		}catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

}
