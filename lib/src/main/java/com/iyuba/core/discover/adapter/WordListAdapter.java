package com.iyuba.core.discover.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.sqlite.mode.Word;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.core.common.widget.Player;
import com.iyuba.lib.R;

/**
 * 单词列表适配器
 *
 * @author 陈彤
 * @version 1.0
 */

public class WordListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Word> mList = new ArrayList<Word>();
	public boolean modeDelete = false;
	public ViewHolder viewHolder;
	Player player ;


	public WordListAdapter(Context context) {

		mContext = context;
		player= new Player(mContext, null);
	}

	public WordListAdapter(Context context, ArrayList<Word> list) {
		mContext = context;
		mList = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void setData(ArrayList<Word> list) {
		mList = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Word word = mList.get(position);
		final int pos = position;
		if (convertView == null) {
			convertView = View.inflate(mContext,R.layout.item_word_lib, null);
			viewHolder = new ViewHolder();
			viewHolder.key =  convertView.findViewById(R.id.word_key);
			viewHolder.pron =  convertView.findViewById(R.id.word_pron);
			viewHolder.key.setTextColor(Color.BLACK);
			viewHolder.def =  convertView.findViewById(R.id.word_def);
			viewHolder.deleteBox =  convertView
					.findViewById(R.id.checkBox_isDelete);
			viewHolder.speaker =  convertView
					.findViewById(R.id.word_speaker);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if( ConfigManager.Instance().isShowDef()){
			viewHolder.def.setVisibility(View.VISIBLE);
		}else {
			viewHolder.def.setVisibility(View.GONE);
		}

		if (modeDelete) {
			viewHolder.deleteBox.setVisibility(View.VISIBLE);
		} else {
			viewHolder.deleteBox.setVisibility(View.GONE);
		}
		if (mList.get(pos).isDelete) {
			viewHolder.deleteBox.setImageResource(R.drawable.ic_choose_on);
		} else {
			viewHolder.deleteBox.setImageResource(R.drawable.ic_choose_off);
		}
		viewHolder.key.setText(word.key);
		if (!TextUtils.isEmpty(word.pron)) {
			StringBuffer sb = new StringBuffer();
			sb.append('[').append(word.pron).append(']');
			viewHolder.pron.setText(TextAttr.decode(sb.toString()));
		} else {
			viewHolder.pron.setText("");
		}
		viewHolder.def.setText(word.def.replaceAll("\n", ""));
		if (word.audioUrl != null && word.audioUrl.length() != 0) {
			viewHolder.speaker.setVisibility(View.VISIBLE);
		} else {
			viewHolder.speaker.setVisibility(View.INVISIBLE);
		}
		viewHolder.speaker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = word.audioUrl;
				if (player!=null) {
					player.playUrl(url);
				}
			}
		});
		return convertView;
	}

	public class ViewHolder {
		TextView key, pron;
		public TextView def;
		ImageView deleteBox;
		ImageView speaker;
	}
}
