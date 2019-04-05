package com.example.administrator.zhixueproject.activity.live;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.administrator.zhixueproject.R;
import com.example.administrator.zhixueproject.activity.BaseActivity;
import com.example.administrator.zhixueproject.adapter.live.LiveContentsAdapter;
import com.example.administrator.zhixueproject.application.MyApplication;
import com.example.administrator.zhixueproject.bean.BaseBean;
import com.example.administrator.zhixueproject.bean.UploadFile;
import com.example.administrator.zhixueproject.bean.live.Live;
import com.example.administrator.zhixueproject.bean.topic.ReleaseContentsBean;
import com.example.administrator.zhixueproject.fragment.LiveFragment;
import com.example.administrator.zhixueproject.fragment.topic.PlaybackDialogFragment;
import com.example.administrator.zhixueproject.http.HandlerConstant1;
import com.example.administrator.zhixueproject.http.HttpConstant;
import com.example.administrator.zhixueproject.http.method.HttpMethod1;
import com.example.administrator.zhixueproject.utils.AddImageUtils;
import com.example.administrator.zhixueproject.utils.FileStorage;
import com.example.administrator.zhixueproject.utils.PopIco;
import com.example.administrator.zhixueproject.utils.SoftInputUtils;
import com.example.administrator.zhixueproject.utils.StatusBarUtils;
import com.example.administrator.zhixueproject.utils.record.RecordUtil;
import com.example.administrator.zhixueproject.utils.record.VoiceManager;
import com.example.administrator.zhixueproject.view.CustomPopWindow;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

/**
 * 发布内容
 *
 * @author petergee
 * @date 2018/10/11
 */
public class AddLiveContentActivity extends BaseActivity implements View.OnClickListener, EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    private List<ReleaseContentsBean> listData = new ArrayList<>();//发布内容的Json数据
    private LiveContentsAdapter mAdapter;
    private boolean mIsFocus;
    private CustomPopWindow recordPopWindow;
    private VoiceManager voiceManager;
    private TextView tv_time_length;
    private Uri mOutputUri;
    private int fileType;
    private long voiceLength = 0;
    public File mFileCamera;
    public File mVoiceFile;
    private String voiceStrLength;
    private EmojiconEditText etContent;
    private RecyclerView rvReleaseContent;
    private LinearLayout llContent;
    private ImageView ivPicture;
    private LinearLayout llReleaseContents;
    private FrameLayout flEmoji;
    private LinearLayout llRelease;
    private PopIco popIco;
    //是否在录音
    private boolean isRecord=false;
    private Live.LiveList liveList;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_content);
        initView();
        getLiveContent();
    }

    private void initView() {
        liveList= (Live.LiveList) getIntent().getSerializableExtra("liveList");
        StatusBarUtils.transparencyBar(this);
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.release_content));
        findViewById(R.id.lin_back).setOnClickListener(this);
        voiceManager = VoiceManager.getInstance(this);

        llContent = (LinearLayout) findViewById(R.id.ll_content);
        findViewById(R.id.iv_expression).setOnClickListener(this);
        ivPicture = (ImageView) findViewById(R.id.iv_picture);
        ivPicture.setOnClickListener(this);
        TextView tvEnd=(TextView)findViewById(R.id.tv_release) ;
        tvEnd.setText("直播结束");
        findViewById(R.id.iv_voice).setOnClickListener(this);
        tvEnd.setOnClickListener(this);
        findViewById(R.id.tv_confirm).setOnClickListener(this);
        llReleaseContents = (LinearLayout) findViewById(R.id.ll_release_contents);
        llRelease = (LinearLayout) findViewById(R.id.ll_release);
        mAdapter = new LiveContentsAdapter(null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvReleaseContent = (RecyclerView) findViewById(R.id.rv_release_content);
        rvReleaseContent.setAdapter(mAdapter);
        rvReleaseContent.setLayoutManager(linearLayoutManager);
        institutionListener();

        etContent = (EmojiconEditText) findViewById(R.id.et_content);
        etContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                mIsFocus = isFocus;
            }
        });
        flEmoji = (FrameLayout) findViewById(R.id.emojicons);
        setEmojiconFragment(false);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(etContent, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(etContent);
    }


    /**
     * RecyclerView监听
     */
    private void institutionListener() {
        //文字改变的监听
        mAdapter.setChangInstitutionDataListener(new LiveContentsAdapter.ChangInstitutionDataListener() {
            @Override
            public void onChangInstitutionDataListener(int position, String data) {
            }

            @Override
            public void onDeleteItemListener(int position) {
                mAdapter.remove(position);
                listData.remove(position);
            }
        });
        //条目子控件的点击事件监听
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_delete:
                        mAdapter.remove(position);
                        listData.remove(position);
                        break;
                    case R.id.iv_record_play:
                        PlaybackDialogFragment fragmentPlay = PlaybackDialogFragment.newInstance(listData.get(position));
                        fragmentPlay.show(getSupportFragmentManager(), PlaybackDialogFragment.class.getSimpleName());
                        break;
                }
            }
        });

    }


    //相机和相册选择图片的回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case AddImageUtils.REQUEST_PICK_IMAGE://从相册选择
                    if (data != null) {
                        if (Build.VERSION.SDK_INT >= 19) {
                            AddImageUtils.handleImageOnKitKat(data, AddLiveContentActivity.this);
                        } else {
                            AddImageUtils.handleImageBeforeKitKat(data, AddLiveContentActivity.this);
                        }
                        mOutputUri = AddImageUtils.cropPhotoSmall(AddLiveContentActivity.this);

                    }
                    break;
                case AddImageUtils.REQUEST_CAPTURE://拍照
                    mOutputUri = AddImageUtils.cropPhotoSmall(AddLiveContentActivity.this);
                    break;
                case AddImageUtils.REQUEST_PICTURE_CUT_SMALL://裁剪完成
                    if (data != null) {
                        try {
                            mFileCamera = new File(mOutputUri.getPath());
                            if (!mFileCamera.isFile()) {
                                return;
                            }
                            List<File> list = new ArrayList<>();
                            list.add(mFileCamera);
                            showProgress("图片上传中");
                            //本地显示
                            addList(mOutputUri.getPath(), fileType, voiceStrLength, voiceLength);
                            //上传图片
                            HttpMethod1.uploadFile(HttpConstant.UPDATE_FILES, list, mHandler);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            // emoji表情
            case R.id.iv_expression:
                if (flEmoji.getVisibility() == View.VISIBLE) {
                    hideEmoji();
                } else {
                    shouEmoji();
                }
                break;
            case R.id.iv_picture:
                addPic();
                break;
            case R.id.tv_confirm:
                fileType = ReleaseContentsBean.TEXT;
                if (!TextUtils.isEmpty(etContent.getText().toString().trim())) {
                    addList(etContent.getText().toString().trim(), fileType, null, 0);
                    listData.clear();
                    listData.add(new ReleaseContentsBean(etContent.getText().toString().trim(), fileType, voiceStrLength, voiceLength));
                    //上传文字数据
                    sendMsg();
                    etContent.setText("");
                }
                break;
            case R.id.tv_release:
                 showProgress(getString(R.string.loding));
                 HttpMethod1.liveEnd(String.valueOf(liveList.getPostId()),mHandler);
                break;
            case R.id.iv_voice:
                showRecordPopWindow();
                break;
            case R.id.lin_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void shouEmoji() {
        startAnim(flEmoji,2);
        flEmoji.setVisibility(View.VISIBLE);
        // 隐藏发布按钮
        llRelease.setVisibility(View.GONE);
    }

    private void hideEmoji() {
        startAnim(flEmoji,1);
        flEmoji.setVisibility(View.GONE);
        llRelease.setVisibility(View.VISIBLE);
    }

    /**
     * emoji动画
     * @param flEmoji
     * @param index
     */
    private void startAnim(FrameLayout flEmoji, int index) {
        Animation animationIn= AnimationUtils.loadAnimation(this,R.anim.emoji_enter);
        Animation animationOut= AnimationUtils.loadAnimation(this,R.anim.emoji_exit);
        if (index==1){
            flEmoji.startAnimation(animationOut);
        }else {
            flEmoji.startAnimation(animationIn);
        }
    }

    /**
     * 上传图片
     */
    private void addPic() {
        fileType = ReleaseContentsBean.IMG;
        popIco = new PopIco(this);
        popIco.showAsDropDown();
        popIco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_pop_ico_camera:
                        AddImageUtils.openCamera(AddLiveContentActivity.this);
                        break;
                    case R.id.tv_pop_ico_photo:
                        AddImageUtils.selectFromAlbum(AddLiveContentActivity.this);
                        break;
                }
            }
        });
    }

    private void showRecordPopWindow() {
        fileType = ReleaseContentsBean.RECORD;
        View contentView = LayoutInflater.from(this).inflate(R.layout.fragment_record_audio, null);
        handleLogic(contentView);
        recordPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .enableBackgroundDark(true)
                .setBgDarkAlpha(0.7f)
                .enableOutsideTouchableDissmiss(true)
                .size(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .create();
        recordPopWindow.showAtLocation(llReleaseContents, Gravity.BOTTOM, 0, 0);

    }

    private void handleLogic(View contentView) {
        tv_time_length = (TextView) contentView.findViewById(R.id.tv_time_length);
        tv_time_length.setText("00:00:00");
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_record_delete:
                        if (voiceManager != null) {
                            isRecord=false;
                            voiceManager.cancelVoiceRecord();
                        }
                        recordPopWindow.dissmiss();
                        break;
                    case R.id.tv_record_start:
                         if(!isRecord){
                             isRecord=true;
                             startRecord();
                         }
                        break;
                    case R.id.tv_record_conform:
                        if (voiceManager != null) {
                            isRecord=false;
                            voiceManager.stopVoiceRecord();
                        }
                        recordPopWindow.dissmiss();
                        break;
                }
            }
        };
        contentView.findViewById(R.id.tv_record_delete).setOnClickListener(listener);
        contentView.findViewById(R.id.tv_record_start).setOnClickListener(listener);
        contentView.findViewById(R.id.tv_record_conform).setOnClickListener(listener);

    }

    private void startRecord() {
        voiceManager.setVoiceRecordListener(new VoiceManager.VoiceRecordCallBack() {
            @Override
            public void recDoing(long time, String strTime) {
                tv_time_length.setText(strTime);
            }

            @Override
            public void recVoiceGrade(int grade) {
            }

            @Override
            public void recStart(boolean init) {
            }

            @Override
            public void recPause(String str) {
            }


            @Override
            public void recFinish(long length, String strLength, String path) {
                mVoiceFile = new File(path);
                List<File> list = new ArrayList<>();
                list.add(mVoiceFile);
                HttpMethod1.uploadFile(HttpConstant.UPDATE_FILES, list, mHandler);
                voiceStrLength = strLength;
                voiceLength = length;
                //本地显示
                addList(path, fileType, strLength, length);
            }
        });
        voiceManager.startVoiceRecord(RecordUtil.getAudioPath());
    }


    /**
     * 添加数据--本地显示
     *
     * @param content 数据内容
     * @param type    数据类型 0：文字；1：图片；2：录音
     */
    private void addList(String content, int type, String strLength, long length) {
        ReleaseContentsBean data = new ReleaseContentsBean(content, type, strLength, length);
        mAdapter.addData(data);
        mAdapter.notifyDataSetChanged();
        rvReleaseContent.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (isHideSoftInput(view, (int) ev.getX(), (int) ev.getY())) {
            closeSoftInput();
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void closeSoftInput() {
        etContent.clearFocus();
        SoftInputUtils.closeSoftInput(this);
    }

    private boolean isHideSoftInput(View view, int x, int y) {
        if (view == null || !(view instanceof EditText) || !mIsFocus) {
            return false;
        }
        return x < llContent.getLeft() ||
                x > llContent.getRight() ||
                y < llContent.getTop();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            clearTask();
            BaseBean baseBean=null;
            switch (msg.what) {
                case HandlerConstant1.UPLOAD_HEAD_SUCCESS:
                    UploadFile bean = (UploadFile) msg.obj;
                    if (null == bean) {
                        return;
                    }
                    if (bean.isStatus()) {
                        String head = "http://";
                        String url = bean.getData().getUrl();
                        if (url.contains(head)) {
                            url = url.substring(head.length(), url.length());
                        }
                        //发布时需要用到的去http
                        listData.clear();
                        listData.add(new ReleaseContentsBean(url, fileType, voiceStrLength, voiceLength));
                        voiceStrLength = "";
                        voiceLength = 0;

                        //上传图片或者mp3
                        sendMsg();
                    } else {
                        showMsg(bean.getErrorMsg());
                    }
                    break;
                    //发布文字成功
                case HandlerConstant1.SEND_TEXT_SUCCESS:
                    baseBean= (BaseBean) msg.obj;
                    if(null==baseBean){
                        return;
                    }
                    if (!baseBean.status) {
                        showMsg(baseBean.getErrorMsg());
                    }
                     break;
                // 直播结束
                case HandlerConstant1.LIVE_END_SUCCESS:
                    baseBean= (BaseBean) msg.obj;
                    if(null==baseBean){
                        return;
                    }
                    if (baseBean.status) {
                        //删除文件
                        if (mFileCamera != null) {
                            FileStorage.deleteFile(mFileCamera.getAbsolutePath());
                        }
                        if (mVoiceFile != null) {
                            FileStorage.deleteFile(mVoiceFile.getAbsolutePath());
                        }
                        Intent intent=new Intent(LiveFragment.LIVE_END_SUCCESS);
                        intent.putExtra("postId",liveList.getPostId());
                        sendBroadcast(intent);
                        finish();
                    } else {
                        showMsg(baseBean.getErrorMsg());
                    }
                    break;
                case HandlerConstant1.REQUST_ERROR:
                    showMsg(getString(R.string.net_error));
                    break;
                default:
                    break;

            }
        }
    };


    private void sendMsg(){
        String msg=MyApplication.gson.toJson(listData);
        if(!TextUtils.isEmpty(msg)){
            if(msg.substring(0,1).equals("[") && msg.substring(msg.length()-1,msg.length()).equals("]")){
                msg=msg.substring(1,msg.length()-1);
            }
            showProgress("上传中");
            HttpMethod1.sendText(String.valueOf(liveList.getPostId()),msg,mHandler);
        }
    }


    private void getLiveContent(){
        HttpMethod1.getLiveContent(String.valueOf(liveList.getPostId()),mHandler);
    }

}
