package com.example.administrator.zhixueproject.activity.college;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.example.administrator.zhixueproject.R;
import com.example.administrator.zhixueproject.activity.BaseActivity;
import com.example.administrator.zhixueproject.adapter.college.CollegeItemAdapter;
import com.example.administrator.zhixueproject.application.MyApplication;
import com.example.administrator.zhixueproject.bean.BaseBean;
import com.example.administrator.zhixueproject.bean.Colleges;
import com.example.administrator.zhixueproject.bean.Home;
import com.example.administrator.zhixueproject.bean.MyColleges;
import com.example.administrator.zhixueproject.callback.CollegeCallBack;
import com.example.administrator.zhixueproject.fragment.LeftFragment;
import com.example.administrator.zhixueproject.http.HandlerConstant1;
import com.example.administrator.zhixueproject.http.method.HttpMethod1;
import com.example.administrator.zhixueproject.utils.SPUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * 加入过的更多学院
 * Created by Administrator on 2018/9/23.
 */

public class MoreCollegeActivity extends BaseActivity implements CollegeCallBack{
    private ListView listView;
    private CollegeItemAdapter collegeItemAdapter;
    private String collegeId;
    private List<Colleges> list=new ArrayList<>();
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_college);
        initView();
        //查询加入过的学院列表
        showProgress("数据加载中");
        HttpMethod1.getMyCollege(mHandler);
    }

    /**
     * 初始化控件
     */
    private void initView(){
        TextView tvHead=(TextView)findViewById(R.id.tv_title);
        tvHead.setText(getString(R.string.joined_college));
        listView = (ListView)findViewById(R.id.rv_college_list);

        findViewById(R.id.lin_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MoreCollegeActivity.this.finish();
            }
        });
    }


    private Handler mHandler=new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            clearTask();
            switch (msg.what){
                case HandlerConstant1.GET_MY_COLLEGE_SUCCESS:
                    final MyColleges myColleges= (MyColleges) msg.obj;
                    if(null==myColleges){
                        return  false;
                    }
                    if(myColleges.isStatus()){
                        list=myColleges.getData().getColleges();
                        collegeItemAdapter=new CollegeItemAdapter(mContext,list,MoreCollegeActivity.this);
                        listView.setAdapter(collegeItemAdapter);
                    }
                     break;
                //退出学院
                case HandlerConstant1.QUIT_COLLEGE_SUCCESS:
                     final BaseBean baseBean= (BaseBean) msg.obj;
                     if(baseBean==null){
                         break;
                     }
                     if(baseBean.isStatus()){
                         for (int i=0;i<list.size();i++){
                              if(list.get(i).getCollegeId()==Integer.parseInt(collegeId)){
                                  list.remove(i);
                                  collegeItemAdapter.notifyDataSetChanged();
                                  break;
                              }
                         }
                     }else{
                         showMsg(baseBean.getErrorMsg());
                     }
                     break;
                case HandlerConstant1.GET_COLLEGE_DETAILS_SUCCESS:
                    final Home home= (Home) msg.obj;
                    if(null==home){
                        return  false;
                    }
                    if(home.isStatus()) {
                        MyApplication.homeBean = home.getData().getCollege();
                        MyApplication.spUtil.addString(SPUtil.HOME_INFO,MyApplication.gson.toJson(MyApplication.homeBean));
                        sendBroadcast(new Intent(LeftFragment.GET_COLLEGE_DETAILS));
                        finish();
                    }
                     break;
                case HandlerConstant1.REQUST_ERROR:
                    clearTask();
                    showMsg(getString(R.string.net_error));
                    break;
                default:
                    break;
            }
            return false;
        }
    });


    @Override
    public void quitCollege(String collegeId) {
        this.collegeId=collegeId;
        showProgress("退出中");
        HttpMethod1.quitCollege(Long.parseLong(collegeId),mHandler);
    }

    @Override
    public void onClick(Colleges colleges) {
        showProgress("数据加载中");
        HttpMethod1.getCollegeDetails(colleges.getCollegeId(),mHandler);
    }
}
