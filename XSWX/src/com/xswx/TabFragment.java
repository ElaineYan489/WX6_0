package com.xswx;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TabFragment extends Fragment {
	/**
	 * 设置每个Fragment的内容
	 */
	private String mTitle = "Default";
	public final static String 	TITLE = "title";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		if (getArguments()!= null) {
			mTitle = getArguments().getString(TITLE);
		}
		TextView tv = new TextView(getActivity());
		tv.setText(mTitle);
		tv.setTextSize(20);
		tv.setGravity(Gravity.CENTER);
		tv.setBackgroundColor(Color.parseColor("#ffffffff"));
		return tv;
	}
}
