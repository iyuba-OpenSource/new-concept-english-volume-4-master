//package com.iyuba.conceptEnglish.fragment;
//
//import android.app.AlertDialog;
//import android.app.AlertDialog.Builder;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.media.AudioManager;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.TextView;
//
//import androidx.fragment.app.Fragment;
//
//import com.iyuba.conceptEnglish.R;
//import com.iyuba.conceptEnglish.activity.AboutActivity;
//import com.iyuba.conceptEnglish.activity.FeedbackActivity;
//import com.iyuba.conceptEnglish.activity.FileBrowserActivity;
//import com.iyuba.conceptEnglish.manager.DownloadStateManager;
//import com.iyuba.conceptEnglish.protocol.NewInfoRequest;
//import com.iyuba.conceptEnglish.protocol.NewInfoResponse;
//import com.iyuba.conceptEnglish.protocol.ShareRequest;
//import com.iyuba.conceptEnglish.sqlite.mode.Book;
//import com.iyuba.conceptEnglish.sqlite.mode.DownloadInfo;
//import com.iyuba.conceptEnglish.sqlite.mode.Voa;
//import com.iyuba.conceptEnglish.sqlite.op.BookOp;
//import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
//import com.iyuba.conceptEnglish.util.UtilFile;
//import com.iyuba.conceptEnglish.widget.SleepDialog;
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.network.ClientSession;
//import com.iyuba.core.common.network.IResponseReceiver;
//import com.iyuba.core.common.protocol.BaseHttpRequest;
//import com.iyuba.core.common.protocol.BaseHttpResponse;
//import com.iyuba.core.common.setting.SettingConfig;
//import com.iyuba.core.common.util.FileSize;
//import com.iyuba.core.common.util.MD5;
//import com.iyuba.core.common.widget.dialog.CustomToast;
//import com.iyuba.core.lil.user.UserInfoManager;
//import com.umeng.analytics.MobclickAgent;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//public class SetFragment extends Fragment {
//	private Context mContext;
//	private View root;
//
//	private CheckBox CheckBoxHighSpeedDwonload, checkBoxPushMessage, CheckBoxScreenLit,
//			CheckBoxSyncho;
//	private View btnHighSpeedDownload, btnScreenLit, btnPushMessage, btnSyncho, sleepButton, pathButton,
//			btnClearPic, btnClearVideo, recommendButton, aboutBtn, feedbackBtn;
//	private TextView picSize, soundSize;
//	private static int hour, minute, totaltime, volume;// total用于计算时间，volume用于调整音量,睡眠模式用到的
//	private static boolean isSleep = false;// 睡眠模式是否开启
//	private String lastSavingPath, nowSavingPath;
//
//	private ArrayList<Voa> voaList = new ArrayList<Voa>();
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		root = inflater.inflate(R.layout.setting, container, false);
//		return root;
//	}
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		mContext = getActivity();
//	}
//
//	public void initCheckBox() {
//		CheckBoxHighSpeedDwonload = (CheckBox) root.findViewById(R.id.CheckBox_high_speed_download);
//		CheckBoxHighSpeedDwonload.setChecked(SettingConfig.Instance().isHighSpeed());
//		CheckBoxHighSpeedDwonload.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (UserInfoManager.getInstance().isVip()) {
//					if (SettingConfig.Instance().isHighSpeed()) {
//						SettingConfig.Instance().setHighSpeed(false);
//					} else {
//						SettingConfig.Instance().setHighSpeed(true);
//					}
//
//					CheckBoxHighSpeedDwonload.setChecked(SettingConfig.Instance().isHighSpeed());
//				} else {
//					CheckBoxHighSpeedDwonload.setChecked(false);
//					AlertDialog.Builder builder = new Builder(mContext);
//					builder.setTitle("提示");
//					builder.setPositiveButton("确定", null);
//					builder.setIcon(android.R.drawable.ic_dialog_info);
//					builder.setMessage(getResources().getString(
//							R.string.high_speed_download_toast));
//					builder.show();
//				}
//			}
//		});
//
//		checkBoxPushMessage = (CheckBox) root.findViewById(R.id.CheckBox_PushMessage);
//		checkBoxPushMessage.setChecked(SettingConfig.Instance().isPush());
//		checkBoxPushMessage.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//				setPush();
//			}
//		});
//
//		CheckBoxScreenLit = (CheckBox) root.findViewById(R.id.CheckBox_ScreenLit);
//		CheckBoxScreenLit.setChecked(SettingConfig.Instance().isLight());
//		CheckBoxScreenLit
//				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView,
//							boolean isChecked) {
//						SettingConfig.Instance().setLight(isChecked);
//					}
//				});
//
//		CheckBoxSyncho = (CheckBox) root
//				.findViewById(R.id.CheckBox_auto_syncho);
//		CheckBoxSyncho.setChecked(SettingConfig.Instance().isSyncho());
//		CheckBoxSyncho
//				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView,
//							boolean isChecked) {
//						SettingConfig.Instance().setSyncho(isChecked);
//					}
//				});
//	}
//
//
//	public void initWidget() {
//		btnHighSpeedDownload = root.findViewById(R.id.btn_high_speed_download);
//		btnScreenLit = root.findViewById(R.id.btn_screen_lit);
//		btnPushMessage = root.findViewById(R.id.btn_push_message);
//		btnSyncho = root.findViewById(R.id.btn_auto_syncho);
//		sleepButton = root.findViewById(R.id.sleep_mod);
//		pathButton = root.findViewById(R.id.setting_saving_path);
//		btnClearPic = root.findViewById(R.id.clear_pic);
//		btnClearVideo = root.findViewById(R.id.clear_video);
//
//		recommendButton = root.findViewById(R.id.recommend_btn);
//		aboutBtn = root.findViewById(R.id.about_btn);
//		feedbackBtn = root.findViewById(R.id.feedback_btn);
//
//		initCacheSize();
//		initListener();
//	}
//
//	public void initCacheSize() {
//		picSize = (TextView) root.findViewById(R.id.picSize);
//		soundSize = (TextView) root.findViewById(R.id.soundSize);
//		new Thread(new Runnable() {
//			// 获取图片大小
//			@Override
//			public void run() {
//				String strings;
//				strings = getSize(0);
//				handler.obtainMessage(0, strings).sendToTarget();
//
//			}
//		}).start();
//
//		new Thread(new Runnable() {
//			// 获取音频大小
//			@Override
//			public void run() {
//				String strings;
//				strings = getSize(1);
//				handler.obtainMessage(1, strings).sendToTarget();
//
//			}
//		}).start();
//	}
//
//
//	public void initListener() {
//		btnHighSpeedDownload.setOnClickListener(ocl);
//		btnScreenLit.setOnClickListener(ocl);
//		btnPushMessage.setOnClickListener(ocl);
//		sleepButton.setOnClickListener(ocl);
//		pathButton.setOnClickListener(ocl);
//		btnSyncho.setOnClickListener(ocl);
//		btnClearPic.setOnClickListener(ocl);
//		btnClearVideo.setOnClickListener(ocl);
//		recommendButton.setOnClickListener(ocl);
//		aboutBtn.setOnClickListener(ocl);
//		feedbackBtn.setOnClickListener(ocl);
//	}
//
//	OnClickListener ocl = new OnClickListener() {
//
//		@Override
//		public void onClick(View arg0) {
//			Intent intent = null;
//
//			switch (arg0.getId()) {
//			case R.id.btn_high_speed_download:
//				setHighSpeedDownload();
//				break;
//			case R.id.btn_screen_lit:
//				setScreenLit();
//				break;
//			case R.id.btn_push_message:
//				setPush();
//				break;
//			case R.id.btn_auto_syncho:
//				setAutoSyncho();
//				break;
//			case R.id.sleep_mod:
//				intent = new Intent(mContext, SleepDialog.class);
//				startActivityForResult(intent, 23);// 第二个参数requestcode随便写的，应该定义个static比较好
//				break;
//			case R.id.setting_saving_path:
//				lastSavingPath = ConfigManager.Instance().loadString(
//						"media_saving_path");
//				intent = new Intent(mContext, FileBrowserActivity.class);
//				startActivityForResult(intent, 25);
//				break;
//			case R.id.clear_pic:
//				CustomToast.showToast(mContext, R.string.setting_deleting, 2000);// 这里可以改为引用资源文件
//				new CleanBufferAsyncTask("image").execute();
//				break;
//			case R.id.clear_video:
//				Dialog dialog = new AlertDialog.Builder(mContext)
//				.setIcon(android.R.drawable.ic_dialog_alert)
//				.setTitle(getResources().getString(R.string.alert_title))
//				.setMessage(
//						getResources()
//								.getString(R.string.setting_alert))
//				.setPositiveButton(
//						getResources().getString(R.string.alert_btn_ok),
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//								CustomToast
//										.showToast(
//												mContext,
//												R.string.setting_deleting,
//												2000);// 这里可以改为引用资源文件
//								new CleanBufferAsyncTask("video")
//										.execute();
//							}
//						})
//				.setNeutralButton(
//						getResources().getString(
//								R.string.alert_btn_cancel),
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int which) {
//							}
//						}).create();
//				dialog.show();
//				break;
//			case R.id.recommend_btn:
//				prepareMessage();
//				break;
//			case R.id.about_btn:
//				intent = new Intent();
//				intent.setClass(mContext, AboutActivity.class);
//				startActivity(intent);
//				break;
//			case R.id.feedback_btn:
//				intent = new Intent();
//				intent.setClass(mContext, FeedbackActivity.class);
//				startActivity(intent);
//			}
//		}
//	};
//
//	public void setHighSpeedDownload() {
//		if(UserInfoManager.getInstance().isVip()) {
//			if (SettingConfig.Instance().isHighSpeed()) {
//				SettingConfig.Instance().setHighSpeed(false);
//			} else {
//				SettingConfig.Instance().setHighSpeed(true);
//			}
//
//			CheckBoxHighSpeedDwonload.setChecked(SettingConfig.Instance()
//					.isHighSpeed());
//		} else {
//			AlertDialog.Builder builder = new Builder(mContext);
//			builder.setTitle("提示");
//			builder.setPositiveButton("确定",null);
//			builder.setIcon(android.R.drawable.ic_dialog_info);
//			builder.setMessage(getResources().getString(R.string.high_speed_download_toast));
//			builder.show();
//		}
//	}
//
//	public void setScreenLit() {
//		if (SettingConfig.Instance().isLight()) {
//			SettingConfig.Instance().setLight(false);
//		} else {
//			SettingConfig.Instance().setLight(true);
//		}
//		CheckBoxScreenLit.setChecked(SettingConfig.Instance().isLight());
//	}
//
//	private void setPush() {
//		if (checkBoxPushMessage.isChecked()) {
//			SettingConfig.Instance().setPush(true);
////			PushAgent.getInstance(mContext).enable();
//		} else {
//			SettingConfig.Instance().setPush(false);
////			PushAgent.getInstance(mContext).disable();
//		}
//	}
//
//	public void setAutoSyncho() {
//		if (SettingConfig.Instance().isSyncho()) {
//			SettingConfig.Instance().setSyncho(false);
//		} else {
//			SettingConfig.Instance().setSyncho(true);
//		}
//		CheckBoxSyncho.setChecked(SettingConfig.Instance().isSyncho());
//	}
//
//	public void initSleep() {
//		if (!isSleep) {
//			((TextView) root.findViewById(R.id.sleep_state))
//					.setText(R.string.setting_sleep_state_off);
//		} else {
//			((TextView) root.findViewById(R.id.sleep_state)).setText(String
//					.format("%02d:%02d", hour, minute));
//		}
//	}
//
//	Handler sleepHandler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			int count = 0;
//			AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//			super.handleMessage(msg);
//			switch (msg.what) {
//			case 0:
//				if (hour + minute != 0) {// 时间没结束
//					count++;
//					if (count % 10 == 0) {
//						if (am.getStreamVolume(AudioManager.STREAM_MUSIC) > 2) {
//							am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
//									AudioManager.ADJUST_LOWER, 0);// 第三参数为0代表不弹出提示。
//						}
//					}
//					totaltime--;
//					((TextView) root.findViewById(R.id.sleep_state)).setText(String
//							.format("%02d:%02d", hour, minute));
//					hour = totaltime / 60;
//					minute = totaltime % 60;
//					sleepHandler.sendEmptyMessageDelayed(0, 60000);
//				} else {// 到结束时间
//					isSleep = false;
//					((TextView) root.findViewById(R.id.sleep_state))
//							.setText(R.string.setting_sleep_state_off);
//					Intent intent = new Intent();
//					intent.setAction("gotosleep");
//					mContext.sendBroadcast(intent);
//					am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
//				}
//				break;
//			default:
//				break;
//			}
//		}
//	};
//
//	public Handler handler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//
//			switch (msg.what) {
//			case 0:
//				String strings = (String) msg.obj;
//				picSize.setText(strings);
//				break;
//			case 1:
//				String string = (String) msg.obj;
//				soundSize.setText(string);
//				break;
//			case 2:
//				CustomToast.showToast(mContext,
//						R.string.file_path_move_success, 1000);
//				break;
//			case 3:
//				CustomToast.showToast(mContext,
//						R.string.file_path_move_exception, 1000);
//				break;
//			case 6:
//				initWidget();
//				break;
//			case 7:
//				initCheckBox();
//				break;
//			case 8:
//				initSleep();
//				break;
//			default:
//				break;
//			}
//		}
//
//	};
//
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == 23 && resultCode == 1) {// 睡眠模式设置的返回结果
//			hour = data.getExtras().getInt("hour");
//			minute = data.getExtras().getInt("minute");
//			if (hour + minute == 0) {
//				isSleep = false;
//				hour = 0;
//				minute = 0;
//				totaltime = 0;
//				sleepHandler.removeMessages(0);
//				((TextView) root.findViewById(R.id.sleep_state))
//						.setText(R.string.setting_sleep_state_off);
//			} else {
//				sleepHandler.removeMessages(0);
//				isSleep = true;
//				totaltime = hour * 60 + minute;
//				AudioManager amAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//				volume = amAudioManager
//						.getStreamVolume(AudioManager.STREAM_MUSIC);
//				sleepHandler.sendEmptyMessage(0);
//			}
//
//		} else if (requestCode == 25 && resultCode == 2) {// 改变存储路径的结果
//			nowSavingPath = data.getExtras().getString("nowSavingPath");
//			if (!nowSavingPath.equals(lastSavingPath)) {
//				Dialog dialog = new AlertDialog.Builder(mContext)
//						.setIcon(android.R.drawable.ic_dialog_alert)
//						.setTitle(getResources().getString(R.string.alert_title))
//						.setMessage(
//								getResources().getString(
//										R.string.setting_file_path_ask))
//						.setPositiveButton(
//								getResources().getString(R.string.alert_btn_ok),
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//											int whichButton) {
//										if (lastSavingPath == null
//												|| nowSavingPath == null) {
//											return;
//										}
//										new Thread(new Runnable() {
//											@Override
//											public void run() {
//												if (movePath(lastSavingPath,
//														nowSavingPath)) {
//													handler.sendEmptyMessage(2);
//												} else {
//													handler.sendEmptyMessage(3);
//												}
//
//											}
//										}).start();
//										CustomToast
//												.showToast(
//														mContext,
//														R.string.setting_file_path_moving,
//														2000);// 这里可以改为引用资源文件
//									}
//								})
//						.setNeutralButton(
//								getResources().getString(
//										R.string.alert_btn_cancel),
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//											int which) {
//									}
//								}).create();
//				dialog.show();
//
//			}
//		}
//	};
//
//	private boolean movePath(String oldPath, String newPath) {
//		boolean isok = true;
//		try {
//			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
//			File a = new File(oldPath);
//			String[] file = a.list();
//			File temp = null;
//			for (int i = 0; i < file.length; i++) {
//				if (oldPath.endsWith(File.separator)) {
//					temp = new File(oldPath + file[i]);
//				} else {
//					temp = new File(oldPath + File.separator + file[i]);
//				}
//				if (temp.isFile() && temp.getName().startsWith("temp_audio_")) {
//					if (!UtilFile.copyFile(temp.getPath(), newPath + "/"
//							+ (temp.getName()).toString())) {
//						isok = false;
//					} else {
//						// 移动成功，删除原来的
//						if (temp.delete()) {
//							Log.d("shanchu", "chenggong");
//						} else {
//							Log.d("shanchu", "shibai");
//						}
//					}
//
//				}
//				if (temp.isDirectory()) {// 如果是子文件夹
//				}
//			}
//		}
//
//		catch (Exception e) {
//			isok = false;
//		}
//		return isok;
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		MobclickAgent.onPause(mContext);
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		handler.sendEmptyMessage(6);
//		handler.sendEmptyMessage(7);
//		handler.sendEmptyMessage(8);
//
//		((TextView) root.findViewById(R.id.savingpath_path)).setText(ConfigManager
//				.Instance().loadString("media_saving_path"));// 显示路径
//
//		if (UserInfoManager.getInstance().isLogin()) {
//			ClientSession.Instace()
//					.asynGetResponse(
//							new NewInfoRequest(
//									String.valueOf(UserInfoManager.getInstance().getUserId())),
//							new IResponseReceiver() {
//								@Override
//								public void onResponse(
//										BaseHttpResponse response,
//										BaseHttpRequest request, int rspCookie) {
//									NewInfoResponse rs = (NewInfoResponse) response;
//									if (rs.letter > 0) {
//										handler.sendEmptyMessage(4);
//									}
//								}
//							});
//		}
//
//		MobclickAgent.onResume(mContext);
//	}
//
//	private void prepareMessage() {
//		String text = getResources().getString(R.string.setting_share1)
//				+ Constant.APPName
//				+ getResources().getString(R.string.setting_share2)
//				+ "：http://app." + Constant.IYUBA_CN + "android/androidDetail.jsp?id=222";
//		Intent shareInt = new Intent(Intent.ACTION_SEND);
//		shareInt.setType("text/*");
//		shareInt.putExtra(Intent.EXTRA_TEXT, text);
//		shareInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		shareInt.putExtra("sms_body", text);
//		startActivity(Intent.createChooser(shareInt,
//				getResources().getString(R.string.setting_share_ways)));
//		getIyubi("qq");
//	}
//
//	private void getIyubi(String to) {
//		String sigMd5 = UserInfoManager.getInstance().getUserId()
//				+ Constant.APPID + "android" + to + "20001" + 0 + "iyuba"; // 认证码Md5(userId+appId+from+to+titleId+type+’iyuba’)
//		sigMd5 = MD5.getMD5ofStr(sigMd5);
//		ClientSession.Instace().asynGetResponse(
//				new ShareRequest(String.valueOf(UserInfoManager.getInstance().getUserId()),
//						"20001", to, sigMd5), new IResponseReceiver() {
//					@Override
//					public void onResponse(BaseHttpResponse response,
//							BaseHttpRequest request, int rspCookie) {
//					}
//				}, null, null);
//	}
//
//	class CleanBufferAsyncTask extends AsyncTask<Void, Void, Void> {
//		private String filepath = Constant.picAddr;
//		public String result;
//		private String cachetype;
//		private VoaOp voaOp;
//		private BookOp bookOp;
//		private List<Book> bookList;
//		private List<DownloadInfo> infoList;
//
//		public CleanBufferAsyncTask(String type) {
//			this.cachetype = type;
//			if (type.equals("image")) {
//				filepath = Constant.picAddr;// 此处在voa常速英语中改为filepath=Constant.Instance().getPicPos();Constant文件不一致
//			} else if (type.equals("video")) {
//				DownloadStateManager manager = DownloadStateManager.instance();
//				bookOp = manager.bookOp;
//				infoList = manager.downloadList;
//				bookList = manager.bookList;
//				filepath = ConfigManager.Instance().loadString(
//						"media_saving_path");
//			}
//		}
//
//		public boolean Delete() {
//			File file = new File(filepath);
//			if (file.isFile()) {
//				file.delete();
//				return true;
//			} else if (file.isDirectory()) {
//				File files[] = file.listFiles();
//				if (files != null && files.length == 0) {
//					return false;
//				} else {
//					for (int i = 0; i < files.length; i++) {
//						files[i].delete();
//					}
//					return true;
//				}
//			} else {
//				return false;
//			}
//		}
//
//		public DownloadInfo getDownloadInfo(int voaId) {
//			for(DownloadInfo info : infoList) {
//				if(info.voaId == voaId) {
//					return info;
//				}
//			}
//
//			return null;
//		}
//
//		@Override
//		protected Void doInBackground(Void... params) {
//			if (Delete()) {
//				if (cachetype.equals("image")) {
//					picSize.post(new Runnable() {
//						@Override
//						public void run() {
//							picSize.setText("OK");
//						}
//					});
//				} else if (cachetype.equals("video")) {
//					for(Book book : bookList) {
//						book.downloadNum = 0;
//						book.downloadState = 0;
//						bookOp.updateDownloadNum(book);
//					}
//
//					voaOp = new VoaOp(mContext);
//					voaList = (ArrayList<Voa>) voaOp.findDataFromDownload();
//					if (voaList != null) {
//						Iterator<Voa> iteratorVoa = voaList.iterator();
//						while (iteratorVoa.hasNext()) {
//							Voa voaTemp = iteratorVoa.next();
//							voaOp.deleteDataInDownload(voaTemp.voaId);
//							DownloadStateManager.instance().delete(voaTemp.voaId);
//						}
//						soundSize.post(new Runnable() {
//							@Override
//							public void run() {
//								soundSize.setText("OK");
//							}
//						});
//					}
//				}
//			} else
//				CustomToast
//						.showToast(mContext, R.string.setting_del_fail, 1000);
//			return null;
//		}
//	}
//
//	private String getSize(int type) {
//		if (type == 0) {
//			return FileSize.getInstance().getFormatFolderSize(
//					new File(Constant.envir + "/image"));
//		} else {
//			return FileSize.getInstance().getFormatFolderSize(
//					new File(ConfigManager.Instance().loadString(
//							"media_saving_path")));
//		}
//	}
//}
