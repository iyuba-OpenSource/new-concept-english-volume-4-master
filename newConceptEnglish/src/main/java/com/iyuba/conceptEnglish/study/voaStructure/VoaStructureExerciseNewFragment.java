package com.iyuba.conceptEnglish.study.voaStructure;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.lil.concept_other.util.ConceptHomeRefreshUtil;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.mode.ExerciseRecord;
import com.iyuba.conceptEnglish.sqlite.mode.VoaStructureExercise;
import com.iyuba.conceptEnglish.sqlite.op.VoaStructureExerciseOp;
import com.iyuba.conceptEnglish.util.StringEqualsUtil;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.user.UserInfoManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @desction:
 * @date: 2023/3/20 16:00
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class VoaStructureExerciseNewFragment extends Fragment {

    //数据库操作
    private VoaStructureExerciseOp structureDb;
    //总的数据
    private Map<Integer, VoaStructureExercise> structureMap = new HashMap<>();
    //当前的数据
    private VoaStructureExercise structureExercise;
    //题目数据
    private List<String> titleList;
    //题干数据
    private List<String> descList;
    //成绩数据
    private Map<Integer,Map<Integer, ExerciseRecord>> exerciseRecordList;
    //分类型的成绩数据
    private Map<Integer,ExerciseRecord> singleMap;

    //当前课程的voaId
    private int voaId;
    //当前的位置
    private int position = 0;
    //多填空题目适配器
    private VoaStructureMultiAdapter adapter;
    //多填空答案适配器
    private VoaStructureMultiAnswerAdapter answerAdapter;


    //视图
    private RelativeLayout emptyLayout;
    private RelativeLayout contentLayout;

    private TextView titleView;
    private TextView descView;

    private RelativeLayout singleBlankLayout;
    private RelativeLayout singleUserAnswerLayout;
    private EditText singleUserAnswer;
    private RelativeLayout singleRightAnswerLayout;
    private TextView singleRightAnswer;

    private RelativeLayout multiBlankLayout;
    private TextView multiBlank;
    private RecyclerView multiBlankView;
    private LinearLayout noNoteAnswerLayout;
    private RecyclerView noNoteAnswerView;

    private RelativeLayout bottomLayout;
    private TextView indexView;
    private Button previousView;
    private Button nextView;
    private Button submitView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_exercise_voastrucrure,null);

        initView(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        initClick();

        refreshData();
    }

    private void initView(View rootView){
        emptyLayout = rootView.findViewById(R.id.emptyLayout);
        contentLayout = rootView.findViewById(R.id.contentLayout);

        titleView = rootView.findViewById(R.id.blank_type);
        descView = rootView.findViewById(R.id.question);

        singleBlankLayout = rootView.findViewById(R.id.singleBlankLayout);
        singleUserAnswer = rootView.findViewById(R.id.user_answer);
        singleRightAnswerLayout = rootView.findViewById(R.id.right_answer_body);
        singleRightAnswer = rootView.findViewById(R.id.right_answer);

        multiBlankLayout = rootView.findViewById(R.id.multiBlankLayout);
        multiBlank = rootView.findViewById(R.id.multi_blank);
        multiBlankView = rootView.findViewById(R.id.multi_blank_user);
        noNoteAnswerLayout = rootView.findViewById(R.id.multi_blank_no_note_layout);
        noNoteAnswerView = rootView.findViewById(R.id.noNoteRightAnswer);

        bottomLayout = rootView.findViewById(R.id.bottomLayout);
        indexView = rootView.findViewById(R.id.index);
        previousView = rootView.findViewById(R.id.previous);
        nextView = rootView.findViewById(R.id.next);
        submitView = rootView.findViewById(R.id.submit);
    }

    private void initData(){
        voaId = VoaDataManager.Instace().voaTemp.voaId;
        //数据库内容
        structureDb = new VoaStructureExerciseOp(getActivity());
        structureDb.updateOther();
        //总数据内容
        structureMap = structureDb.findData(voaId);
        //题目内容（可能有的没有题目）
        titleList = new ArrayList<>();
        //题干内容（可能有的没有题干）
        descList = new ArrayList<>();
        //成绩数据
        exerciseRecordList = new HashMap<>();

        if (structureMap!=null&&structureMap.keySet().size()>0){
            for (Integer key:structureMap.keySet()){
                VoaStructureExercise temp = structureMap.get(key);

                //题目
                if (TextUtils.isEmpty(temp.descEN)&&TextUtils.isEmpty(temp.descCN)){
                    if (titleList.size()>0){
                        titleList.add(titleList.get(titleList.size()-1));
                    }else {
                        titleList.add("");
                    }
                }else {
                    StringBuilder builder = new StringBuilder();
                    if (TextUtils.isEmpty(temp.descEN)){
                        builder.append("");
                    }else {
                        builder.append(temp.descEN);
                    }

                    if (TextUtils.isEmpty(temp.descCN)){
                        builder.append("");
                    }else {
                        builder.append(temp.descCN);
                    }

                    titleList.add(builder.toString());
                }

                //题干
                if (!TextUtils.isEmpty(temp.note)){
                    descList.add(temp.number+". "+temp.note);
                }else {
                    descList.add("");
                }
            }
        }
    }

    private void initClick(){
        //上一个
        previousView.setOnClickListener(v->{
            if (position==0){
                CustomToast.showToast(getActivity(), "已是第一题", 1000);
                return;
            }

            position--;
            refreshData();
        });
        //下一个
        nextView.setOnClickListener(v->{
            if (position==structureMap.keySet().size()-1){
                CustomToast.showToast(getActivity(), "已是最后一题", 1000);
                return;
            }

            position++;
            refreshData();
        });
        //提交
        submitView.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()) {
                LoginUtil.startToLogin(getActivity());
            }else {
                submitAndRefresh();

                //设置首页刷新
                ConceptHomeRefreshUtil.getInstance().setRefreshState(true);
            }
        });
    }

    //刷新数据（无论上一个还是下一个）
    private void refreshData(){
        if (structureMap!=null&&structureMap.keySet().size()>0){
            showViewStatus(true);
            String index = (position+1)+"/"+structureMap.keySet().size();
            indexView.setText(index);

            //获取数据进行展示
            //题目
            String title = titleList.get(position);
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(VoaStructureUtil.transformString(title));
            titleView.setTextColor(Constant.normalColor);
            titleView.setTextSize(Constant.textSize);
            //题干
            String desc = descList.get(position);
            descView.setVisibility(View.VISIBLE);
            descView.setText(VoaStructureUtil.transformString(desc));
            descView.setTextColor(Constant.normalColor);
            descView.setTextSize(Constant.textSize);

            //类型判断
            structureExercise = structureMap.get(position);
            if (structureExercise!=null){
                singleMap = createRecord();

                if (structureExercise.quesNum>1){
                    //多个填空
                    showMultiBlank();
                }else {
                    //单个填空
                    showSingleBlank();
                }
            }
        }else {
            showViewStatus(false);
        }
    }

    //数据显示
    private void showViewStatus(boolean isShowContent){
        if (isShowContent){
            emptyLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
        }else {
            emptyLayout.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
        }
    }

    //单个填空题
    private void showSingleBlank(){
        descView.setVisibility(View.VISIBLE);
        singleUserAnswer.setBackgroundResource(R.drawable.gray_item);
        singleUserAnswer.setEnabled(true);
        singleRightAnswer.setBackgroundResource(R.drawable.gray_item);

        singleBlankLayout.setVisibility(View.VISIBLE);
        multiBlankLayout.setVisibility(View.GONE);

        if (exerciseRecordList==null||exerciseRecordList.size()==0){
            singleUserAnswer.setEnabled(true);
            singleRightAnswerLayout.setVisibility(View.GONE);
            singleUserAnswer.setText("");
            submitView.setVisibility(View.VISIBLE);
            return;
        }

        if (exerciseRecordList.get(position)==null){
            singleUserAnswer.setEnabled(true);
            singleRightAnswerLayout.setVisibility(View.GONE);
            singleUserAnswer.setText("");
            submitView.setVisibility(View.VISIBLE);
            return;
        }

        ExerciseRecord record = exerciseRecordList.get(position).get(1);
        if (record==null){
            singleUserAnswer.setEnabled(true);
            singleRightAnswerLayout.setVisibility(View.GONE);
            singleUserAnswer.setText("");
            submitView.setVisibility(View.VISIBLE);
            return;
        }

        //存在答案时的数据
        submitView.setVisibility(View.INVISIBLE);
        singleRightAnswerLayout.setVisibility(View.VISIBLE);
        singleUserAnswer.setEnabled(false);
        singleUserAnswer.setText(record.UserAnswer);
        singleRightAnswer.setText(record.RightAnswer);

        if (record.AnswerResut!=2){
            if (record.AnswerResut==1){
                singleUserAnswer.setBackgroundResource(R.drawable.green_item);
            }else {
                singleUserAnswer.setBackgroundResource(R.drawable.red_item);
            }
        }else {
            singleUserAnswer.setBackgroundResource(R.drawable.gray_item);
        }
    }

    //多个填空题
    private void showMultiBlank(){
        descView.setVisibility(View.GONE);

        singleBlankLayout.setVisibility(View.GONE);
        multiBlankLayout.setVisibility(View.VISIBLE);
        noNoteAnswerLayout.setVisibility(View.GONE);

        if (structureExercise==null){
            contentLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
            return;
        }

        //显示题目
        if (TextUtils.isEmpty(structureExercise.note)&&TextUtils.isEmpty(structureExercise.answer)){
            contentLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
            return;
        }

        if (exerciseRecordList!=null&&exerciseRecordList.size()>0){
            if (exerciseRecordList.get(position)!=null){
                //显示空格或者自己的数据
                Map<Integer,ExerciseRecord> multiData = exerciseRecordList.get(position);
                if (multiData!=null){
                    submitView.setVisibility(View.INVISIBLE);
                    setMultiBlankShow(VoaStructureUtil.transUserAnswerToMultiBlank(multiData));

                    //在没有note并且答案不为空的情况下，显示答案数据
                    if (TextUtils.isEmpty(structureExercise.note)&&!TextUtils.isEmpty(structureExercise.answer)){
                        setNoNoteMultiAnswerShow(VoaStructureUtil.transAnswerToMultiBlank(structureExercise.answer));
                    }

                    multiBlank.setText(VoaStructureUtil.transformPassageQuestionWithAnswer(structureExercise.note,structureExercise.answer));
                }
                return;
            }
        }

        submitView.setVisibility(View.VISIBLE);
        //显示空格数据
        if (TextUtils.isEmpty(structureExercise.note)){
            multiBlank.setText("");
        }else {
            multiBlank.setText(VoaStructureUtil.transformPassageQuestion(structureExercise.note));
        }

        setMultiBlankShow(VoaStructureUtil.transIntDataToMultiBlank(structureExercise.quesNum));
    }

    //设置多个空格或者自己的数据
    private void setMultiBlankShow(List<VoaStructureKVBean> list){
        multiBlankView.setItemViewCacheSize(list.size());

        if (adapter==null){
            adapter = new VoaStructureMultiAdapter(getActivity(),list);
            multiBlankView.setAdapter(adapter);
        }else {
            adapter.refreshData(list);
        }

        //根据note判断
        //如果没有note，则使用linear形式，否则使用grid形式
        RecyclerView.LayoutManager manager = null;
        if (TextUtils.isEmpty(structureExercise.note)){
            manager = new NoScrollLinearLayoutManager(getActivity(),false);
        }else {
            manager = new NoScrollGridLayoutManager(getActivity(),2,false);
        }
        multiBlankView.setLayoutManager(manager);
    }

    //设置多个空格的答案数据（仅限于没有note的情况下）
    private void setNoNoteMultiAnswerShow(List<String> answerList){
        if (answerList!=null&&answerList.size()>0){
            noNoteAnswerLayout.setVisibility(View.VISIBLE);

            noNoteAnswerView.setItemViewCacheSize(answerList.size());
            if (answerAdapter==null){
                answerAdapter = new VoaStructureMultiAnswerAdapter(getActivity(),answerList);
                noNoteAnswerView.setAdapter(answerAdapter);
            }else {
                answerAdapter.refreshData(answerList);
            }
        }else {
            noNoteAnswerView.setVisibility(View.GONE);
        }

        //根据note判断
        //如果没有note，则使用linear形式，否则使用grid形式
        RecyclerView.LayoutManager manager = null;
        if (TextUtils.isEmpty(structureExercise.note)){
            manager = new NoScrollLinearLayoutManager(getActivity(),false);
        }else {
            manager = new NoScrollGridLayoutManager(getActivity(),2,false);
        }
        noNoteAnswerView.setLayoutManager(manager);
    }

    //获取数据
    private Map<Integer,ExerciseRecord> getMultiRecordData(){
        Map<Integer,ExerciseRecord> multiData = new HashMap<>();

        List<RecyclerView.ViewHolder> holders = adapter.getHolder();
        for (int i = 0; i < holders.size(); i++) {
            VoaStructureMultiAdapter.VSNItemHolder holder = (VoaStructureMultiAdapter.VSNItemHolder) holders.get(i);
            String input = holder.editText.getText().toString().trim();

            ExerciseRecord record = new ExerciseRecord();
            record.UserAnswer = input;
            record.RightAnswer = singleMap.get(i+1).RightAnswer;

            if (record.RightAnswer.equals("")) {
                record.AnswerResut = -1;
            } else if (StringEqualsUtil.isEquals(record.RightAnswer.trim(),record.UserAnswer.trim())) {
                record.AnswerResut = 1;
            } else {
                record.AnswerResut = 0;
            }

            multiData.put(i,record);
        }

        return multiData;
    }

    //获取数据
    private Map<Integer,ExerciseRecord> getSingleRecordData(){
        String edit = singleUserAnswer.getText().toString().trim();
        String right = singleMap.get(1).RightAnswer;

        ExerciseRecord record = new ExerciseRecord();
        record.UserAnswer = edit;
        record.RightAnswer = right;

        if (record.RightAnswer.equals("")) {
            record.AnswerResut = -1;
        } else if (StringEqualsUtil.isEquals(record.RightAnswer.trim(),record.UserAnswer.trim())) {
            record.AnswerResut = 1;
        } else {
            record.AnswerResut = 0;
        }

        Map<Integer,ExerciseRecord> singleData = new HashMap<>();
        singleData.put(1,record);
        return singleData;
    }

    //拆分数据显示
    private Map<Integer,ExerciseRecord> createRecord() {
        Map<Integer, ExerciseRecord> recordMap = new HashMap<>();

        String[] answer = structureExercise.answer.split("###");

        int questionNum = structureExercise.quesNum;
        Log.e("我想要的数据", questionNum + "====");
        if (questionNum > 1) {
            for (int i = 1; i <= questionNum; i++) {
                ExerciseRecord record = new ExerciseRecord();
                record.voaId = voaId;
                record.TestNumber = i;
                try {
                    record.RightAnswer = (i-1)<answer.length?answer[i - 1]:answer[i];
                } catch (Exception e) {
                    e.printStackTrace();
                }
                record.AnswerResut = 2; // 未提交
                recordMap.put(i, record);
            }
        } else {
            ExerciseRecord record = new ExerciseRecord();
            record.voaId = voaId;
            record.TestNumber = 1;
            record.RightAnswer = answer[0];

            recordMap.put(1, record);
        }

        return recordMap;
    }

    //提交数据并刷新
    private void submitAndRefresh(){
        if (structureExercise.quesNum>1){
            exerciseRecordList.put(position,getMultiRecordData());
        }else {
            exerciseRecordList.put(position,getSingleRecordData());
        }

        refreshData();
    }
}
