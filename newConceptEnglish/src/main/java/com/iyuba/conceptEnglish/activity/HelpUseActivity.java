package com.iyuba.conceptEnglish.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.iyuba.ConstantNew;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.adapter.TestFragmentAdapter;
import com.iyuba.conceptEnglish.han.utils.AdvertisingKey;
import com.iyuba.conceptEnglish.lil.concept_other.book_choose.ConceptBookChooseActivity;
import com.iyuba.conceptEnglish.widget.indicator.PageIndicator;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;

/**
 * 使用说明Activity
 */
public class HelpUseActivity extends BasisActivity {
    private ViewPager viewPager;

    private PageIndicator pi;
    private TestFragmentAdapter testFragmentAdapter;
    private int lastIntoCount;
    private int goInfo = 0;// 0=第一次使用程序 1=从设置界面进入

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.help_use);


        CrashApplication.getInstance().addActivity(this);
        goInfo = this.getIntent().getIntExtra("isFirstInfo", 0);

        pi = (PageIndicator) findViewById(R.id.pageIndicator);

        FragmentManager fragmentManager = getSupportFragmentManager();
        testFragmentAdapter = new TestFragmentAdapter(fragmentManager);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(0);
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                pi.setCurrIndicator(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                switch (arg0) {
                    case 0: // 停止变更
                        if (viewPager.getCurrentItem() == testFragmentAdapter.getCount() - 1) {
                            lastIntoCount = lastIntoCount + 1;
                        }
                        break;
                    case 1:
                        break;
                    case 2: // 已经变更
                        lastIntoCount = 0;
                        break;
                }
                if (lastIntoCount > 1) {
                    if (arg0 == 0) {
                        if (goInfo == 0) {

                            if (getPackageName().equals(AdvertisingKey.releasePackage)) {
//                                Intent intent = new Intent();
//                                intent.setClass(HelpUseActivity.this,
//                                        BookChooseActivity.class);
//                                intent.putExtra("isFirstInfo", 0);
//                                startActivity(intent);
                                ConceptBookChooseActivity.start(HelpUseActivity.this,0);
                                finish();
                            } else if(getPackageName().equals(AdvertisingKey.xiaomiPackage) ||
                                    getPackageName().equals(AdvertisingKey.vivoPackage)){
//                                Intent intent = new Intent();
//                                intent.setClass(HelpUseActivity.this,
//                                        BookChooseActivity.class);
//                                intent.putExtra("isFirstInfo", 0);
//                                startActivity(intent);
                                ConceptBookChooseActivity.start(HelpUseActivity.this,0);
                                finish();
                            }else if (ConstantNew.PACK_NAME.equals("com.iyuba.youth")){
                                Intent intent3 = new Intent(HelpUseActivity.this, MainFragmentActivity.class);
                                intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent3.putExtra("isFirstInfo", 0);
                                startActivity(intent3);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            } else if (ConstantNew.PACK_NAME.equals(AdvertisingKey.smallClassPackage)){
//                                Intent intent = new Intent();
//                                intent.setClass(HelpUseActivity.this,
//                                        BookChooseActivity.class);
//                                intent.putExtra("isFirstInfo", 0);
//                                startActivity(intent);
                                ConceptBookChooseActivity.start(HelpUseActivity.this,0);
                            }
                        }
                        finish();
                    }
                }
            }
        });
        viewPager.setAdapter(testFragmentAdapter);
        pi.setIndicator(testFragmentAdapter.getCount());
        pi.setCurrIndicator(0);
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