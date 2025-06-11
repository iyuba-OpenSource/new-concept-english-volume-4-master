package com.iyuba.conceptEnglish.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDiffcultyExercise;

public class SpecialDiffcultyExerciseAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<VoaDiffcultyExercise> mList = new ArrayList<VoaDiffcultyExercise>();
	public boolean modeDelete = false;
	public ViewHolder viewHolder;

	public SpecialDiffcultyExerciseAdapter(Context context, ArrayList<VoaDiffcultyExercise> list) {
		mContext = context;
		mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final VoaDiffcultyExercise exercise = mList.get(position);
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			convertView = vi.inflate(R.layout.listitem_voa_annotation, null);
			
			viewHolder = new ViewHolder();
			viewHolder.descENText = (TextView) convertView.findViewById(R.id.anno_N);
			viewHolder.descCNText = (TextView) convertView.findViewById(R.id.anno_N);
			viewHolder.numberText = (TextView) convertView.findViewById(R.id.note);
			viewHolder.noteText = (TextView) convertView.findViewById(R.id.anno_N);
			viewHolder.typeText = (TextView) convertView.findViewById(R.id.anno_N);
			viewHolder.quesNumText = (TextView) convertView.findViewById(R.id.anno_N);
			viewHolder.answerText = (TextView) convertView.findViewById(R.id.anno_N);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.descENText.setText(exercise.descEN);
		viewHolder.descCNText.setText(Html.fromHtml(exercise.descCN));
		viewHolder.numberText.setText(exercise.number);
		viewHolder.noteText.setText(exercise.note);
		viewHolder.typeText.setText(exercise.type);
		viewHolder.quesNumText.setText(exercise.quesNum);
		viewHolder.answerText.setText(exercise.answer);
		
		return convertView;
	}
	
	public class ViewHolder {
		TextView descENText;
		TextView descCNText;
		TextView numberText;
		TextView noteText;
		TextView typeText;
		TextView quesNumText;
		TextView answerText;
	}
}
