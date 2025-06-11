package com.iyuba.conceptEnglish.widget;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;

import com.iyuba.conceptEnglish.R;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.base.CrashApplication;

public class WakeupDialog extends Activity {
	private Button comfiButton,cancelButton;
	private TimePicker mTimePicker;
	private int hour,minute;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wakeup_dialog);
		CrashApplication.getInstance().addActivity(this);
		comfiButton=(Button)findViewById(R.id.exitBtn0);
		cancelButton=(Button)findViewById(R.id.exitBtn1);
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				Log.d("当前退出的界面0023", getClass().getName());
			}
		});
		comfiButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hour=mTimePicker.getCurrentHour();
				minute=mTimePicker.getCurrentMinute();
				Intent mintent=new Intent();
				mintent.putExtra("hour", hour);
				mintent.putExtra("minute", minute);
				mintent.putExtra("aorp", mTimePicker.is24HourView());
				ConfigManager.Instance().putInt("wakeuphour", hour);
				ConfigManager.Instance().putInt("wakeupminute", minute);
				setResult(2, mintent);//第一个参数是自定义的结果编号，可以自己定义static，这里就省了。
				finish();
				Log.d("当前退出的界面0024", getClass().getName());
			}
		});
		mTimePicker=(TimePicker)findViewById(R.id.wakeup_picker);
		mTimePicker.setIs24HourView(true);
		Calendar calendar=Calendar.getInstance(TimeZone.getDefault());  
        int hour=calendar.get(Calendar.HOUR_OF_DAY);  
        int minute=calendar.get(Calendar.MINUTE);  
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);
		
	}
	@Override
	protected void onPause() {
		super.onPause();
		
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
	

}
