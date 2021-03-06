package com.example.administrator.zhixueproject.adapter.memberManage;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.administrator.zhixueproject.R;
import com.example.administrator.zhixueproject.bean.memberManage.MemberTopicListBean;
import com.example.administrator.zhixueproject.utils.Utils;

import java.util.List;

/**
 * 会员详情大家谈话题列表adapter
 */

public class TalkAboutListAdapter extends BaseQuickAdapter<MemberTopicListBean,BaseViewHolder> {

    public TalkAboutListAdapter(@LayoutRes int layoutResId, @Nullable List<MemberTopicListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MemberTopicListBean item) {
        helper.setVisible(R.id.ll_post_reward,false);
        helper.setVisible(R.id.tv_isPay,true);//显示是否付费
        helper.setVisible(R.id.view_peep,false);
        helper.setVisible(R.id.tv_peep_num,false);//隐藏偷看

        helper.setText(R.id.tv_topic_name,item.getPostName());//话题
        helper.setText(R.id.tv_teacher_name,item.getPostWriterId());//讲师名称
        int isFree = item.getPostIsFree();//是否付费(1：免费、2：付费)
        if(isFree==1){
            helper.setText(R.id.tv_isPay,mContext.getResources().getString(R.string.free));
        }else if(isFree==2){
            helper.setText(R.id.tv_isPay,mContext.getResources().getString(R.string.pay));
        }

        helper.setText(R.id.tv_time,item.getPostCreationTime());//时间
        String content=item.getPostContent();
        helper.setText(R.id.tv_content, Utils.getChineseChar(content));//话题内容
        int scanNum = item.getPostSeeNum();//浏览人数
        helper.setText(R.id.tv_scan_number, "浏览 "+scanNum);
        int commentNum = item.getPostTalkNum();//评论人数
        helper.setText(R.id.tv_comment_num, "评论 "+commentNum);

    }
}
