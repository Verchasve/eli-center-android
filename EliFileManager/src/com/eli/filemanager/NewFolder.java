package com.eli.filemanager;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NewFolder extends Activity {
	EditText nameFolder;
	Button btCreatFolder;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_folder_dialog);
        nameFolder = (EditText) findViewById(R.id.nameFolder);
        btCreatFolder = (Button) findViewById(R.id.btCreateFolder);
    	evenClick();
    }
    
    
    private void evenClick() {
    	btCreatFolder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean success = (new File("/mnt/sdcard/" + nameFolder.getText())).mkdir();
				if(!success){
					alertbox("Exist folder", "Folder name is exist!");
				} else {
		            Intent intent = new Intent(NewFolder.this, List.class);
		            startActivity(intent);
				}
			}
		});
	}
    
    protected void alertbox(String title, String mymessage)
    {
    	new AlertDialog.Builder(this)
	       .setMessage(mymessage)
	       .setTitle(title)
	       .setCancelable(true)
	       .setNeutralButton(android.R.string.cancel,
	          new DialogInterface.OnClickListener() {
	          public void onClick(DialogInterface dialog, int whichButton){}
	          })
	       .show();
    }
}
