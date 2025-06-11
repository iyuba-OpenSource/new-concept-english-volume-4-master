package com.iyuba.core.talkshow.talkClass;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.iyuba.core.common.data.model.TalkClass;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.event.TalkClassEvent;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.lib.R;
import com.iyuba.lib.R2;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TalkClassActivity extends AppCompatActivity implements TalkClassMvpView {

    Toolbar toolbar;
    GridView gridView;

    private TalkClassPresenter mPresenter;
    private Context mContext;

    private List<Map<String, Object>> data_list;
    private SimpleAdapter simAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_class);
        initView();

        mContext = this;
        setSupportActionBar(toolbar);
        mPresenter = new TalkClassPresenter();
        mPresenter.attachView(this);
        mPresenter.getLessonList("321");//客栈
    }

    private void initView(){
        toolbar = findViewById(R.id.toolbar);
        gridView = findViewById(R.id.grid_view);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void showMessage(String str) {
        ToastUtil.showToast(mContext, str);
    }

    @Override
    public void getLesson(final List<TalkClass> resetData) {
//        final List<TalkClass> resetData = new ArrayList<>(list);
//        Collections.sort(resetData,new Comparator<TalkClass>() {
//            @Override
//            public int compare(TalkClass o1,TalkClass o2) {
//                return o1.Id.compareTo(o2.Id);
//            }
//        });

        data_list = new ArrayList<>();
        String[] from = {"image", "text"};
        int[] to = {R.id.iv_icon, R.id.tv_text};
        simAdapter = new SimpleAdapter(mContext, getData(resetData), R.layout.grid_lesson, from, to);
        simAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view instanceof ImageView) {
                    ImageView imageView = (ImageView) view;
                    String path = String.valueOf(data);
                    /*Drawable drawable = mContext.getResources().getDrawable(R.drawable.loading);
                    Glide.with(mContext).load(path)
                            .asBitmap()
                            .placeholder(drawable)
                            .error(drawable)
                            .dontAnimate()  //防止加载网络图片变形
                            .into(imageView);*/
                    LibGlide3Util.loadImg(mContext,path,R.drawable.loading,imageView);
                    return true;
                } else {
                    return false;
                }
            }
        });
        gridView.setAdapter(simAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String classId = resetData.get(position).Id;
                String className = resetData.get(position).SeriesName;
                Intent intent =getIntent();
                intent.putExtra("classId",classId);
                intent.putExtra("className",className);
                setResult(RESULT_OK,intent);
                EventBus.getDefault().post(new TalkClassEvent(classId,className));
                finish();
            }
        });
    }

    public List<Map<String, Object>> getData(List<TalkClass> list) {
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", list.get(i).pic);
            map.put("text", list.get(i).SeriesName);
            data_list.add(map);
        }
        return data_list;
    }
}
