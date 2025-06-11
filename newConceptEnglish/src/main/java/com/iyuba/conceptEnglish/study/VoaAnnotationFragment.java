package com.iyuba.conceptEnglish.study;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.adapter.AnnotationAdapter;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.mode.VoaAnnotation;
import com.iyuba.conceptEnglish.sqlite.op.AnnotationOp;

import java.util.ArrayList;
import java.util.List;


public class VoaAnnotationFragment extends Fragment {

    private ListView annoListView;
    private View noAnnotationView;
    private List<VoaAnnotation> voaAnnos;
    private AnnotationAdapter annosAdapter;
    private Context mContext;
    private AnnotationOp annotationOp = new AnnotationOp(mContext);
    private int voaId;

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.voa_annotations, container, false);
        }
        mContext = getActivity();

        init();
        return rootView;
    }


    public void init() {
        noAnnotationView = rootView.findViewById(R.id.no_annotation_view);
        annoListView = (ListView) rootView.findViewById(R.id.annotation_list);
    }

    private void initVoaAnnotation() {
        voaId = VoaDataManager.Instace().voaTemp.voaId;
        voaAnnos = annotationOp.findDataByVoaId(voaId);
        if (voaAnnos.size() == 0) {
            noAnnotationView.setVisibility(View.VISIBLE);
            annoListView.setVisibility(View.GONE);
        } else {
            annoListView.setVisibility(View.VISIBLE);
            noAnnotationView.setVisibility(View.GONE);
            annosAdapter = new AnnotationAdapter(mContext, (ArrayList<VoaAnnotation>) voaAnnos);
            annoListView.setAdapter(annosAdapter);
        }

    }

    public void onResume() {
        initVoaAnnotation();
        super.onResume();
    }
}
