package com.iyuba.conceptEnglish.activity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.adapter.CommentListAdapter;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.protocol.UserCommentRequest;
import com.iyuba.conceptEnglish.protocol.UserCommentResponse;
import com.iyuba.conceptEnglish.sqlite.mode.Comment;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IErrorReceiver;
import com.iyuba.core.common.network.INetStateReceiver;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.ErrorResponse;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 排行详情界面
 * Created by ivotsm on 2017/3/10.
 */

public class CommentActivity extends BasisActivity {
    private Context mContext;
    private Button backBtn, synchoBtn, editBtn;
    private TextView title;
    private RecyclerView listComment;
    private String uid, voaId, userName, userPic;
    private boolean commentAll = false;
    private LayoutInflater inflater;
    //    private View commentFooter;
    private ArrayList<Comment> comments = new ArrayList<>();
    private MediaPlayer voiceMediaPlayer;
    private CommentListAdapter commentAdapter;
    private CustomDialog waitDialog;
    private List<VoaDetail> textDetailTemp;
    private String type = "";
    private INetStateReceiver mNetStateReceiver = new INetStateReceiver() {

        @Override
        public void onStartConnect(BaseHttpRequest request, int rspCookie) {

        }

        @Override
        public void onConnected(BaseHttpRequest request, int rspCookie) {

        }

        @Override
        public void onStartSend(BaseHttpRequest request, int rspCookie,
                                int totalLen) {

        }

        @Override
        public void onSend(BaseHttpRequest request, int rspCookie, int len) {

        }

        @Override
        public void onSendFinish(BaseHttpRequest request, int rspCookie) {

        }

        @Override
        public void onStartRecv(BaseHttpRequest request, int rspCookie,
                                int totalLen) {

        }

        @Override
        public void onRecv(BaseHttpRequest request, int rspCookie, int len) {

        }

        @Override
        public void onRecvFinish(BaseHttpRequest request, int rspCookie) {

        }

        @Override
        public void onNetError(BaseHttpRequest request, int rspCookie,
                               ErrorResponse errorInfo) {

        }

        @Override
        public void onCancel(BaseHttpRequest request, int rspCookie) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_comment);

        mContext = this;
        inflater = getLayoutInflater();
        waitDialog = WaittingDialog.showDialog(mContext);

        textDetailTemp = VoaDataManager.Instace().voaDetailsTemp;// 句子list

        backBtn = (Button) findViewById(R.id.button_back);
        title = (TextView) findViewById(R.id.title);
        synchoBtn = (Button) findViewById(R.id.button_syncho);
        editBtn = (Button) findViewById(R.id.button_edit);
        listComment = findViewById(R.id.voa_list);

        synchoBtn.setVisibility(View.GONE);
        editBtn.setVisibility(View.GONE);

        initRecyclerView();
        uid = getIntent().getStringExtra("uid");
        voaId = getIntent().getStringExtra("voaId");
        userName = getIntent().getStringExtra("userName");
        userPic = getIntent().getStringExtra("userPic");
        type = getIntent().getStringExtra("type");

        if (!TextUtils.isEmpty(userName)) {
            title.setText("\"" + userName + "\"" + "的评测");
        } else {
            title.setText("\"" + uid + "\"" + "的评测");
        }


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        waitDialog.show();
        handler.sendEmptyMessage(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (commentAdapter != null)
            commentAdapter.stopVoices();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    ClientSession.Instace().asynGetResponse(
                            new UserCommentRequest(uid, Constant.EVAL_TYPE, voaId, type), new IResponseReceiver() {
                                @Override
                                public void onResponse(BaseHttpResponse response,
                                                       BaseHttpRequest request, int rspCookie) {
                                    UserCommentResponse tr = (UserCommentResponse) response;
                                    if (tr.result.equals("true")) {
                                        comments.clear();
                                        for (Comment comment : tr.comments) {
                                            if (comment.shuoshuoType != -1) {
                                                comments.add(comment);
                                            }
                                        }
                                        //comments.addAll(tr.comments);
                                        Collections.sort(comments);
                                        handler.sendEmptyMessage(1);
                                    }
                                }
                            }, new IErrorReceiver() {
                                @Override
                                public void onError(ErrorResponse errorResponse, BaseHttpRequest request, int rspCookie) {
                                }
                            }, mNetStateReceiver);
                    break;
                case 1:
                    for (int i = 0; i < comments.size(); i++) {
                        comments.get(i).username = userName;
                        comments.get(i).imgsrc = userPic;
                        comments.get(i).userId = uid;
                    }
                    waitDialog.dismiss();
                    commentAdapter = new CommentListAdapter(mContext, comments, 2, textDetailTemp, uid);
                    listComment.setAdapter(commentAdapter);
                    break;
            }
        }
    };

    private void initRecyclerView() {
        listComment.setLayoutManager(new LinearLayoutManager(mContext));
        listComment.addItemDecoration(new DividerItemDecoration(mContext, 1));
    }
}
