package com.example.administrator.zhixueproject.http.api;

import com.example.administrator.zhixueproject.bean.BaseBean;
import com.example.administrator.zhixueproject.bean.UploadFile;
import com.example.administrator.zhixueproject.bean.live.SelectLecturersBean;
import com.example.administrator.zhixueproject.bean.topic.ActionManageBean;
import com.example.administrator.zhixueproject.bean.topic.ActionNeophyteBean;
import com.example.administrator.zhixueproject.bean.topic.PostsCourseBean;
import com.example.administrator.zhixueproject.bean.topic.PostsDetailsBean;
import com.example.administrator.zhixueproject.bean.topic.TopicsListBean;
import com.example.administrator.zhixueproject.bean.topic.VoteManageBean;
import com.example.administrator.zhixueproject.bean.topic.VoteNeophyteBean;
import com.example.administrator.zhixueproject.http.HttpConstant;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface HttpApi2 {
    /**
     * 查询话题列表
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_TOPIC_LIST)
    Call<TopicsListBean> getTopicList(@FieldMap Map<String,String> map);

    /**
     * 修改个人资料
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.MODIFY_USER_INFO)
    Call<BaseBean> modifyUserInfo(@FieldMap Map<String,String> map);

    /**
     *  话题上下架
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.IS_UP_OR_DOWN)
    Call<TopicsListBean> isUpOrDown(@FieldMap Map<String,String> map);

    /**
     *  添加话题
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.ADD_TOPIC)
    Call<TopicsListBean> addTopic(@FieldMap Map<String,String> map);

    /**
     *  修改话题
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.UPDATE_TOPIC)
    Call<TopicsListBean> updateTopic(@FieldMap Map<String,String> map);

    /**
     *  话题排序
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.UPDATE_SORT)
    Call<TopicsListBean> updateSort(@FieldMap Map<String,String> map);

    /**
     *  获取帖子列表
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_POST_LIST)
    Call<PostsCourseBean> getPostList(@FieldMap Map<String,String> map);

    /**
     *  获取讲师列表
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_TEACHER_LIST)
    Call<SelectLecturersBean> getLecturersList(@FieldMap Map<String,String> map);

    /**
     *  发布帖子
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.ADD_POST)
    Call<UploadFile> addPost(@FieldMap Map<String,String> map);

    /**
     *  修改帖子
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.UPDATE_POST)
    Call<UploadFile> updatePost(@FieldMap Map<String,String> map);

    /**
     *  添加活动
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.ADD_ACTIVITY)
    Call<UploadFile> addActivity(@FieldMap Map<String,String> map);

    /**
     *  修改活动
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.UPDATE_ACTIVITY)
    Call<UploadFile> updateActivity(@FieldMap Map<String,String> map);

    /**
     *  添加投票
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.ADD_VOTE)
    Call<BaseBean> addVote(@FieldMap Map<String,String> map);


    /**
     * 评论帖子
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.COMMENT_POST)
    Call<BaseBean> commentPost(@FieldMap Map<String,String> map);

    /**
     * 评论楼层
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.COMMENT_REPLY)
    Call<BaseBean> commentReply(@FieldMap Map<String,String> map);

    /**
     * 获取帖子详情
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_POST_DETAIL)
    Call<PostsDetailsBean>getPostDetail(@FieldMap Map<String,String> map);

    /**
     * 获取有偿提问帖子详情
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_YOUCHANG_DETAIL)
    Call<PostsDetailsBean>getYouChangDetail(@FieldMap Map<String,String> map);

    /**
     * 获取活动列表
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_ACTIVITY_LIST)
    Call<ActionManageBean>getActivityList(@FieldMap Map<String,String> map);


    /**
     * 获取活动列表
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.DELETE_ACTIVITY)
    Call<ActionManageBean>deleteActivity(@FieldMap Map<String,String> map);

    /**
     * 获取活动参与者列表
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_ACTIVITY_USER_LIST)
    Call<ActionNeophyteBean>getActivityUserList(@FieldMap Map<String,String> map);

    /**
     * 获取投票活动列表
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_VOTE_LIST)
    Call<VoteManageBean>getVoteList(@FieldMap Map<String,String> map);

    /**
     * 删除投票活动
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.DELETE_VOTE)
    Call<VoteManageBean>deleteVote(@FieldMap Map<String,String> map);

    /**
     * 获取投票详情页
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST(HttpConstant.GET_VOTE_DETAIL)
    Call<VoteNeophyteBean>getVoteDetail(@FieldMap Map<String,String> map);
}
