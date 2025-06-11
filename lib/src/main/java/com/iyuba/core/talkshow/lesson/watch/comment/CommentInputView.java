package com.iyuba.core.talkshow.lesson.watch.comment;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyuba.core.common.util.Recorder;
import com.iyuba.core.event.PauseEvent;
import com.iyuba.core.event.StopEvent;
import com.iyuba.lib.R;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

public class CommentInputView extends RelativeLayout {

    private RequestPermissionCallback mPermissionCb;

    public interface Mode {
        int TEXT = 1;
        int VOICE = 2;
    }

    String mReplyPrefix;
    ImageView mIvModeSwitch;
    RelativeLayout mTextInputContainer;
    EditText mEtText;
    View mVTextLine;
    LinearLayout mVoiceInputContainer;
    TextView mTvTouchSay;
    TextView mTvListeningTest;

    private Recorder mRecorder;
    private MediaPlayer mMediaPlayer;

    private boolean mIsRecordReady = false;
    private boolean mGestureUp = false;

    private String mRecordFilePath;

    public int mMode = Mode.TEXT;
    private OnCommentSendListener mSendListener;
    private RecorderListener mRecorderListener;
    private InputMethodCallback mInputMethodCallback;


    public CommentInputView(Context context) {
        this(context, null);
    }

    public CommentInputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.comment_input_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
        initClick();
        initState();
    }

    private void initView(){
        mReplyPrefix = getResources().getString(R.string.reply);
        mIvModeSwitch = findViewById(R.id.iv_switch_mode);
        mTextInputContainer = findViewById(R.id.text_input_container);
        mEtText = findViewById(R.id.et_text);
        mVTextLine = findViewById(R.id.v_text_line);
        mVoiceInputContainer = findViewById(R.id.voice_input_container);
        mTvTouchSay = findViewById(R.id.tv_touch_say);
        mTvListeningTest = findViewById(R.id.tv_listening_test);
    }

    private void initClick(){
        findViewById(R.id.iv_switch_mode).setOnClickListener(v->{
            changeMode();
            initState();
        });
        findViewById(R.id.tv_listening_test).setOnClickListener(v->{
            if (mIsRecordReady) {
                try {
                    EventBus.getDefault().post(new StopEvent(StopEvent.SOURCE.VIDEO));

                    mMediaPlayer.setOnCompletionListener(null);
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                    }
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(mRecordFilePath);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.tv_send).setOnClickListener(v->{
            if (mSendListener != null) {
                switch (mMode) {
                    case Mode.VOICE:
                        if (mIsRecordReady) {
                            File file = new File(mRecordFilePath);
                            if (file.exists()) {
                                mSendListener.onVoiceSend(file);
                            }
                        }

                        break;
                    case Mode.TEXT:
                    default:
                        String textComment = mEtText.getText().toString();
                        if (!textComment.equals("") && mSendListener != null) {
                            InputMethodManager imm = (InputMethodManager) getContext()
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(mEtText.getWindowToken(), 0);
                            mSendListener.onTextSend(textComment);
                        }
                        break;
                }
            }
        });
        findViewById(R.id.tv_touch_say).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mGestureUp = false;
                        EventBus.getDefault().post(new StopEvent(StopEvent.SOURCE.VIDEO));
                        if (mRecordFilePath != null && mPermissionCb != null) {
                            mPermissionCb.requestRecordPermission();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        mGestureUp = true;
                        if (mRecorder != null) {
                            mRecorder.stop();
                        }
                        break;
                }
                return true;
            }
        });
    }

    public void changeMode() {
        if (mMode == Mode.TEXT) {
            mMode = Mode.VOICE;
        } else if (mMode == Mode.VOICE) {
            mEtText.requestFocus();
            mMode = Mode.TEXT;
        }
    }

    public void onRecordPermissionGranted() {
        if (!mGestureUp) {
            EventBus.getDefault().post(new PauseEvent());
            Timber.e("mRecorder.start------------");
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mRecorder.start(mRecordFilePath);
        }
    }

    public void initState() {
        switch (mMode) {
            case Mode.VOICE:
                mTextInputContainer.setVisibility(View.GONE);
                mVoiceInputContainer.setVisibility(View.VISIBLE);
                mIvModeSwitch.setImageResource(R.drawable.text_mode);
                if (mInputMethodCallback != null) {
                    mInputMethodCallback.hide();
                }
                break;
            case Mode.TEXT:
            default:
                mTextInputContainer.setVisibility(View.VISIBLE);
                mVoiceInputContainer.setVisibility(View.GONE);
                mIvModeSwitch.setImageResource(R.drawable.voice_mode);
                if (getVisibility() == VISIBLE) {
                    mEtText.requestFocus();
                }
                if (mInputMethodCallback != null) {
                    mInputMethodCallback.show();
                }
                break;
        }
    }

    public void release() {
        new File(mRecordFilePath).delete();
    }

    private Recorder.OnStateChangeListener mStateListener = new Recorder.OnStateChangeListener() {
        @Override
        public void onStateChange(int newState) {
            switch (newState) {
                case Recorder.State.INITIAL:
                    Timber.e("INITIAL");
                    mTimerHandler.removeMessages(0);
                    break;
                case Recorder.State.RECORDING:
                    Timber.e("RECORDING");
                    mIsRecordReady = false;
                    if (mRecorderListener != null) mRecorderListener.onBegin();
                    mTimerHandler.sendEmptyMessage(0);
                    break;
                case Recorder.State.COMPLETED:
                    Timber.e("COMPLETED");
                    mIsRecordReady = true;
                    if (mRecorderListener != null) mRecorderListener.onEnd();
                    mTimerHandler.removeMessages(0);
                    break;
                case Recorder.State.ERROR:
                    Timber.e("ERROR");
                    if (mRecorderListener != null) mRecorderListener.onError();
                    mTimerHandler.removeMessages(0);
                    break;
                default:
                    break;
            }
        }
    };

    private Handler mTimerHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (mRecorderListener != null)
                mRecorderListener.onVolumeChanged(mRecorder.getCurrentDB());
            mTimerHandler.sendEmptyMessageDelayed(0, 200);
            return false;
        }
    });

    public void setRecordFilePath(String path) {
        this.mRecordFilePath = path;
    }

    public void setRecorder(Recorder recorder) {
        if (recorder != null) {
            mRecorder = recorder;
            mRecorder.setOnStateChangeListener(mStateListener);
        }
    }

    public void replyToSomeone(String targetUserName) {
        if (mMode == Mode.VOICE) {
            changeMode();
            initState();
        }
        mEtText.findFocus();
        mEtText.requestFocus();
        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(mEtText, 0);
        mEtText.setText(mReplyPrefix + targetUserName + ":");
        mEtText.setSelection(mEtText.length());
    }

    public void setOnCommentSendListener(OnCommentSendListener l) {
        this.mSendListener = l;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mMediaPlayer = mediaPlayer;
    }

    public void setRecorderListener(RecorderListener l) {
        this.mRecorderListener = l;
    }

    public void setRequestPermissionCallback(RequestPermissionCallback callback) {
        this.mPermissionCb = callback;
    }

    public void setInputMethodCallback(InputMethodCallback inputMethodCallback) {
        this.mInputMethodCallback = inputMethodCallback;
    }

    public void clearInputText() {
        mEtText.setText("");
    }

    public interface OnCommentSendListener {
        void onTextSend(String comment);

        void onVoiceSend(File record);
    }

    public interface RequestPermissionCallback {
        void requestRecordPermission();
    }

    public interface RecorderListener {
        void onBegin();

        void onVolumeChanged(int db);

        void onEnd();

        void onError();
    }

    public interface InputMethodCallback {
        void show();

        void hide();
    }
}
