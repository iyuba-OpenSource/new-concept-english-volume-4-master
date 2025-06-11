package com.iyuba.conceptEnglish.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.api.ApiRetrofit;
import com.iyuba.conceptEnglish.api.GetSignHistoryAPI;
import com.iyuba.conceptEnglish.entity.ShareInfoRecord;
import com.iyuba.conceptEnglish.entity.VoicesResult;
import com.iyuba.conceptEnglish.han.bean.LocalCalendarRecord;
import com.iyuba.conceptEnglish.han.utils.CalendarRecordHelper;
import com.iyuba.conceptEnglish.han.utils.ColorfulMonthView;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.sdk.other.NetworkUtil;
import com.othershe.calendarview.utils.CalendarUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by carl shen on 2021/5/28
 * New English News, new study experience.
 *
 * update by han on 2022/8/23
 * 改换日历控件
 */
public class CalendarActivity extends BasisActivity {

    private Context mContext;
    TextView mTimeTitle;
    TextView chooseDate;
    private ProgressDialog mLoadingDialog;
    private final int[] cDate = CalendarUtil.getCurrentDate();
    private CalendarView calendar_new;
    private CalendarRecordHelper recordHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(this);
        mContext = this;
        chooseDate = findViewById(R.id.choose_date);
        mTimeTitle = findViewById(R.id.time_title);
        recordHelper=new CalendarRecordHelper(this);
        ImageView mButtonBack = findViewById(R.id.button_back);
        mButtonBack.setOnClickListener(v -> finish());
        calendar_new=findViewById(R.id.calendar_new);
        calendar_new.setOnMonthChangeListener((year, month) -> {
            String text=year+ "年" + month+ "月";
            mTimeTitle.setText(text);
            cDate[0]=year;
            cDate[1]=month;
            getCalendar();
        });
        mTimeTitle.setText(cDate[0] + "年" + cDate[1] + "月");
        chooseDate.setText("今天的日期：" + cDate[0] + "年" + cDate[1] + "月" + cDate[2] + "日");
        if (NetworkUtil.isConnected(mContext)) {
            getCalendar();
        } else {
            setCalendarScheme(null);
            showToast(R.string.network_error);
        }
    }

    public void scrollToNext(View view) {
        if (NetworkUtil.isConnected(mContext)) {
            showLoadingLayout();
            if (cDate[1] > 0) {
                cDate[1]--;
            } else {
                cDate[0]--;
                cDate[1] = 12;
            }
            calendar_new.scrollToNext();
            getCalendar();
        } else {
            setCalendarScheme(null);
            showToast(R.string.network_error);
        }
    }

    public void scrollToPre(View view) {
        if (NetworkUtil.isConnected(mContext)) {
            showLoadingLayout();
            if (cDate[1] > 0) {
                cDate[1]--;
            } else {
                cDate[0]--;
                cDate[1] = 12;
            }
            calendar_new.scrollToPre();
            getCalendar();
        } else {
            setCalendarScheme(null);
            showToast(R.string.network_error);
        }
    }
    public String getCurrTime(boolean flag){
        String curTime;
        String symbol="";
        if (flag){
            symbol="-";
        }
        if (cDate[1] < 10) {
            curTime = cDate[0] +symbol+ "0" + cDate[1];
        } else {
            curTime = cDate[0] +symbol+ "" + cDate[1];
        }
        return curTime;
    }

    private void getCalendar() {

        GetSignHistoryAPI getSignHistoryAPI = ApiRetrofit.getInstance().getGetSignHistoryAPI();
        getSignHistoryAPI.getCalendar(GetSignHistoryAPI.url, String.valueOf(UserInfoManager.getInstance().getUserId()), Constant.APPID, getCurrTime(false))
                .enqueue(new Callback<VoicesResult>() {
                    @Override
                    public void onResponse(Call<VoicesResult> call, Response<VoicesResult> response) {
                        dismissLoadingLayout();
                        assert response.body() != null;
                        if ("200".equals(response.body().result)) {
                            setCalendarScheme(response.body().record);
                        }
                    }

                    @Override
                    public void onFailure(Call<VoicesResult> call, Throwable t) {
                        setCalendarScheme(null);
                        dismissLoadingLayout();
                    }
                });
    }

    private void setCalendarScheme(List<ShareInfoRecord> ranking){
        List<ShareInfoRecord> list;
        if (ranking==null){
            list= new ArrayList<>();
        }else {
            list= ranking;
        }
        calendar_new.setSchemeDate(inflateNetData(list));
    }

    /**
     * 无论**情况都根据服务器数据更新本地数据然后再查
     * */
    public Map<String, Calendar> inflateNetData(List<ShareInfoRecord> ranking) {
        Map<String, Calendar>map=new HashMap<>();
        ranking.forEach(item -> {
            //先把item转换为Calendar再put
            String localTime=item.createtime.substring(0,item.createtime.indexOf(" "));
            recordHelper.insertOrReplace(localTime,item.scan);
        });
        String createTime="%"+getCurrTime(true)+"%";
        List<LocalCalendarRecord> localRecords=recordHelper.findByCreateTime(createTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        localRecords.forEach(item -> {
            try {
                Date date = sdf.parse(item.getCreateTime());
                String flag;
                if (item.getScan()>1){
                    flag=ColorfulMonthView.scan;
                }else {
                    flag=ColorfulMonthView.unScan;
                }

                Calendar calendar = getSchemeCalendar(date.getYear() + 1900, date.getMonth() + 1, date.getDate(), flag);
                map.put(calendar.toString(),calendar);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        return map;
    }


    public void showLoadingLayout() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new ProgressDialog(mContext);
        }
        mLoadingDialog.show();
    }

    public void dismissLoadingLayout() {
        if (mLoadingDialog != null) {
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        }
    }

    public void showToast(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    private Calendar getSchemeCalendar(int year, int month, int day, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setScheme(text);
        return calendar;
    }


}
