package com.xswx;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.xswx.view.ChangeColorWithText;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends FragmentActivity implements OnClickListener,OnPageChangeListener{

	private ViewPager mViewPager;
	private List<Fragment> mTabs = new ArrayList<Fragment>();
	private String[] mTitles = new String[]{
			"First Fragment !", "Second Fragment !", "Third Fragment !",
			"Fourth Fragment !"
	};
	private FragmentPagerAdapter mAdapter;
	private List<ChangeColorWithText> mIndicateTablist = new ArrayList<ChangeColorWithText>();
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setChangeOverflowButtonAlways();
		getActionBar().setDisplayShowHomeEnabled(false);
		
		initView();
		initDatas();
		mViewPager.setAdapter(mAdapter);
		initEvent();
		
	}
	
	private void initDatas() {
		for(String title : mTitles){
			//1、创建TabFragment
			TabFragment tf = new TabFragment();
			//2、利用Bundle传值
			Bundle bundle = new Bundle();
			bundle.putString(TabFragment.TITLE, title);
			tf.setArguments(bundle);
			mTabs.add(tf);
		}
		
		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()){

			@Override
			public Fragment getItem(int position) {
				return mTabs.get(position);
			}

			@Override
			public int getCount() {
				return  mTabs.size();
			}
			
		};
	}

	private void initEvent() {
		mViewPager.setOnPageChangeListener(this);
		
		
	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.id_viewpagers);
		ChangeColorWithText ct_weixin = (ChangeColorWithText) findViewById(R.id.ct_weixin);
		mIndicateTablist.add(ct_weixin);
		ChangeColorWithText ct_contact = (ChangeColorWithText) findViewById(R.id.ct_contact);
		mIndicateTablist.add(ct_contact);
		ChangeColorWithText ct_found = (ChangeColorWithText) findViewById(R.id.ct_found);
		mIndicateTablist.add(ct_found);
		ChangeColorWithText ct_me = (ChangeColorWithText) findViewById(R.id.ct_me);
		mIndicateTablist.add(ct_me);
		
		//为tab添加监听事件
				ct_weixin.setOnClickListener(this);
				ct_contact.setOnClickListener(this);
				ct_found.setOnClickListener(this);
				ct_me.setOnClickListener(this);
				
				ct_weixin.setIconAlpha(1.0f);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void setChangeOverflowButtonAlways(){
		try{
			ViewConfiguration configuration = ViewConfiguration.get(this);
			Field menukey = configuration.getClass().getDeclaredField("sHasPermanentMenuKey");//获取MainActivity中的sHasPermanentMenuKey属性
			menukey.setAccessible(true);
			//要强制系统显示menu图标，可以用反射的方法，把ViewConfiguration对象的sHasPermanentMenuKey属性设为false,让系统以为没有硬件菜单键
			menukey.setBoolean(configuration, false);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	//设置menu显示icon
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if(featureId == Window.FEATURE_ACTION_BAR && menu!=null){
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {//滑动时调用
		if(positionOffset > 0 ){
			ChangeColorWithText left = mIndicateTablist.get(position);
			ChangeColorWithText right = mIndicateTablist.get(position+1);
			left.setIconAlpha(1-positionOffset);
			right.setIconAlpha(positionOffset);
			
		}
	}

	@Override
	public void onPageSelected(int arg0) {
		
	}

	@Override
	public void onClick(View v) {
		
		clickTab(v);
	}

	public void clickTab(View v) {
		resetOtherTabColor();
		
		switch (v.getId()) {
		case R.id.ct_weixin:
			mIndicateTablist.get(0).setIconAlpha(1.0f);
			mViewPager.setCurrentItem(0,false);
			break;
		case R.id.ct_contact:
			mIndicateTablist.get(1).setIconAlpha(1.0f);
			mViewPager.setCurrentItem(1,false);
			break;
		case R.id.ct_found:
			mIndicateTablist.get(2).setIconAlpha(1.0f);
			mViewPager.setCurrentItem(2,false);
			break;
		case R.id.ct_me:
			mIndicateTablist.get(3).setIconAlpha(1.0f);
			mViewPager.setCurrentItem(3,false);
			break;

		default:
			break;
		}
	}

	/**
	 * 重置其他tab的颜色值
	 */
	private void resetOtherTabColor() {
		for(ChangeColorWithText c : mIndicateTablist){
			c.setIconAlpha(0);
		}
	}
}
