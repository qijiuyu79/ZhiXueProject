package com.example.administrator.zhixueproject.adapter.college;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.administrator.zhixueproject.R;
import com.example.administrator.zhixueproject.activity.college.BuyInessInActivity;
import com.example.administrator.zhixueproject.activity.college.MedalListActivity;
import com.example.administrator.zhixueproject.bean.BuyIness;
import com.example.administrator.zhixueproject.utils.DateUtil;
import com.example.administrator.zhixueproject.utils.LogUtils;

import java.util.List;

public class BuyInessInAdapter extends BaseAdapter{

	private Activity activity;
	private List<BuyIness.BusInessList> listAll;
	private BuyIness.BusInessList busInessList;
	public BuyInessInAdapter(Activity activity, List<BuyIness.BusInessList> listAll) {
		super();
		this.activity = activity;
		this.listAll=listAll;
	}

	@Override
	public int getCount() {
		return listAll==null ? 0 : listAll.size();
	}

	@Override
	public Object getItem(int position) {
		return listAll.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder = null;
		if(view==null){
			holder = new ViewHolder(); 
			view = LayoutInflater.from(activity).inflate(R.layout.buyiness_in_item, null);
			holder.imageView=(ImageView)view.findViewById(R.id.iv_business_pic);
			holder.tvTopic=(TextView)view.findViewById(R.id.tv_businessIn_topic);
			holder.tvCollege=(TextView)view.findViewById(R.id.tv_businessIn_college);
			holder.tvTime=(TextView)view.findViewById(R.id.tv_businessIn_endtime);
			holder.tvDel=(TextView)view.findViewById(R.id.tv_delete);
			holder.tvUp=(TextView)view.findViewById(R.id.tv_up);
			view.setTag(holder);
		}else{
			holder=(ViewHolder)view.getTag();
		}
		busInessList=listAll.get(position);
		String imgUrl=busInessList.getTopicImg();
		holder.imageView.setTag(R.id.imageid,imgUrl);
		if(holder.imageView.getTag(R.id.imageid)!=null && imgUrl==holder.imageView.getTag(R.id.imageid)){
			Glide.with(activity).load(imgUrl).override(105,77).centerCrop().into(holder.imageView);
		}
		if(!TextUtils.isEmpty(busInessList.getTopicName())){
			holder.tvTopic.setText(Html.fromHtml("<font color='#999999'>"+"购进话题 "+"</font>"));
			holder.tvTopic.append(busInessList.getTopicName());
		}
		holder.tvCollege.setText(busInessList.getCollegeName());
		holder.tvTime.setText(DateUtil.gethour(busInessList.getBuytopicEndtime()));
		holder.tvDel.setTag(busInessList);
		//删除
		holder.tvDel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(null==v.getTag()){
					return;
				}
				final BuyIness.BusInessList busInessList= (BuyIness.BusInessList) v.getTag();
				((BuyInessInActivity)activity).deleteBuyIness(busInessList);
			}
		});
		holder.tvUp.setTag(busInessList);
		if(busInessList.getBuytopicUp()==1){
			holder.tvUp.setText("下架");
		}else{
			holder.tvUp.setText("上架");
		}
		//上下架
		holder.tvUp.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(null==v.getTag()){
					return;
				}
				final BuyIness.BusInessList busInessList= (BuyIness.BusInessList) v.getTag();
				((BuyInessInActivity)activity).uporDown(busInessList);
			}
		});
		return view;
	}


	 private class ViewHolder{
		private ImageView imageView;
		TextView tvTopic,tvCollege,tvTime,tvDel,tvUp;
	 }
}
