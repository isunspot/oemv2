package com.capitalbio.oemv2.myapplication.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.capitalbio.oemv2.myapplication.Fragment.AircatHelpFragment;
import com.capitalbio.oemv2.myapplication.Fragment.BloodHelpFragment;
import com.capitalbio.oemv2.myapplication.Fragment.BodyHelpFragment;
import com.capitalbio.oemv2.myapplication.Fragment.SportHelpFragment;
import com.capitalbio.oemv2.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class UserHelpActivity extends FragmentActivity {
	private TextView textView;
    private LinearLayout llUserAgreement;
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentList;
	private RadioButton radioButton1,radioButton2,radioButton3,radioButton4,radioButton5;
	private RadioGroup radioGroup;
	protected void initChildLayout(){

	/*	setNavigateBarBackGround(R.color.blue_light);
		setTvTopTitle(R.string.userhelp);
		setLeftTopIcon(R.drawable.ic_back);
		llUserAgreement = (LinearLayout) View.inflate(this, R.layout.ac_userhelp, null);
		llBody.addView(llUserAgreement);*/
		/*textView  = (TextView) findViewById(R.id.tv_view);

		textView.setText(Html
				.fromHtml("一：首次添加注意事项<br /> <br />" +
								"\n" +
								"  添加方法：<br /><br />1) 保证您的手机已连接一个可以连接到internet的无线路由器\n" +
								"\t  <br /> <br />2) 点击空气猫图标，输入路由器密码，确定。\n" +
								"\t   <br /><br />3) 进入搜索界面，空气猫连接成功后会自动搜索到空气猫的mac地址。\n" +
								"\t   <br /><br />4) 点击mac地址进行添加，同时为空气猫设置备注。确认添加。" +
								" <br /> <br />\n" +
								" 二：新增设备注意事项 <br /> <br />\n" +
								"\n" +
								"  添加方法:  <br /><br />1) 进入空气猫主界面后点击右上角的加号“+”图标\n" +
								"            <br /><br />2) 为空气猫设置可连接外网的路由器，然后搜索添加。<br /> <br />"
								+ "三：分享设备方法<br /> <br />" +
								"\n" +
								"   关注方法:<br /> 1) 推送消息添加：点击分享图标，选择推送消息，输入对方用户名，点击“发送”。\n" +
								"\t <br />2) 扫描二维码添加：点击分享图标，选择生成二维码，被分享用户点击app右上角加号“+”，选择“扫一扫”，成功扫描二维码后即可关注设备。\n" +
								"\t  <br />"

				));*/


	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_userhelp);
		mPager = (ViewPager) findViewById(R.id.viewpager);
		radioButton1 = (RadioButton) findViewById(R.id.rb_1);
		radioButton2 = (RadioButton) findViewById(R.id.rb_2);
		radioButton3 = (RadioButton) findViewById(R.id.rb_3);
		radioButton4 = (RadioButton) findViewById(R.id.rb_4);
		//radioButton5 = (RadioButton) findViewById(R.id.rb_5);
		radioGroup = (RadioGroup) findViewById(R.id.bh_radioGroup);

		InitViewPager();

	}

	public void back(View v){
		finish();
	}

	/*
     * 初始化ViewPager
     */
	public void InitViewPager(){
		fragmentList = new ArrayList<Fragment>();
		Fragment oneFragment= new BloodHelpFragment();
		Fragment secondFragment = new BodyHelpFragment();
		Fragment thirdFragment = new SportHelpFragment();
		Fragment fourFragment = new AircatHelpFragment();
		fragmentList.add(oneFragment);
		fragmentList.add(secondFragment);
		fragmentList.add(thirdFragment);
		fragmentList.add(fourFragment);

		//给ViewPager设置适配器
		mPager.setAdapter(new PageAdapter(getSupportFragmentManager(), fragmentList));
		mPager.setCurrentItem(0);//设置当前显示标签页为第一页
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器

		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.rb_1:
						mPager.setCurrentItem(0);
						break;
					case R.id.rb_2:
						mPager.setCurrentItem(1);
						break;
					case R.id.rb_3:
						mPager.setCurrentItem(2);
						break;
					case R.id.rb_4:
						mPager.setCurrentItem(3);
						break;

				}
			}
		});

	}

	 class PageAdapter extends FragmentPagerAdapter {

		private List<Fragment> fragments;
		public PageAdapter(FragmentManager fm) {
			super(fm);
		}
		public PageAdapter(FragmentManager fm,List<Fragment> fgs) {
			super(fm);
			this.fragments = fgs;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			return super.instantiateItem(container, position);
		}

		public void setFragments(List<Fragment> fragments) {
			this.fragments = fragments;
			notifyDataSetChanged();
		}

		@Override
		public Fragment getItem(int pos) {

			return fragments.get(pos);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
	}


	private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			RadioButton rb = (RadioButton) radioGroup.getChildAt(position);
			rb.setChecked(true);

		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}
	}
}