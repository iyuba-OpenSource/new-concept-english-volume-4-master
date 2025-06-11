package com.iyuba.conceptEnglish.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.adapter.AnnotationAdapter;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.mode.VoaAnnotation;
import com.iyuba.conceptEnglish.sqlite.op.AnnotationOp;
import com.iyuba.core.common.base.BasisActivity;


public class VoaAnnotationActivity extends BasisActivity {
	
	private ListView annoListView;
	private View noAnnotationView;
	private List<VoaAnnotation> voaAnnos;
	private AnnotationAdapter annosAdapter;
	private Context mContext;
	private AnnotationOp annotationOp = new AnnotationOp(mContext);
	private int voaId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.voa_annotations);
		
		mContext = this;
		
		init();
	}
	
	public void init() {
		noAnnotationView = findViewById(R.id.no_annotation_view);
		annoListView = (ListView) findViewById(R.id.annotation_list);
	}

	private void initVoaAnnotation() {
		voaId = VoaDataManager.Instace().voaTemp.voaId;
		voaAnnos = annotationOp.findDataByVoaId(voaId);
		if(voaAnnos.size() == 0) {
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
