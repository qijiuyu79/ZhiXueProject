package com.example.administrator.zhixueproject.fragment.memberManage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.administrator.zhixueproject.R;
import com.example.administrator.zhixueproject.activity.memberManage.MemberDetailActivity;
import com.example.administrator.zhixueproject.activity.topic.PostDetailValueActivity;
import com.example.administrator.zhixueproject.adapter.memberManage.PaidQuestionListAdapter;
import com.example.administrator.zhixueproject.bean.memberManage.MemberDetailBean;
import com.example.administrator.zhixueproject.bean.memberManage.MemberTopicListBean;
import com.example.administrator.zhixueproject.bean.topic.PostListBean;
import com.example.administrator.zhixueproject.fragment.BaseFragment;
import com.example.administrator.zhixueproject.http.HandlerConstant1;
import com.example.administrator.zhixueproject.http.HandlerConstant2;
import com.example.administrator.zhixueproject.http.method.HttpMethod2;
import com.example.administrator.zhixueproject.utils.DateUtil;
import com.example.administrator.zhixueproject.utils.LogUtils;
import com.example.administrator.zhixueproject.view.refreshlayout.MyRefreshLayout;
import com.example.administrator.zhixueproject.view.refreshlayout.MyRefreshLayoutListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 有偿问答
 */
public class PaidQuestionFragment extends BaseFragment implements MyRefreshLayoutListener, BaseQuickAdapter.OnItemClickListener {
    public static final String TYPE_PAY_QUESTION = "3";//有偿
    private List<MemberTopicListBean> mQuestionList=new ArrayList<>();
    private PaidQuestionListAdapter mPaitQuestionListAdapter;
    private int PAGE = 1;
    private String LIMIT = "10";
    private String TIMESTAMP = "";
    private String attendId;
    private MyRefreshLayout refreshLayout;
    private RecyclerView rvMemberDetailList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_talk_about, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        attendId = getArguments().getString(MemberDetailActivity.ATTEND_ID);
        refreshLayout = (MyRefreshLayout) view.findViewById(R.id.refresh_layout);
        rvMemberDetailList = (RecyclerView) view.findViewById(R.id.rv_talk_about_list);
        refreshLayout.setMyRefreshLayoutListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvMemberDetailList.setLayoutManager(linearLayoutManager);
        getVipInfo(HandlerConstant2.GET_VIP_INFO_SUCCESS);
        adapterView();
    }

    /**
     * 查询会员详情
     *
     * @param index
     */
    private void getVipInfo(int index) {
        TIMESTAMP= DateUtil.getTime();
        showProgress(getString(R.string.loading));
        HttpMethod2.getVipInfo(attendId, TYPE_PAY_QUESTION, TIMESTAMP, PAGE + "", LIMIT, index, mHandler);
    }

    @Override
    public void onRefresh(View view) {
        PAGE = 1;
        getVipInfo(HandlerConstant2.GET_VIP_INFO_SUCCESS);
    }

    @Override
    public void onLoadMore(View view) {
        PAGE++;
        getVipInfo(HandlerConstant2.GET_VIP_INFO_SUCCESS2);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            clearTask();
            MemberDetailBean bean = (MemberDetailBean) msg.obj;
            switch (msg.what) {
                // 刷新成功
                case HandlerConstant2.GET_VIP_INFO_SUCCESS:
                    requestDataSuccess(bean);
                    break;
                // 加载成功
                case HandlerConstant2.GET_VIP_INFO_SUCCESS2:
                    loadMoreSuccess(bean);
                    break;
                case HandlerConstant1.REQUST_ERROR:
                    requestError();
                    break;
            }
        }
    };

    /**
     * 请求成功
     *
     * @param bean
     */
    private void requestDataSuccess(MemberDetailBean bean) {
        refreshLayout.refreshComplete();
        if (null == bean) {
            return;
        }
        if (bean.isStatus()) {
            mQuestionList = bean.getData().getPostList();
            LogUtils.e("mQuestionList   ="+mQuestionList.size());
            if (null!=mQuestionList&&mQuestionList.size()==0){
                return;
            }
            mPaitQuestionListAdapter.setNewData(mQuestionList);
            mPaitQuestionListAdapter.notifyDataSetChanged();
        } else {
            bean.getErrorMsg();
        }
    }

    /**
     * 加载更多成功
     *
     * @param bean
     */
    private void loadMoreSuccess(MemberDetailBean bean) {
        refreshLayout.loadMoreComplete();
        if (null == bean) {
            return;
        }
        if (bean.isStatus()) {
            if (bean.getData().getPostList().size() <= 0) {
                return;
            }
            mQuestionList.addAll(bean.getData().getPostList());
            mPaitQuestionListAdapter.setNewData(mQuestionList);
            mPaitQuestionListAdapter.notifyDataSetChanged();
        } else {
            // showMsg(bean.getErrorMsg());
        }
    }

    /**
     * 加载失败
     */
    private void requestError() {
        refreshLayout.refreshComplete();
        refreshLayout.loadMoreComplete();
        showMsg(getString(R.string.net_error));
    }

    /**
     * 设置adapter
     */
    private void adapterView() {
        mPaitQuestionListAdapter = new PaidQuestionListAdapter(R.layout.member_detail_topic_item,mQuestionList);
        rvMemberDetailList.setAdapter(mPaitQuestionListAdapter);
        //条目点击
        mPaitQuestionListAdapter.setOnItemClickListener(this);
        mPaitQuestionListAdapter.setEmptyView(R.layout.empty_member_detail_view, (ViewGroup) rvMemberDetailList.getParent());
    }


    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        MemberTopicListBean listBean=mQuestionList.get(i);
        PostListBean bean=new PostListBean();
        bean.setPostId(listBean.getPostId());
        bean.setPostType(3);
        bean.setPostName(listBean.getPostName());
        bean.setPostIsFree(listBean.getPostIsFree());
        bean.setPostReward(listBean.getPostReward()+"");
        bean.setPostIsTop(0);
        PostDetailValueActivity.start(getActivity(),bean,2);

    }

}
