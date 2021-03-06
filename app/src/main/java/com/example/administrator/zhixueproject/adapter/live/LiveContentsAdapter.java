package com.example.administrator.zhixueproject.adapter.live;

import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.administrator.zhixueproject.R;
import com.example.administrator.zhixueproject.bean.topic.ReleaseContentsBean;

import java.util.List;


public class LiveContentsAdapter extends BaseMultiItemQuickAdapter<ReleaseContentsBean, BaseViewHolder> {

    public LiveContentsAdapter(@Nullable List<ReleaseContentsBean> data) {
        super(data);
        addItemType(ReleaseContentsBean.IMG, R.layout.institution_img);
        addItemType(ReleaseContentsBean.TEXT, R.layout.institution_text);
        addItemType(ReleaseContentsBean.RECORD, R.layout.institution_record);
    }

    @Override
    protected void convert(final BaseViewHolder helper, ReleaseContentsBean item) {
        switch (helper.getItemViewType()) {
            case ReleaseContentsBean.IMG:
                ImageView delete=helper.getView(R.id.iv_delete);
                delete.setVisibility(View.GONE);
                Glide.with(mContext).load(item.getContent()).into((ImageView) helper.getView(R.id.iv_img));
                break;
            case ReleaseContentsBean.TEXT:
                helper.setText(R.id.et_text, item.getContent());
                EditText etText = helper.getView(R.id.et_text);
                etText.setSelection(item.getContent().length());
                etText.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (changInstitutionDataListener != null) {
                            if (TextUtils.isEmpty(charSequence.toString())) {
                                changInstitutionDataListener.onDeleteItemListener(helper.getAdapterPosition());
                            } else {
                                changInstitutionDataListener.onChangInstitutionDataListener(helper.getAdapterPosition(), charSequence.toString());
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                break;
            case ReleaseContentsBean.RECORD:
                helper.setTag(R.id.iv_record_play,item);
                ImageView delete2=helper.getView(R.id.iv_delete);
                delete2.setVisibility(View.GONE);
                helper.addOnClickListener(R.id.iv_record_play);
//                long minutes = TimeUnit.MILLISECONDS.toMinutes(item.getTimeLength());
//                long seconds = TimeUnit.MILLISECONDS.toSeconds(item.getTimeLength()) - TimeUnit.MINUTES.toSeconds(minutes);
//                helper.setText(R.id.file_length_text_view, String.format("%02d:%02d", minutes,seconds));
                helper.setText(R.id.file_length_text_view, item.getStrLength());
                break;
        }
    }


    private ChangInstitutionDataListener changInstitutionDataListener;

    public interface ChangInstitutionDataListener {
        void onChangInstitutionDataListener(int position, String data);

        void onDeleteItemListener(int position);
    }

    public void setChangInstitutionDataListener(ChangInstitutionDataListener changInstitutionDataListener) {
        this.changInstitutionDataListener = changInstitutionDataListener;
    }
}
