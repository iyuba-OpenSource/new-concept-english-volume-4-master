package com.iyuba.conceptEnglish.lil.concept_other.book_choose;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.activity.BookDownloadActivity;
import com.iyuba.conceptEnglish.databinding.BookChooseBinding;
import com.iyuba.conceptEnglish.entity.YouthBookEntity;
import com.iyuba.conceptEnglish.event.RefreshBookEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayEvent;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlaySession;
import com.iyuba.conceptEnglish.sqlite.op.BookTableOp;
import com.iyuba.configation.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 相关功能参考
 * BookChooseActivity这个界面
 */
public class ConceptBookChooseActivity extends BaseViewBindingActivity<BookChooseBinding> {


    //当前选中的数据类型
    private String curSelectedType = TypeLibrary.BookType.conceptFourUS;

    //三个类型的数据
    List<ConceptBookChooseBean> fourVolumeList = new ArrayList<>();
    List<ConceptBookChooseBean> youthBookList = new ArrayList<>();

    //适配器
    private ConceptBookChooseAdapter chooseAdapter;

    public static void start(Context context, int isFirstInfo) {
        Intent intent = new Intent();
        intent.setClass(context, ConceptBookChooseActivity.class);
        intent.putExtra("isFirstInfo", isFirstInfo);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initToolbar();
        initList();
        initClick();
        initData();

        checkData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /***********************初始化********************/
    private void initToolbar() {
        binding.buttonBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void initList() {
        chooseAdapter = new ConceptBookChooseAdapter(new ArrayList<>(), this);
        binding.recycler.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recycler.setAdapter(chooseAdapter);
        chooseAdapter.setOnClickListener(new ConceptBookChooseAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                ConceptBookChooseBean chooseBean = chooseAdapter.getSelectBean(position);
                if (chooseBean==null){
                    ToastUtil.showToast(ConceptBookChooseActivity.this,"选中数据错误");
                    return;
                }

                //根据类型处理下相关后台播放操作
                //这里先获取之前的数据，然后对比现在的数据即可
                String preBookType = ConceptBookChooseManager.getInstance().getBookType();
                int preBookId = ConceptBookChooseManager.getInstance().getBookId();
                if (!preBookType.equals(curSelectedType)
                        ||preBookId!=chooseBean.getBookId()){
                    //停止控制显示
                    EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_hide));
                    //删除数据
                    ConceptBgPlaySession.getInstance().setPlayPosition(-1);
                    ConceptBgPlaySession.getInstance().setVoaList(null);
                }

                //设置选中的数据
                ConceptBookChooseManager.getInstance().setBookType(curSelectedType);
                ConceptBookChooseManager.getInstance().setBookId(chooseBean.getBookId());
                ConceptBookChooseManager.getInstance().setBookName(getBookName(curSelectedType,chooseBean));
                //刷新数据显示
                EventBus.getDefault().post(new RefreshBookEvent());

                //针对个别包名，刷新单词显示
                if (getPackageName().equals(Constant.package_learnNewEnglish) || getPackageName().equals(Constant.package_conceptStory)){
                    //刷新单词的内容
                    EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.concept_word));
                }
                //回退
                finish();
            }
        });
    }

    private void initClick() {
        //是否是第一次进来
        int isFirst = getIntent().getIntExtra("isFirstInfo",1);
        if (isFirst == 0){
            binding.buttonBack.setVisibility(View.GONE);
        }

        binding.download.setVisibility(View.GONE);
        binding.download.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(this, BookDownloadActivity.class);
            startActivity(intent);
        });

        binding.selectGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.america_pronunciation://美音
                        curSelectedType = TypeLibrary.BookType.conceptFourUS;
                        switchType(TypeLibrary.BookType.conceptFourUS);
                        chooseAdapter.refreshData(fourVolumeList);
                        break;
                    case R.id.english_pronunciation://英音
                        curSelectedType = TypeLibrary.BookType.conceptFourUK;
                        switchType(TypeLibrary.BookType.conceptFourUK);
                        chooseAdapter.refreshData(fourVolumeList);
                        break;
                    case R.id.youth://青少版
                        curSelectedType = TypeLibrary.BookType.conceptJunior;
                        switchType(TypeLibrary.BookType.conceptJunior);
                        chooseAdapter.refreshData(youthBookList);
                        break;
                }
            }
        });
    }

    private void initData() {
        //全四册
        fourVolumeList.add(new ConceptBookChooseBean(1, R.drawable.icon_book_1, null, "新概念第一册", 0));
        fourVolumeList.add(new ConceptBookChooseBean(2, R.drawable.icon_book_2, null, "新概念第二册", 0));
        fourVolumeList.add(new ConceptBookChooseBean(3, R.drawable.icon_book_3, null, "新概念第三册", 0));
        fourVolumeList.add(new ConceptBookChooseBean(4, R.drawable.icon_book_4, null, "新概念第四册", 0));

        //青少版
        List<YouthBookEntity> tempList = new BookTableOp(this).selectAllData();
        for (YouthBookEntity entity : tempList) {
            youthBookList.add(new ConceptBookChooseBean(entity.Id, -1, entity.pic, "新概念英语青少版" + entity.DescCn, entity.version));
        }
    }

    /************************获取数据**********************/
    private void checkData() {

        curSelectedType = ConceptBookChooseManager.getInstance().getBookType();
        switchType(curSelectedType);

        //获取数据显示
        if (curSelectedType.equals(TypeLibrary.BookType.conceptFourUS)
                || curSelectedType.equals(TypeLibrary.BookType.conceptFourUK)) {
            //全四册
            chooseAdapter.refreshData(fourVolumeList);
        } else {
            //青少版
            chooseAdapter.refreshData(youthBookList);
        }
    }

    /*************************辅助功能**************************/
    private void radioButtonTextColorChange(int americanColor, int englishColor, int youthColor) {
        binding.americaPronunciation.setTextColor(americanColor);
        binding.englishPronunciation.setTextColor(englishColor);
        binding.youth.setTextColor(youthColor);
    }

    //切换类型
    private void switchType(String selectType) {
        //根据数据类型显示
        switch (selectType) {
            case TypeLibrary.BookType.conceptFourUS://美语
                binding.selectGroup.check(R.id.america_pronunciation);
                radioButtonTextColorChange(getResources().getColor(R.color.white), getResources().getColor(R.color.bookChooseUncheck),
                        getResources().getColor(R.color.bookChooseUncheck));
                break;
            case TypeLibrary.BookType.conceptFourUK://英语
                binding.selectGroup.check(R.id.english_pronunciation);
                radioButtonTextColorChange(getResources().getColor(R.color.bookChooseUncheck), getResources().getColor(R.color.white),
                        getResources().getColor(R.color.bookChooseUncheck));
                break;
            case TypeLibrary.BookType.conceptJunior://青少版
                binding.selectGroup.check(R.id.youth);
                radioButtonTextColorChange(getResources().getColor(R.color.bookChooseUncheck), getResources().getColor(R.color.bookChooseUncheck),
                        getResources().getColor(R.color.white));
                break;
        }
    }

    //保存书籍的标题显示
    private String getBookName(String curType,ConceptBookChooseBean chooseBean){
        if (curType.equals(TypeLibrary.BookType.conceptJunior)){
            return chooseBean.getTitle();
        }

        String bookName = "新概念英语";
        switch (chooseBean.getBookId()){
            case 1:
                bookName = "新概念英语一";
                break;
            case 2:
                bookName = "新概念英语二";
                break;
            case 3:
                bookName = "新概念英语三";
                break;
            case 4:
                bookName = "新概念英语四";
                break;
            default:
                break;
        }

        if (curType.equals(TypeLibrary.BookType.conceptFourUS)){
            bookName+="（美音）";
        }else if (curType.equals(TypeLibrary.BookType.conceptFourUK)){
            bookName+="（英音）";
        }
        return bookName;
    }
}