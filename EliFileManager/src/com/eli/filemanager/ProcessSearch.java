package com.eli.filemanager;

import android.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class ProcessSearch {

	SearchActivity activity;
	private Button search_btn;
	private Spinner directory, type_search;
	private CheckBox include_sub, case_sensitive;
	private EditText name_search, maxsize, minsize, daysago;

	public ProcessSearch(SearchActivity activity) {
		this.activity = activity;
		directory = (Spinner)activity.findViewById(R.id.directory);
		type_search = (Spinner)activity.findViewById(R.id.type_search);
		name_search = (EditText)activity.findViewById(R.id.name_et);
		include_sub = (CheckBox)activity.findViewById(R.id.sub_cbx);
		case_sensitive = (CheckBox)activity.findViewById(R.id.case_cbx);
		maxsize = (EditText)activity.findViewById(R.id.max_et);
		minsize = (EditText)activity.findViewById(R.id.min_et);
		daysago = (EditText)activity.findViewById(R.id.days_et);
		search_btn = (Button)activity.findViewById(R.id.serch_btn);
		init();
	}

	public void init(){
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(activity, R.array.directory_arr ,android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directory.setAdapter(adapter1);
        directory.setSelection(0);
        
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(activity, R.array.type_search_arr ,android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_search.setAdapter(adapter2);
        type_search.setSelection(0);
        
        search_btn.setOnClickListener(onClickSearchListener());
	}
	
	public OnClickListener onClickSearchListener(){
		OnClickListener onBackClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = name_search.getText().toString();
				if(name == null && name.equals("")){
					AlertDialog.Builder builder = new AlertDialog.Builder(activity);
					builder.setTitle("Missing");
					builder.setMessage("You have to type searching name");
					builder.setPositiveButton("Ok", null);
					builder.show();
					name_search.requestFocus();
				}else{
					
				}
			}
		};
		return onBackClick;
	}
}
