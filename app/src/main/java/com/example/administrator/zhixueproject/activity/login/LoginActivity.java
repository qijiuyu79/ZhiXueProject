package com.example.administrator.zhixueproject.activity.login;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.zhixueproject.R;
import com.example.administrator.zhixueproject.activity.BaseActivity;
import com.example.administrator.zhixueproject.activity.Mp3Activity;
import com.example.administrator.zhixueproject.activity.TabActivity;
import com.example.administrator.zhixueproject.application.MyApplication;
import com.example.administrator.zhixueproject.bean.Home;
import com.example.administrator.zhixueproject.bean.UserInfo;
import com.example.administrator.zhixueproject.http.HandlerConstant1;
import com.example.administrator.zhixueproject.http.method.HttpMethod1;
import com.example.administrator.zhixueproject.utils.CodeUtils;
import com.example.administrator.zhixueproject.utils.SPUtil;
import com.example.administrator.zhixueproject.utils.UpdateVersionUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import org.json.JSONObject;
/**
 * 登陆
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private EditText etMobile,etPwd,etCode;
    private ImageView imgCode;
    public static final String ACTION_WEIXIN_LOGIN_OPENID="com.admin.broadcast.action.weixin_login_openid";
    //微信的openId
    private String openId;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        registerReceiver();
        //查询最新版本
        new UpdateVersionUtils().searchVersion(this);
    }


    /**
     * 初始化控件
     */
    //18610717775   a123456
    //13121430770   123456
    private void initView(){
        TextView tvTitle=(TextView)findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.main_title));
        TextView tvRegister=(TextView)findViewById(R.id.tv_right);
        tvRegister.setVisibility(View.GONE);
        etMobile=(EditText)findViewById(R.id.et_telphone) ;
        etPwd=(EditText)findViewById(R.id.et_pwd);
        etCode=(EditText)findViewById(R.id.et_code);
        imgCode=(ImageView) findViewById(R.id.iv_get_code);
        imgCode.setImageBitmap(CodeUtils.getInstance().createBitmap());
        findViewById(R.id.tv_login).setOnClickListener(this);
        findViewById(R.id.iv_get_code).setOnClickListener(this);
        findViewById(R.id.tv_forget_pwd).setOnClickListener(this);
        findViewById(R.id.tv_weixin_login).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //注册
            case R.id.tv_right:
                setClass(RegisterActivity.class);
                break;
            //刷新验证码
            case R.id.iv_get_code:
                imgCode.setImageBitmap(CodeUtils.getInstance().createBitmap());
                break;
            //登陆
            case R.id.tv_login:
                String mobile=etMobile.getText().toString().trim();
                String pwd=etPwd.getText().toString().trim();
                String code=etCode.getText().toString().trim();
                String realCode = CodeUtils.getInstance().getCode();
                if (TextUtils.isEmpty(mobile)){
                    showMsg(getString(R.string.login_phone));
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    showMsg(getString(R.string.login_pwd));
                    return;
                }
                if (pwd.length() < 6) {
                    showMsg(getString(R.string.login_long_pwd));
                    return;
                }
                if(TextUtils.isEmpty(code)){
                    showMsg(getString(R.string.login_right_code));
                    return;
                }
                if(!TextUtils.equals(realCode,code)){
                    showMsg(getString(R.string.code_error));
                    return;
                }
                showProgress("登录中...");
                HttpMethod1.login(mobile,pwd,mHandler);
                break;
            //忘记密码
            case R.id.tv_forget_pwd:
                setClass(SettingPwdActivity.class);
                break;
            //微信登陆
            case R.id.tv_weixin_login:
                 if (!MyApplication.api.isWXAppInstalled()) {
                     showMsg("请先安装微信客户端!");
                 }else{
                     final SendAuth.Req req = new SendAuth.Req();
                     req.scope = "snsapi_userinfo";
                     req.state = "wechat_sdk_demo_test";
                     MyApplication.api.sendReq(req);
                 }
                 break;
            case R.id.lin_back:
                finish();
                break;
            default:
                break;
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UserInfo userInfo;
            switch (msg.what){
                //登陆回执
                case HandlerConstant1.LOGIN_SUCCESS:
                    userInfo= (UserInfo) msg.obj;
                    if(null==userInfo){
                        clearTask();
                        return;
                    }
                    if(userInfo.isStatus()){
                        MyApplication.userInfo=userInfo;
                        loginSuccess(userInfo);
                    }else{
                        clearTask();
                        showMsg(userInfo.getErrorMsg());
                    }
                    break;
                //微信登陆回执
                case HandlerConstant1.WEIXIN_LOGIN_SUCCESS:
                      final String message= (String) msg.obj;
                      if(TextUtils.isEmpty(message)){
                          clearTask();
                          return;
                      }
                      try {
                          final JSONObject jsonObject=new JSONObject(message);
                          if(jsonObject.getBoolean("status")){
                              userInfo=MyApplication.gson.fromJson(message,UserInfo.class);
                              MyApplication.userInfo=userInfo;
                              loginSuccess(userInfo);
                          }else{
                              clearTask();
                              if(jsonObject.getString("errorCode").equals("200203") || jsonObject.getString("errorCode").equals("200214")){
                                  Intent intent=new Intent(mContext,RegisterActivity.class);
                                  intent.putExtra("openId",openId);
                                  startActivity(intent);
                              }else{
                                  showMsg(jsonObject.getString("errorMsg"));
                              }
                          }
                      }catch (Exception e){
                          e.printStackTrace();
                      }
                      break;
                case HandlerConstant1.GET_HOME_INFO_SUCCESS:
                     clearTask();
                     final Home home= (Home) msg.obj;
                     if(null==home){
                        return;
                     }
                     if(home.isStatus()){
                        MyApplication.homeBean=home.getData().getCollege();
                         MyApplication.homeBean.setAttendType(home.getData().getType());
                         MyApplication.spUtil.addString(SPUtil.HOME_INFO,MyApplication.gson.toJson(MyApplication.homeBean));
                        setClass(TabActivity.class);
                        finish();
                     }else{
                        showMsg(home.getErrorMsg());
                     }
                     break;
                case HandlerConstant1.REQUST_ERROR:
                    clearTask();
                    showMsg(getString(R.string.net_error));
                    break;
                default:
                    break;
            }
        }
    };


    private void loginSuccess(UserInfo userInfo){
        MyApplication.spUtil.addString(SPUtil.LOGIN_MOBILE,etMobile.getText().toString().trim());
        MyApplication.spUtil.addString(SPUtil.USER_INFO,MyApplication.gson.toJson(userInfo));
        //保存token
        MyApplication.spUtil.addString(SPUtil.TOKEN,userInfo.getData().getToken());

        //查询首页信息
        getHomeInfo();
    }


    /**
     * 查询首页信息
     */
    private void getHomeInfo(){
        HttpMethod1.getHomeInfo(mHandler);
    }


    /**
     * 注册广播
     */
    private void registerReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ACTION_WEIXIN_LOGIN_OPENID);
        // 注册广播监听
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals(ACTION_WEIXIN_LOGIN_OPENID)){
                openId=intent.getStringExtra("openId");
                showProgress("微信登录中");
                HttpMethod1.wxLogin("0",openId,"0",null,null,null,null,null,mHandler);
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        final String mobile=MyApplication.spUtil.getString(SPUtil.LOGIN_MOBILE);
        etMobile.setText(mobile);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
