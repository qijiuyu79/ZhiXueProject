package com.example.administrator.zhixueproject.adapter.topic;

import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.administrator.zhixueproject.R;
import com.example.administrator.zhixueproject.bean.topic.ReleaseContentsBean;
import com.example.administrator.zhixueproject.utils.LogUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class ReleaseContentsAdapter extends BaseMultiItemQuickAdapter<ReleaseContentsBean, BaseViewHolder> {

    public ReleaseContentsAdapter(@Nullable List<ReleaseContentsBean> data) {
        super(data);
        addItemType(ReleaseContentsBean.IMG, R.layout.institution_img);
        addItemType(ReleaseContentsBean.TEXT, R.layout.institution_text);
        addItemType(ReleaseContentsBean.RECORD, R.layout.institution_record);
    }

    @Override
    protected void convert(final BaseViewHolder helper, ReleaseContentsBean item) {
        switch (helper.getItemViewType()) {
            case ReleaseContentsBean.IMG:
                helper.addOnClickListener(R.id.iv_delete);
                LogUtils.e("url=  "+item.getContent());
                String url=item.getContent();
                Glide.with(mContext).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into((ImageView) helper.getView(R.id.iv_img));
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
                LogUtils.e("record");
                long minutes = TimeUnit.MILLISECONDS.toMinutes(item.getTimeLength());
                long seconds = TimeUnit.MILLISECONDS.toSeconds(item.getTimeLength()) - TimeUnit.MINUTES.toSeconds(minutes);
                helper.setText(R.id.file_length_text_view, String.format("%02d:%02d", minutes,seconds));
                helper.setText(R.id.file_length_text_view, item.getStrLength());
                helper.addOnClickListener(R.id.iv_delete);
                helper.addOnClickListener(R.id.iv_record_play);
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
