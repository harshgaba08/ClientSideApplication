package com.weexcel.bluetooth;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class List_Setting_Custom_Adapter extends ArrayAdapter<String>{
	private final Activity context; //activity context
	private final String[] text;  
	private final Integer[] imageId;
	
	
	
	public List_Setting_Custom_Adapter(Activity context,
	String[] web, Integer[] imageId) {
	super(context, R.layout.list_settings_custom_view, web);
	this.context = context;
	this.text = web;
	this.imageId = imageId;
	}
	
	// view method to inflate and set the data and images in list
	@Override
	public View getView(int position, View view, ViewGroup parent) {
	LayoutInflater inflater = context.getLayoutInflater();
	View rowView= inflater.inflate(R.layout.list_settings_custom_view, null, true);
	TextView txtTitle = (TextView) rowView.findViewById(R.id.item_text);
	ImageView imageView = (ImageView) rowView.findViewById(R.id.item_icon);
	txtTitle.setText(text[position]);
	imageView.setImageResource(imageId[position]);
	return rowView;
	}
	}
