package com.iyuba.conceptEnglish.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.iyuba.conceptEnglish.R;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.module.intelligence.ui.LearnResultActivity;
import com.iyuba.module.intelligence.ui.LearningGoalActivity;
import com.iyuba.module.intelligence.ui.TestResultActivity;
import com.iyuba.module.intelligence.ui.WordResultActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AIStudyActivity extends BasisActivity {

    private Context mContext;
    private boolean infoFlag = false, levelFlag = false;


    public static void buildIntent(Context context, boolean infoFlag, boolean levelFlag) {
        Intent intent = new Intent(context, AIStudyActivity.class);
        intent.putExtra("infoFlag", infoFlag);
        intent.putExtra("levelFlag", levelFlag);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ai_study);
        mContext = this;
        ButterKnife.bind(this);

        infoFlag = getIntent().getBooleanExtra("infoFlag", false);
        levelFlag = getIntent().getBooleanExtra("levelFlag", false);
    }

    protected void onResume() {
        super.onResume();
    }


    @OnClick({R.id.button_back, R.id.intel_ability_test, R.id.intel_userinfo, R.id.intel_goal, R.id.intel_result, R.id.intel_test_result, R.id.intel_word_result})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.button_back:
                finish();
                break;
            case R.id.intel_ability_test:
                if (UserInfoManager.getInstance().isVip()) {
                    intent = new Intent();
                    intent.setClass(mContext, AbilityMapActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, "成为VIP用户即可使用智慧化评测功能！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.intel_userinfo:

                intent = new Intent();
                intent.setClass(mContext, InfoFullFillActivity.class);
                startActivity(intent);
                break;
            case R.id.intel_goal:
                intent = LearningGoalActivity.buildIntent(mContext);
                startActivity(intent);
                break;
            case R.id.intel_result:

                if (infoFlag) {
                    Toast toast = Toast.makeText(mContext, "请先完善个人信息", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (levelFlag) {
                    Toast toast = Toast.makeText(mContext, "请先完善学习目标", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    intent = LearnResultActivity.buildIntent(mContext);
                    startActivity(intent);
                }
                break;
            case R.id.intel_test_result:

                if (infoFlag) {
                    Toast toast = Toast.makeText(mContext, "请先完善个人信息", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (levelFlag) {
                    Toast toast = Toast.makeText(mContext, "请先完善学习目标信息", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    intent = TestResultActivity.buildIntent(mContext);
                    startActivity(intent);
                }
                break;
            case R.id.intel_word_result:

                if (infoFlag) {
                    Toast toast = Toast.makeText(mContext, "请先完善个人信息", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (levelFlag) {
                    Toast toast = Toast.makeText(mContext, "请先完善学习目标信息", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    intent = WordResultActivity.buildIntent(mContext);
                    startActivity(intent);
                }
                break;
        }
    }
}
