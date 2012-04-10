package com.eli.filemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;
import android.widget.TextView;

import com.eli.filemanager.pojo.Files;

public class List extends Activity {
	ListView listFile;
	ListFileAdapter listFileAdapter;
	Context context;
	TextView currentFile;
	ArrayList<Files> arr;
	ArrayList<Files> arrFile;
	ArrayList<Files> arrFolder;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        listFile = (ListView) findViewById(R.id.lvFile);
        currentFile = (TextView) findViewById(R.id.tvCurrentFile);
        getAllListFile("/");
        listFileAdapter = new ListFileAdapter(this, R.layout.list_detail,arr);
        listFile.setAdapter(listFileAdapter);
    }
    
    private void getAllListFile(String path){
    	arrFile = new ArrayList<Files>();
    	arrFolder = new ArrayList<Files>();
    	File sdCardRoot = Environment.getExternalStorageDirectory();
    	File dir = new File(sdCardRoot,path);
    	currentFile.setText(dir.getAbsolutePath());
    	for(File f :dir.listFiles()){
    		if(f.isFile()){
    			Files ff = new Files();
    			ff.setName(f.getName());
    			arrFile.add(ff);
    		}else if(f.isDirectory()){
    			Files ff = new Files();
    			ff.setName(f.getName());
    			arrFolder.add(ff);
    		}
    	}
        sort(arrFile);
        sort(arrFolder);
    	arr = new  ArrayList<Files>();
    	arr.addAll(arrFolder);
    	arr.addAll(arrFile);
    }
    
    private void sort(ArrayList<Files> lst ){
    	Collections.sort(lst, new Comparator<Files>() {
            @Override
            public int compare(Files object1, Files object2) {
                return object1.getName().toLowerCase().compareTo(object2.getName().toLowerCase());
            }
        });

    }
}
