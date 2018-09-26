package com.example.administrator.zhixueproject.http;

public class HttpConstant {

    public static final String IP="http://kooboss.imwork.net/risenb-client-web/";

    //获取短信验证码
    public static final String GET_SMS_CODE="user/getSmsCode.do";

    //注册
    public static final String REGISTER="user/userRegister.do";

    //登陆
    public static final String LOGIN="user/login.do";

    //查询学院VIP数据
    public static final String GET_COLLETE_VIPS="college/getCollegeGradeList.do";

    //查询话题列表
    public static final String GET_TOPIC_LIST="topic/getTopicList.do";

    //查询首页信息
    public static final String GET_HOME_INFO="user/home.do";

    //修改密码
    public static final String UPDATE_PWD="user/updatePwdByMobile.do";

    // 修改个人资料
    public static final String MODIFY_USER_INFO ="user/update.do" ;

    //查询个人资料
    public static final String GET_USER_INFO="user/getInformation.do";

    //获取邮箱验证码
    public static final String GET_EMAIL_CODE="user/getEmailCode.do";

    //上传文件
    public static final String UPDATE_FILES="sys/uploadFiles.do";

    //用户加入过的更多学院
    public static final String GET_MORE_COLLEGE="user/getMoreUserCollege.do";

    //修改密码
    public static final String UPDATE_PWD2="user/updatePwd.do";

    //编辑学院
    public static final String EDIT_COLLEGE="college/editCollege.do";

    //会员等级设置
    public static final String MEMBER_LEVEL_SETTING="college/updateVipGrade.do";

    //判断验证码是否正确
    public static final String CHECK_SMS_CODE="user/checkCode.do";

    //自动登录(会话保持)
    public static final String AUTO_LOGIN="user/autoLogin.do";

    //保存会员等级
    public static final String SAVE_VIP_GRADE="college/saveVipGrade.do";

    //获取勋章列表
    public static final String GET_MEDAL_LIST="medalType/getMedalType.do";
}
