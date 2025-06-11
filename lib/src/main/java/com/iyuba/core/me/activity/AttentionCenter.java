package com.iyuba.core.me.activity;

/**
 * 关注界面
 *
 * @author chentong
 * @version 1.0
 * @para "userid" 当前用户userid（本人或其他人个人主页）
 */
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.SocialDataManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.message.RequestAttentionList;
import com.iyuba.core.common.protocol.message.ResponseAttentionList;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.ExeRefreshTime;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshView;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshView.OnFooterRefreshListener;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshView.OnHeaderRefreshListener;
import com.iyuba.core.me.adapter.AttentionListAdapter;
import com.iyuba.core.me.sqlite.mode.Attention;
import com.iyuba.lib.R;

import java.util.ArrayList;

import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;

public class AttentionCenter extends BasisActivity implements
		OnHeaderRefreshListener, OnFooterRefreshListener {
	private String currUserid;
	private Button backButton;
	private Context mContext;
	private CustomDialog waitingDialog;
	private Boolean isLastPage = false;
	private Boolean isTopRefresh = false;
	private Boolean isFootRefresh = false;
	private ListView fansList;// 新闻列表
	private PullToRefreshView refreshView;// 刷新列表
	private AttentionListAdapter adapter;
	private ArrayList<Attention> attentionArrayList = new ArrayList<Attention>();
	private int curPage = 1;
	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fanslist);
		CrashApplication.getInstance().addActivity(this);
		mContext = this;
		currUserid = this.getIntent().getStringExtra("userid");
		//currUserid = AccountManager.Instace(mContext).userId;;
		initWidget();
		waitingDialog = WaittingDialog.showDialog(mContext);
	}

	private void initWidget() {
		// TODO Auto-generated method stub
		backButton = (Button) findViewById(R.id.button_back);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				onBackPressed();
			}
		});
		title = (TextView) findViewById(R.id.title);
		title.setText(R.string.me_attention_text);
		initFansView();
	}

	private void initFansView() {
		// TODO Auto-generated method stub
		fansList = (ListView) findViewById(R.id.list);
		refreshView = (PullToRefreshView) findViewById(R.id.listview);
		refreshView.setOnHeaderRefreshListener(this);
		refreshView.setOnFooterRefreshListener(this);
		adapter = new AttentionListAdapter(mContext,attentionArrayList);
		fansList.setAdapter(adapter);
		handler.sendEmptyMessage(0);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				curPage = 1;
				attentionArrayList.clear();
				handler.sendEmptyMessage(1);
				handler.sendEmptyMessage(2);
				break;
			case 1:
				ExeProtocol.exe(
						new RequestAttentionList(currUserid, String
								.valueOf(curPage)), new ProtocolResponse() {
							@Override
							public void finish(BaseHttpResponse bhr) {
								// TODO Auto-generated method stub
								ResponseAttentionList res = (ResponseAttentionList) bhr;
								if (res.result.equals("550")) {
									attentionArrayList.clear();
									Log.e("tag",res.fansList.size()+"---");
									attentionArrayList.addAll(res.fansList);
									adapter.setData(attentionArrayList);
									if (attentionArrayList.size() >= res.num) {
										isLastPage = true;
									}
								} else {
								}
								curPage += 1;
								handler.sendEmptyMessage(4);
							}

							@Override
							public void error() {
								// TODO Auto-generated method stub
								Log.e("Tag","ERROR");
								handler.sendEmptyMessage(3);
								handler.sendEmptyMessage(5);
							}
						});
				break;
			case 2:
				waitingDialog.show();
				break;
			case 3:
				waitingDialog.dismiss();
				break;
			case 4:
				handler.sendEmptyMessage(3);
				Log.e("TagSize",attentionArrayList.size()+"");
				adapter.notifyDataSetChanged();
				if (isTopRefresh) {
					refreshView.onHeaderRefreshComplete();
				} else if (isFootRefresh) {
					refreshView.onFooterRefreshComplete();
				}
				setListener();
				break;
			case 5:
				CustomToast.showToast(mContext, R.string.check_network);
				break;
			default:
				break;
			}
		}
	};

	private void setListener() {
		fansList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				SocialDataManager.Instance().attention = attentionArrayList.get(position);

//				Intent intent = new Intent();
//				intent.setClass(mContext, PersonalHome.class);
//				SocialDataManager.Instance().userid = attentionArrayList.get(position).followuid;
//				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				startActivity(intent);

				startActivity(PersonalHomeActivity.buildIntent(mContext, Integer.parseInt(attentionArrayList.get(position).followuid), attentionArrayList.get(position).fusername, 0));

			}
		});
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		if (!isLastPage) {
			handler.sendEmptyMessage(1);
			isFootRefresh = true;
		} else {
			refreshView.onFooterRefreshComplete();
		}
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(0);
		refreshView.setLastUpdated(ExeRefreshTime
				.lastRefreshTime("AttentionCenter"));
		isTopRefresh = true;
	}
}
