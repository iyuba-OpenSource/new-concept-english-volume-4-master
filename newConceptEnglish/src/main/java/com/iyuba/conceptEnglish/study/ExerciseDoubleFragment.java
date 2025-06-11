package com.iyuba.conceptEnglish.study;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.configation.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ExerciseDoubleFragment extends Fragment {

    private static String IMAGE_URL = "http://static2." + Constant.IYUBA_CN + "newconcept/images/";
    private static final String PNG = ".png";
    private Context mContext;
    Unbinder unbinder;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    private ExerciseDoubleAdapter adapter;
    private View rootView;
    private int voaId;
    private VoaOp voaOp;

    private List<String> list = new ArrayList<>();

    public void onCreate(@Nullable Bundle paramBundle) {
        super.onCreate(paramBundle);
        this.mContext = getActivity();
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater paramLayoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle paramBundle) {
        if (rootView == null) {
            rootView = paramLayoutInflater.inflate(R.layout.fragment_exercise_double, viewGroup, false);
        }
        unbinder = ButterKnife.bind(this, rootView);

        voaOp = new VoaOp(mContext);
        voaId = VoaDataManager.Instace().voaTemp.voaId;
        int picNum = Integer.parseInt(voaOp.findDataById(voaId).pic);
        for (int i = 0; i < picNum; i++) {
            list.add(IMAGE_URL + voaId + "/" + (i + 1) + PNG);
//           http://static2."+Constant.IYUBA_CN+"newconcept/images/1002/1.png
        }
        initRecyclerView();
        return rootView;
    }

    private void initRecyclerView() {
        adapter = new ExerciseDoubleAdapter(list, mContext);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter);
    }


    public void onDestroyView() {
        super.onDestroyView();
        this.unbinder.unbind();
    }

    public void refreshData() {
        voaId = VoaDataManager.Instace().voaTemp.voaId;
        int picNum = Integer.parseInt(voaOp.findDataById(voaId).pic);
        for (int i = 0; i < picNum; i++) {
            list.add(IMAGE_URL + voaId + "/" + (i + 1) + PNG);
        }
        adapter.notifyDataSetChanged();
    }

}
