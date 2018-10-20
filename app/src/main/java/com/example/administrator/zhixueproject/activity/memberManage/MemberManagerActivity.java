package com.example.administrator.zhixueproject.activity.memberManage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.administrator.zhixueproject.R;
import com.example.administrator.zhixueproject.activity.BaseActivity;
import com.example.administrator.zhixueproject.adapter.memberManage.MemberManagerAdapter;
import com.example.administrator.zhixueproject.application.MyApplication;
import com.example.administrator.zhixueproject.bean.memberManage.AttendanceBean;
import com.example.administrator.zhixueproject.bean.memberManage.MemberLevelBean;
import com.example.administrator.zhixueproject.bean.memberManage.MemberManagerBean;
import com.example.administrator.zhixueproject.fragment.memberManage.MemberLevelFragment;
import com.example.administrator.zhixueproject.http.HandlerConstant1;
import com.example.administrator.zhixueproject.http.HandlerConstant2;
import com.example.administrator.zhixueproject.http.method.HttpMethod2;
import com.example.administrator.zhixueproject.utils.InputMethodUtils;
import com.example.administrator.zhixueproject.utils.MaxTextLengthFilter;
import com.example.administrator.zhixueproject.view.refreshlayout.MyRefreshLayout;
import com.example.administrator.zhixueproject.view.refreshlayout.MyRefreshLayoutListener;
import java.util.List;

/**
 * c端会员管理
 *
 * @author PeterGee
 * @date 2018/10/20
 */
public class MemberManagerActivity extends BaseActivity implements View.OnClickListener, MyRefreshLayoutListener, BaseQuickAdapter.OnItemChildClickListener, MemberLevelFragment.OnMemberLevelListener {

    private MemberManagerAdapter mMemberManagerAdapter;
    private boolean isShowLevel = false;
    private MemberLevelFragment mMemberLevelFragment;
    public static String LEVEL = "level";
    public static final String MEMBER_INFO = "member_info";
    // private MemberManagerP mMemberManagerP;
    private List<MemberLevelBean> mUserCollege;
    private List<AttendanceBean> mAttendanceList;
    private int PAGE = 1;
    private String LIMIT = "10";
    private String TIMESTAMP = System.currentTimeMillis() + "";
    private String NAME = "";
    private String CollegeGradeId = "";
    private int itemCheckedPosition;
    private MyRefreshLayout mRrefreshLayout;
    private RecyclerView mRvMemberManager;
    private TextView tvMemberLevel;
    private ImageView ivDownArrow;
    private EditText etMemberSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_manager);
        initView();
    }

    private void initView() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.C_member_manager));
        findViewById(R.id.lin_back).setOnClickListener(this);
        mRrefreshLayout = (MyRefreshLayout) findViewById(R.id.refresh_layout);
        mRvMemberManager = (RecyclerView) findViewById(R.id.rv_member_manager);
        tvMemberLevel = (TextView) findViewById(R.id.tv_member_level);
        ivDownArrow = (ImageView) findViewById(R.id.iv_down_arrow);
        // 搜索
        etMemberSearch = (EditText) findViewById(R.id.et_member_search);
        findViewById(R.id.rl_member_level).setOnClickListener(this);

        mMemberManagerAdapter = new MemberManagerAdapter(R.layout.member_manager_item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRvMemberManager.setAdapter(mMemberManagerAdapter);
        mRvMemberManager.setLayoutManager(linearLayoutManager);
        mRrefreshLayout.setMyRefreshLayoutListener(this);
        mMemberManagerAdapter.setOnItemChildClickListener(this);
        mMemberManagerAdapter.setEmptyView(R.layout.empty_view, (ViewGroup) mRvMemberManager.getParent());
        //清空等级首选项
        MyApplication.spUtil.addString(LEVEL, "");

        //编辑框，软键盘搜索按钮监听
        etMemberSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodUtils.hideInputMethod(v);//隐藏软键盘
                    NAME = etMemberSearch.getText().toString().trim();
                    getVipList(HandlerConstant2.GET_VIP_LIST_SUCCESS1);//搜索
                    return true;
                }
                return false;
            }
        });
        //搜索内容超出20字提示
        etMemberSearch.setFilters(new InputFilter[]{new MaxTextLengthFilter(20, getResources().getString(R.string.et_length_tip))});
    }

    /**
     * 获取c端会员列表
     *
     * @param index
     */
    private void getVipList(int index) {
        showProgress(getString(R.string.loading));
        HttpMethod2.getVipList(NAME, CollegeGradeId, TIMESTAMP, PAGE+"", LIMIT, index, mHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMemberManagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lin_back:
                finish();
                break;
            case R.id.rl_member_level://会员等级
                isShowLevel = !isShowLevel;
                showFragment(isShowLevel);
                break;
            default:
                break;
        }
    }

    /**
     * 是否显示城市选择fm
     *
     * @param show
     */
    private void showFragment(boolean show) {
        if (mMemberLevelFragment == null) {
            mMemberLevelFragment = new MemberLevelFragment();
            mMemberLevelFragment.setOnMemberLevelListener(this);
        }
        mMemberLevelFragment.setList(mUserCollege);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (show) {
            //防止快速点击
            if (mMemberLevelFragment.isAdded()) {
                return;
            }
            transaction.add(R.id.fl_contener, mMemberLevelFragment).commit();
            tvMemberLevel.setTextColor(getResources().getColor(R.color.color_48c6ef));
            ivDownArrow.setImageResource(R.mipmap.up_arrow_blue);
        } else {
            transaction.remove(mMemberLevelFragment).commit();
            tvMemberLevel.setTextColor(getResources().getColor(R.color.color_333333));
            ivDownArrow.setImageResource(R.mipmap.down_arrow);
        }
    }

    @Override
    public void onRefresh(View view) {
        PAGE = 1;
        getVipList(HandlerConstant2.GET_VIP_LIST_SUCCESS1);
    }

    @Override
    public void onLoadMore(View view) {
        PAGE++;
        getVipList(HandlerConstant2.GET_VIP_LIST_SUCCESS2);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
        switch (view.getId()) {
            case R.id.tv_edit://编辑_跳转会员设置页面
                this.itemCheckedPosition = position;
                // Intent starter = new Intent(this,MemberSettingUI.class);
                //starter.putExtra(MEMBER_INFO,mAttendanceList.get(position));
                //startActivityForResult(starter,REQUEST_CODE_EDIT);
                break;
            case R.id.tv_kick_out://踢出
                // mMemberManagerP.kickOutVip(C,mAttendanceList.get(position).getAttendId()+"",CollegeId,position);
                break;
            case R.id.content://条目点击事件
                //跳转会员详情页面
                this.itemCheckedPosition = position;
                //Intent starter2 = new Intent(this,MemberDetailUI.class);
                //starter2.putExtra(MEMBER_INFO,mAttendanceList.get(position));
                //startActivityForResult(starter2,REQUEST_CODE_DETAIL);
                break;
            default:
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            clearTask();
            MemberManagerBean bean = (MemberManagerBean) msg.obj;
            switch (msg.what) {
                case HandlerConstant2.GET_VIP_LIST_SUCCESS1:
                    break;
                case HandlerConstant2.GET_VIP_LIST_SUCCESS2:
                    break;
                case HandlerConstant1.REQUST_ERROR:
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    public void closeLevelListListener(View view, int position) {
        String level = mUserCollege.get(position).getUserCollegegradeName();
        tvMemberLevel.setText(level);
        isShowLevel=false;
        showFragment(isShowLevel);
        MyApplication.spUtil.addString(LEVEL,position+"");
        //选中会员等级的id
        CollegeGradeId=mUserCollege.get(position).getUserCollegegradeId();
        //搜索显示选中等级的会员列表
        getVipList(HandlerConstant2.GET_VIP_LIST_SUCCESS1);
    }
}
