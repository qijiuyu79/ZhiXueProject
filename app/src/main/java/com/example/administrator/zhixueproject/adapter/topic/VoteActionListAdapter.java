package com.example.administrator.zhixueproject.adapter.topic;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.administrator.zhixueproject.R;
import com.example.administrator.zhixueproject.bean.topic.AddVoteBean;
import com.example.administrator.zhixueproject.bean.topic.VoteDetailListBean;
import java.util.List;

/**
 * Created by geqipeng
 * date  : 2018/11/24
 * desc:  投票选项
 */
public class VoteActionListAdapter extends BaseQuickAdapter<AddVoteBean, BaseViewHolder> {

    public VoteActionListAdapter(@LayoutRes int layoutResId, @Nullable List<AddVoteBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, AddVoteBean item) {
        baseViewHolder.setText(R.id.tv_comment_reply,item.getContent());
    }

}
