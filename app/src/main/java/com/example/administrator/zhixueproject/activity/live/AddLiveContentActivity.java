package com.example.administrator.zhixueproject.activity.live;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Path;
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
import com.example.administrator.zhixueproject.activity.ShowImgActivity;
import com.example.administrator.zhixueproject.adapter.live.LiveContentsAdapter;
import com.example.administrator.zhixueproject.application.MyApplication;
import com.example.administrator.zhixueproject.bean.BaseBean;
import com.example.administrator.zhixueproject.bean.UploadFile;
import com.example.administrator.zhixueproject.bean.live.Live;
import com.example.administrator.zhixueproject.bean.live.SeeNumBean;
import com.example.administrator.zhixueproject.bean.topic.ReleaseContentsBean;
import com.example.administrator.zhixueproject.fragment.LiveFragment;
import com.example.administrator.zhixueproject.fragment.topic.PlaybackDialogFragment;
import com.example.administrator.zhixueproject.http.HandlerConstant1;
import com.example.administrator.zhixueproject.http.HttpConstant;
import com.example.administrator.zhixueproject.http.method.HttpMethod1;
import com.example.administrator.zhixueproject.utils.AddImageUtils;
import com.example.administrator.zhixueproject.utils.FileStorage;
import com.example.administrator.zhixueproject.utils.FileUtils;
import com.example.administrator.zhixueproject.utils.LogUtils;
import com.example.administrator.zhixueproject.utils.PopIco;
import com.example.administrator.zhixueproject.utils.SoftInputUtils;
import com.example.administrator.zhixueproject.utils.StatusBarUtils;
import com.example.administrator.zhixueproject.utils.TimerUtil;
import com.example.administrator.zhixueproject.utils.Utils;
import com.example.administrator.zhixueproject.utils.record.RecordUtil;
import com.example.administrator.zhixueproject.utils.record.VoiceManager;
import com.example.administrator.zhixueproject.view.CustomPopWindow;
import com.example.administrator.zhixueproject.view.DialogView;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private String mOutputUri;
    private int fileType;
    private long voiceLength = 0;
    public File mFileCamera;
    public File mVoiceFile;
    private String voiceStrLength, mp3Path;
    private EmojiconEditText etContent;
    private RecyclerView rvReleaseContent;
    private LinearLayout llContent;
    private ImageView ivPicture;
    private LinearLayout llReleaseContents;
    private FrameLayout flEmoji;
    private LinearLayout llRelease;
    private PopIco popIco;
    private TextView tvRight;
    //是否在录音
    private boolean isRecord = false;
    private Live.LiveList liveList;
    private List<ReleaseContentsBean> firstList = new ArrayList<>();
    private boolean isBottom = true;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_content);
        initView();
        getLiveContent();
        getLiveSeeNumber();
    }


    private void initView() {
        liveList = (Live.LiveList) getIntent().getSerializableExtra("liveList");
        StatusBarUtils.transparencyBar(this);
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.release_content));
        tvRight= (TextView) findViewById(R.id.tv_right);
        findViewById(R.id.img_right).setVisibility(View.GONE);
        findViewById(R.id.lin_back).setOnClickListener(this);
        voiceManager = VoiceManager.getInstance(this);

        llContent = (LinearLayout) findViewById(R.id.ll_content);
        findViewById(R.id.iv_expression).setOnClickListener(this);
        ivPicture = (ImageView) findViewById(R.id.iv_picture);
        ivPicture.setOnClickListener(this);
        TextView tvEnd = (TextView) findViewById(R.id.tv_release);
        tvEnd.setText("直播结束");
        findViewById(R.id.iv_voice).setOnClickListener(this);
        tvEnd.setOnClickListener(this);
        findViewById(R.id.tv_confirm).setOnClickListener(this);
        llReleaseContents = (LinearLayout) findViewById(R.id.ll_release_contents);
        llRelease = (LinearLayout) findViewById(R.id.ll_release);
        mAdapter = new LiveContentsAdapter(firstList);
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

        rvReleaseContent.setOnScrollListener(new RecyclerView.OnScrollListener() {
            //用来标记是否正在向最后一个滑动
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();

                    // 判断是否滚动到底部，并且是向右滚动
                    if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
                        //加载更多功能的代码
                        isBottom=true;
                        LogUtils.e("+++++++++++++++++++++1");
                    }else{
                        isBottom=false;
                        LogUtils.e("+++++++++++++++++++++2");
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                if (dy > 0) {
                    //大于0表示正在向右滚动
                    isSlidingToLast = true;
                } else {
                    //小于等于0表示停止或向左滚动
                    isSlidingToLast = false;
                }
            }
        });
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
                        PlaybackDialogFragment fragmentPlay = PlaybackDialogFragment.newInstance((ReleaseContentsBean) view.getTag());
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
        Intent intent = new Intent(AddLiveContentActivity.this, ShowImgActivity.class);
        switch (requestCode) {
            case AddImageUtils.REQUEST_PICK_IMAGE://从相册选择
                if (data != null) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        AddImageUtils.handleImageOnKitKat(data, AddLiveContentActivity.this);
                    } else {
                        AddImageUtils.handleImageBeforeKitKat(data, AddLiveContentActivity.this);
                    }
                    intent.putExtra("imgPath", FileUtils.getFileByUri(AddImageUtils.imageUri, AddLiveContentActivity.this).getPath());
                    startActivityForResult(intent, 0x00a);
                }
                break;
            case AddImageUtils.REQUEST_CAPTURE://拍照
                intent.putExtra("imgPath", FileUtils.getFileByUri(AddImageUtils.imageUri, AddLiveContentActivity.this).getPath());
                startActivityForResult(intent, 0x00a);
                break;
        }
        if (resultCode == 0x00a) {
            mOutputUri = FileUtils.amendRotatePhoto(FileUtils.getFileByUri(AddImageUtils.imageUri, AddLiveContentActivity.this), AddLiveContentActivity.this);
            uploadImg();
        }
    }


    /**
     * 上传图片
     */
    private void uploadImg() {
        try {
            mFileCamera = new File(mOutputUri);
            if (!mFileCamera.isFile()) {
                return;
            }
            List<File> list = new ArrayList<>();
            list.add(mFileCamera);
            showProgress("图片上传中");
            //本地显示
//            addList(mOutputUri, fileType, voiceStrLength, voiceLength);
            HttpMethod1.uploadFile(HttpConstant.UPDATE_FILES, list, mHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    DialogView dialogView;

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
//                    addList(etContent.getText().toString().trim(), fileType, null, 0);
                    listData.clear();
                    listData.add(new ReleaseContentsBean(etContent.getText().toString().trim(), fileType, voiceStrLength, voiceLength));
                    //上传文字数据
                    sendMsg();
                }
                break;
            case R.id.tv_release:
                dialogView = new DialogView(this, "确定结束直播吗？", "确定", "取消", new View.OnClickListener() {
                    public void onClick(View v) {
                        dialogView.dismiss();
                        showProgress(getString(R.string.loding));
                        HttpMethod1.liveEnd(String.valueOf(liveList.getPostId()), mHandler);
                    }
                }, null);
                dialogView.show();
                break;
            case R.id.iv_voice:
                closeSoftInput();
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
        startAnim(flEmoji, 2);
        flEmoji.setVisibility(View.VISIBLE);
        // 隐藏发布按钮
        llRelease.setVisibility(View.GONE);
    }

    private void hideEmoji() {
        startAnim(flEmoji, 1);
        flEmoji.setVisibility(View.GONE);
        llRelease.setVisibility(View.VISIBLE);
    }

    /**
     * emoji动画
     *
     * @param flEmoji
     * @param index
     */
    private void startAnim(FrameLayout flEmoji, int index) {
        Animation animationIn = AnimationUtils.loadAnimation(this, R.anim.emoji_enter);
        Animation animationOut = AnimationUtils.loadAnimation(this, R.anim.emoji_exit);
        if (index == 1) {
            flEmoji.startAnimation(animationOut);
        } else {
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
                            isRecord = false;
                            voiceManager.cancelVoiceRecord();
                        }
                        recordPopWindow.dissmiss();
                        break;
                    case R.id.tv_record_start:
                        if (!isRecord) {
                            isRecord = true;
                            startRecord();
                        }
                        break;
                    case R.id.tv_record_conform:
                        if (voiceManager != null) {
                            isRecord = false;
                            voiceManager.stopVoiceRecord(1);
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
                mp3Path = path;
                voiceStrLength = strLength;
                voiceLength = length;
                //本地显示
//                addList(mp3Path, fileType, voiceStrLength, voiceLength);

                mVoiceFile = new File(path);
                List<File> list = new ArrayList<>();
                list.add(mVoiceFile);
                showProgress("音频上传中，请稍后...");
                HttpMethod1.uploadFile(HttpConstant.UPDATE_FILES, list, mHandler);
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
//        firstList.add(data);
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
            BaseBean baseBean = null;
            switch (msg.what) {
                case HandlerConstant1.GET_LIVE_CONTENT_SUCCESS:
                    final String message = (String) msg.obj;
                    if (TextUtils.isEmpty(message)) {
                        return;
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        if (jsonObject.getBoolean("status")) {
                            JSONObject jsonObject2 = new JSONObject(jsonObject.getString("data"));
                            JSONArray jsonArray = new JSONArray(jsonObject2.getString("postcontent"));

                              if(firstList.size()>=jsonArray.length()){
                                  return;
                              }
                              firstList.clear();
                            for (int i = 0, len = jsonArray.length(); i < len; i++) {
                                JSONObject jsonObject3 = jsonArray.getJSONObject(i);
                                   ReleaseContentsBean data = new ReleaseContentsBean(jsonObject3.getString("content"),jsonObject3.getInt("type"),jsonObject3.isNull("strLength") ? "" : jsonObject3.getString("strLength"),jsonObject3.getLong("timeLength"));
                                   firstList.add(data);

//                                addList(jsonObject3.getString("content"), jsonObject3.getInt("type"), jsonObject3.isNull("strLength") ? "" : jsonObject3.getString("strLength"), jsonObject3.getLong("timeLength"));
                            }
                              mAdapter.notifyDataSetChanged();
                              if(isBottom){
                                  rvReleaseContent.scrollToPosition(mAdapter.getItemCount() - 1);
                              }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case HandlerConstant1.UPLOAD_HEAD_SUCCESS:
                    UploadFile bean = (UploadFile) msg.obj;
                    if (null == bean) {
                        return;
                    }
                    if (bean.isStatus()) {
                        String url = bean.getData().getUrl();
                        listData.clear();
                        listData.add(new ReleaseContentsBean(url, fileType, voiceStrLength, voiceLength));

                        //上传图片或者mp3
                        sendMsg();
                    } else {
                        showMsg(bean.getErrorMsg());
                    }
                    break;
                //发布文字成功
                case HandlerConstant1.SEND_TEXT_SUCCESS:
                    baseBean = (BaseBean) msg.obj;
                    if (null == baseBean) {
                        return;
                    }
                    if (baseBean.status) {
                        switch (fileType) {
                            case ReleaseContentsBean.TEXT:
                                addList(etContent.getText().toString().trim(), fileType, null, 0);
                                etContent.setText("");
                                break;
                            case ReleaseContentsBean.IMG:
                                addList(mOutputUri, fileType, voiceStrLength, voiceLength);
                                break;
                            case ReleaseContentsBean.RECORD:
                                addList(mp3Path, fileType, voiceStrLength, voiceLength);
                                break;
                        }
                    } else {
                        showMsg(baseBean.getErrorMsg());
                    }
                    break;
                // 直播结束
                case HandlerConstant1.LIVE_END_SUCCESS:
                    baseBean = (BaseBean) msg.obj;
                    if (null == baseBean) {
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
                        Intent intent = new Intent(LiveFragment.LIVE_END_SUCCESS);
                        intent.putExtra("postId", liveList.getPostId());
                        sendBroadcast(intent);
                        finish();
                    } else {
                        showMsg(baseBean.getErrorMsg());
                    }
                    break;
                case HandlerConstant1.GET_POST_SEE_NUM_SUCCESS:
                    // 获取直播人数成功
                    SeeNumBean seeNumBean = (SeeNumBean) msg.obj;
                    if (null==seeNumBean) {
                        return;
                    }
                    if (seeNumBean.isStatus()){
                        if (null==seeNumBean.getData()) return;
                        tvRight.setText("观看人数："+seeNumBean.getData().getCount());
                    }else {
                        showMsg(seeNumBean.errorMsg);
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


    private void sendMsg() {
        String msg = MyApplication.gson.toJson(listData);
        if (!TextUtils.isEmpty(msg)) {
            if (msg.substring(0, 1).equals("[") && msg.substring(msg.length() - 1, msg.length()).equals("]")) {
                msg = msg.substring(1, msg.length() - 1);
            }
            showProgress("上传中");
            HttpMethod1.sendText(String.valueOf(liveList.getPostId()), msg, mHandler);
        }
    }


    //查询直播的详情
    private TimerUtil timerUtil;

    private void getLiveContent() {
        timerUtil=new TimerUtil(0, 6000, new TimerUtil.TimerCallBack() {
            public void onFulfill() {
        HttpMethod1.getLiveContent(String.valueOf(liveList.getPostId()), mHandler);
            }
        });
        timerUtil.start();
    }

    /**
     * 获取直播观看人数
     */
    private void getLiveSeeNumber() {
        timerUtil = new TimerUtil(0, 5000, new TimerUtil.TimerCallBack() {
            public void onFulfill() {
                HttpMethod1.getPostSeeNumber(String.valueOf(liveList.getPostId()), mHandler);
            }
        });
        timerUtil.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerUtil != null) {
            timerUtil.stop();
        }
    }
}
