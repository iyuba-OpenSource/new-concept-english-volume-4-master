package com.iyuba.core.talkshow.lesson.recommend;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.widget.divder.NormalGridItemDivider;
import com.iyuba.lib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐 fragment
 */
public class RecommendFragment extends Fragment {

    private static final String VOA_LIST = "voa_list";
    private static final String VOA_ID = "voa_id";
    private static final int SPAN_COUNT = 2;

    public static RecommendFragment newInstance(List<TalkLesson> list,String voaId) {
        RecommendFragment fragment = new RecommendFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(VOA_LIST, (ArrayList<? extends Parcelable>) list);
        args.putString(VOA_ID,voaId);
        fragment.setArguments(args);
        return fragment;
    }

    RecyclerView mRecyclerView;

    private List<TalkLesson> mList;
    private String  mVoaId;
    private RecommendAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            mList = getArguments().getParcelableArrayList(VOA_LIST);
            mVoaId = getArguments().getString(VOA_ID);
        }
        mAdapter= new RecommendAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_recommend, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
        NormalGridItemDivider divider = new NormalGridItemDivider(getContext());
        divider.setDivider(getResources().getDrawable(R.drawable.voa_activity_divider));
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。

        //mAdapter.setmVoaCallback(callback);
        mRecyclerView.setAdapter(mAdapter);
        return  view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mList!=null){
            mAdapter.setVoaList(mList,mVoaId);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void upData(List<TalkLesson> list,String voaId){
        mAdapter.setVoaList(list,voaId);
        mAdapter.notifyDataSetChanged();
    }
}
