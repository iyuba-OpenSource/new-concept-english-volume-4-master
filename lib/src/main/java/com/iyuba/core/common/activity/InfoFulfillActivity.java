package com.iyuba.core.common.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.data.model.Location;
import com.iyuba.core.common.data.model.UserDetailInfoResponse;
import com.iyuba.core.common.data.remote.CheckIPService;
import com.iyuba.core.common.presenter.ProfileMvpView;
import com.iyuba.core.common.presenter.ProfilePresenter;
import com.iyuba.core.common.util.JsonFileReader;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.lib.R;
import com.iyuba.lib.R2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import personal.iyuba.personalhomelibrary.utils.ToastFactory;
import timber.log.Timber;

/**
 * 个人信息完善界面
 */
public class InfoFulfillActivity extends AppCompatActivity implements ProfileMvpView {
    Button backButton;
    Spinner genderSpinner;
    Spinner ageSpinner;
    Spinner locationSpinner;
    Spinner locationCitySpinner;
    Spinner jobSpinner;
    Button submit;

    private ProfilePresenter mPresenter;

    private Context mContext;

    private Location locationData;
    private String[] genderItems;
    private String[] ageItems;
    private String[] jobItems;

    private int tempCityLocNum = 0;

    /* 默认配置 */
    private String gender = "男";
    private String age = "90后";
    private String provinceLoc;
    private String cityLoc;
    private String job = "大学生";
    private String DEFAULT_PROVINCE;
    private String DEFAULT_CITY;
    private final String FAIL_TO_FIND_CITY = "FAILED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
        mContext = this;

        mPresenter = new ProfilePresenter();
        mPresenter.attachView(this);

        initView();
        initClick();

        setListItems();
        initLocationData();
        initSpinners();
        //getDefaultProvince();
        if (UserInfoManager.getInstance().isLogin()){
            mPresenter.getUserInfo();
        }
    }

    private void initView(){
        backButton = findViewById(R.id.back_button);
        genderSpinner = findViewById(R.id.spinner_gender);
        ageSpinner = findViewById(R.id.spinner_age);
        locationSpinner = findViewById(R.id.spinner_location);
        locationCitySpinner = findViewById(R.id.spinner_location_city);
        jobSpinner = findViewById(R.id.spinner_job);
        submit = findViewById(R.id.submit);
    }

    private void initClick(){
        findViewById(R.id.submit).setOnClickListener(v->{
            String parsedGender = parseGender(gender);
            String parsedAge = parseAge(age);
            String parsedJob = parseJob(job);

            mPresenter.uploadUserInfo(parsedGender, provinceLoc, cityLoc, parsedAge, parsedJob);
        });
        findViewById(R.id.back_button).setOnClickListener(v->{
            InfoFulfillActivity.this.finish();
        });
    }

    private void setListItems() {
        genderItems = mContext.getResources().getStringArray(R.array.gender_list);
        ageItems = mContext.getResources().getStringArray(R.array.age_list);
        jobItems = mContext.getResources().getStringArray(R.array.job_list);

        //根据包名进行判断(样式)
        if (getPackageName().equals("com.iyuba.conceptStory")
                ||getPackageName().equals("com.iyuba.nce")
                ||getPackageName().equals("com.iyuba.newconcepttop")){
            LinearLayout bgLayout = findViewById(R.id.bgLayout);
            bgLayout.setBackgroundResource(R.drawable.background_complete_profile_new);
            Button submitBtn = findViewById(R.id.submit);
            submitBtn.setBackgroundResource(R.drawable.blue_button);
        }else if (getPackageName().equals("com.iyuba.learnNewEnglish")){
            //新概念英语微课
            LinearLayout bgLayout = findViewById(R.id.bgLayout);
            bgLayout.setBackgroundResource(R.drawable.background_complete_profile_new2);
            Button submitBtn = findViewById(R.id.submit);
            submitBtn.setBackgroundResource(R.drawable.blue_button);
        }
    }

    private void initLocationData() {
        final String jsonFileName = "city.json";
        String rawData = JsonFileReader.getJson(mContext, jsonFileName);

        locationData = new Gson().fromJson(rawData, Location.class);
    }

    private void initSpinners() {
//        String[] genderItems = mContext.getResources().getStringArray(R.array.gender_list);
//        String[] ageItems = mContext.getResources().getStringArray(R.array.age_list);
//        String[] jobItems = mContext.getResources().getStringArray(R.array.job_list);

        easySetAdapter(genderSpinner, Arrays.asList(genderItems));
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                tv.setGravity(Gravity.END);
                gender = genderSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        easySetAdapter(ageSpinner, Arrays.asList(ageItems));
        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                tv.setGravity(Gravity.END);
                age = ageSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        easySetAdapter(jobSpinner, Arrays.asList(jobItems));
        jobSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                tv.setGravity(Gravity.END);
                job = jobSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        List<String> testData = generateTestList(0);
//        easySetAdapter(locationSpinner, testData);
        easySetAdapter(locationSpinner, getAllProvince());
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    TextView tv = (TextView) view;
                    tv.setGravity(Gravity.END);
                }
                provinceLoc = locationSpinner.getSelectedItem().toString();
                easySetAdapter(locationCitySpinner, getCityList(provinceLoc));

//                locationCitySpinner.setSelection(tempCityLocNum);
//                tempCityLocNum = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        easySetAdapter(locationCitySpinner, getCityList("北京"));
        locationCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    TextView tv = (TextView) view;
                    tv.setGravity(Gravity.END);
                }
                cityLoc = locationCitySpinner.getSelectedItem().toString();
                Timber.d("cityLoc: " + cityLoc);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        genderSpinner.setSelection(0);
        ageSpinner.setSelection(2);//90后的位置
        jobSpinner.setSelection(3);
        locationSpinner.setSelection(0);
        locationCitySpinner.setSelection(0);

        provinceLoc = locationSpinner.getSelectedItem().toString();
        cityLoc = locationCitySpinner.getSelectedItem().toString();
    }

    private String parseGender(String gender) {
        final String DEFAULT_GENDER = "男";
        String newGender = "1";//男性为1，女性非1

        if ("男".equals(gender)) {
            newGender = "1";
        } else if ("女".equals(gender)) {
            newGender = "2";
        }

        return newGender;
    }

    private String parseAge(String age) {
        final String OUT_RANGE_AGE_0 = "05后";
        final String OUT_RANGE_AGE_1 = "70后";
        final String DEFAULT_AGE = "90后";

        for (String existAge : ageItems) {
            if (existAge.equals(age) && !existAge.equals(ageItems[0])
                    && !existAge.equals(ageItems[ageItems.length - 1])) {
                return existAge.replace("后", "s");
            } else if (existAge.equals(ageItems[ageItems.length - 1])) {
                return OUT_RANGE_AGE_0.replace("后", "s");
            } else if (existAge.equals(ageItems[0])) {
                return OUT_RANGE_AGE_1.replace("后", "s");
            }
        }

        return DEFAULT_AGE.replace("后", "s");
    }

    private String parseJob(String job) {
        final String DEFAULT_JOB = "大学生";

        for (String existGender : jobItems) {
            if (existGender.equals(job) && !existGender.equals(jobItems[jobItems.length - 1])) {
                return existGender;
            } else if (existGender.equals(jobItems[jobItems.length - 1])) {
                return DEFAULT_JOB;
            }
        }

        return DEFAULT_JOB;
    }

    private void getDefaultProvince() {
        if (!UserInfoManager.getInstance().isLogin()) {//预计打开此页面的时候会检测登录，提交的时候也会检测。可能冗余
            return;
        }
        String uid = String.valueOf(UserInfoManager.getInstance().getUserId());
        String appid = CheckIPService.appid;
        mPresenter.checkIP(uid, appid);
    }

    private List<String> getAllProvince() {
        List<String> provinceList = new ArrayList<>();
        if (locationData == null || locationData.getCitylist() == null) {
            Timber.d("Fail to get LocationData!");
            Toast.makeText(mContext, "获取省市信息失败", Toast.LENGTH_SHORT).show();
        } else {
            for (Location.CitylistBean item : locationData.getCitylist()) {
                provinceList.add(item.getProvince());
            }
        }

        return provinceList;
    }

    private int getProvinceLocationNum(String province) {
        int loc = 0;
        if (province == null) {
            return loc;
        }
        for (Location.CitylistBean item : locationData.getCitylist()) {
            if (province.contains(item.getProvince())) {
                return loc;
            }
            loc++;
        }
        Timber.e("Could not find province! %s", province);
        return 0;
    }

    private int getCityLocationNum(String city) {
        int loc = 0;
        if (city == null) {
            return loc;
        }
        List<String> cityList = getCityList(provinceLoc);

        for (String item : cityList) {
            if (city.contains(item)) {
                return loc;
            }
            loc++;
        }

        Timber.e("Could not find city! %s %s", city, provinceLoc);
        return 0;
    }

    private List<String> getCityList(String province) {
        Location.CitylistBean singleProvince = findProvince(province);
        if (singleProvince.getCityStringList() == null) {
            return new ArrayList<>();
        }

        return singleProvince.getCityStringList();
    }

    private Location.CitylistBean findProvince(String province) {
        if (locationData == null || locationData.getCitylist() == null) {
            Timber.e("ERROR: could not get data from file!");
            return new Location.CitylistBean();
        }
        if (province == null) {
            Timber.e("ERROR: null input!");
            return new Location.CitylistBean();
        }

        for (Location.CitylistBean item : locationData.getCitylist()) {
            if (province.contains(item.getProvince())) {
                return item;
            }
        }

        Timber.e("Could not find the province. are you sure the input is correct?");
        return new Location.CitylistBean();
    }

    private void easySetAdapter(Spinner spinner, List<String> list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(R.layout.end_gravity_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }

    @Override
    public void getUserInfoSuccess(UserDetailInfoResponse response) {
        if ((response.occupation.isEmpty() || !response.age.isEmpty()) && "0".equals(response.gender)) {//初始状态下，gender为0不为空，需要消灭
            setGender("1");//默认男性
        } else {
            setGender(response.gender);
        }
        setJob(response.occupation);
        setAge(response.age);
        if (TextUtils.isEmpty(response.resideLocation) || response.resideLocation.trim().length() == 0) {//没有或全是空格
            getDefaultProvince();
        } else {
            String[] provAndCity = response.resideLocation.split(" ");
            switch (provAndCity.length){
                case 0:getDefaultProvince();
                    break;
                case 1:
                    checkSuccess(provAndCity[0], "");
                    break;
                case 2:
                    checkSuccess(provAndCity[0], provAndCity[1]);
                    break;
                default:getDefaultProvince();
                    break;
            }
        }
    }

    private void setGender(String gender) {
        if ("1".equals(gender) || TextUtils.isEmpty(gender)) {
            this.gender = "男";
            genderSpinner.setSelection(0);//假定顺序为“男、女、其他”
        } else {
            this.gender = "女";
            genderSpinner.setSelection(1);
        }
    }

    private void setJob(String occupation) {
        if (!TextUtils.isEmpty(occupation)) {
            for (int i = 0; i < jobItems.length; i++) {
                if (occupation.equals(jobItems[i])) {
                    this.job = occupation;
                    jobSpinner.setSelection(i);
                    return;
                }
            }
        }
    }

    private void setAge(String age) {
        if (!TextUtils.isEmpty(age)) {
            age = age.replace("s", "");

            for (int i = 0; i < ageItems.length; i++) {
                if (ageItems[i].contains(age)) {
                    this.age = age;
                    ageSpinner.setSelection(i);
                    return;
                }
            }
        }
    }

    @Override
    public void checkSuccess(String province, String city) {
        Timber.d("CheckIPTest: province:%s, city:%s", province, city);
        if (province==null||city==null){
            return;
        }
        if (isProvinceExist(province)) {
            if (isCityInProvince(city, province)) {
                setAllLocation(city, province);
            } else {
                String defaultCity = getCityList(province).get(0);
                setAllLocation(defaultCity, province);
            }
        } else {
            String tryProvince = whereCityExist(city);
            if (!tryProvince.equals(FAIL_TO_FIND_CITY)) {
                setAllLocation(city, tryProvince);
            }
        }
    }


    private void setAllLocation(String city, String province) {
        provinceLoc = province;
        cityLoc = city;
        int cityLocationNum = getCityLocationNum(city);
        int provinceLocationNum = getProvinceLocationNum(province);
        //tempCityLocNum = cityLocationNum;
        locationSpinner.setSelection(provinceLocationNum);

        Message msg = new Message();
        msg.what = 0;
        msg.arg1 = cityLocationNum;
        msg.arg2 = provinceLocationNum;
        citySetHandler.sendMessageDelayed(msg, 100);

        provinceLoc = locationSpinner.getSelectedItem().toString();
        cityLoc = locationCitySpinner.getSelectedItem().toString();
    }

//    msg.arg1 = cityLocationNum;
//    msg.arg2 = provinceLocationNum;
    Handler citySetHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            String newProv = locationCitySpinner.getSelectedItem().toString();
            String centerCity = locationData.getCitylist().get(msg.arg2).getCityBean().get(0).getCity();
            Timber.d("citySetHandler: %s, %s", newProv, centerCity);

            if (locationCitySpinner.getSelectedItem().toString().equals
                    (locationData.getCitylist().get(msg.arg2).getCityBean().get(0).getCity())) {
                locationCitySpinner.setSelection(msg.arg1);
                citySetHandler.removeCallbacksAndMessages(null);
            } else {
                Message newMsg = new Message();
                newMsg.what = 0;
                newMsg.arg1 =  msg.arg1;
                newMsg.arg2 =  msg.arg2;

                citySetHandler.sendMessageDelayed(newMsg, 100);
            }
            return false;
        }
    });

    private String whereCityExist(String city) {
        for (Location.CitylistBean citylist : locationData.getCitylist()) {
            String province = citylist.getProvince();

            for (String cityData : citylist.getCityStringList()) {
                if (city.contains(cityData)) {
                    return province;
                }
            }
        }

        return FAIL_TO_FIND_CITY;
    }

    private boolean isCityInProvince(String city, String province) {
        List<String> checkList = getCityList(province);
        for(String item : checkList) {
            if (city.contains(item)) {
                return true;
            }
        }

        return false;
    }

    private boolean isProvinceExist(String province) {
        boolean result=false;
        try {
            List<String> checkList = getAllProvince();
            for (String i : checkList) {
                if (province.contains(i)) {
                    result = true;
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void showMessage(String msg) {
        ToastFactory.showShort(mContext, msg);
    }

    private boolean isUploaded = false;

    @Override
    public void uploadSuccess() {
        ToastFactory.showShort(mContext, "上传个人信息成功！");
        isUploaded = true;
        finish();
        ConfigManager.Instance().setAccountIsShowFulfill(UserInfoManager.getInstance().getUserId(), false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isUploaded) {
            Toast.makeText(mContext, "尚未完成个人信息，你可以前往个人中心继续完善", Toast.LENGTH_SHORT).show();
        }
        mPresenter.detachView();
        citySetHandler.removeCallbacksAndMessages(null);

        //ConfigManager.Instance().setAccountIsShowFulfill(false);
    }
}