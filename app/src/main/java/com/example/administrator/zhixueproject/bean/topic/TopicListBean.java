package com.example.administrator.zhixueproject.bean.topic;

import java.io.Serializable;


public class TopicListBean implements Serializable {
    /**
     * topicId : 259
     * creationTime : 2017-07-11 13:07
     * topicImg : http://bjzx2016.oss-cn-shanghai.aliyuncs.com/college/upload/image/57bff74f-4df1-48a3-a662-d04a85db4b0d.png
     * topicUseyn : 1
     * topicName : 微课——德鲁克经典五问
     * topicPayType : 1
     * topicType : 1
     */

    private long topicId;
    private String creationTime;
    private String topicImg;
    private int topicUseyn;
    private String topicName;
    private int topicPayType;
    private int topicType;
    private double topicPrice;
    private String topicVipName;
    private int topicIsTop;


    public int getTopicIsTop() {
        return topicIsTop;
    }

    public void setTopicIsTop(int topicIsTop) {
        this.topicIsTop = topicIsTop;
    }


    public String getTopicVipName() {
        return topicVipName;
    }

    public void setTopicVipName(String topicVipName) {
        this.topicVipName = topicVipName;
    }


    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getTopicImg() {
        return topicImg;
    }

    public void setTopicImg(String topicImg) {
        this.topicImg = topicImg;
    }

    public int getTopicUseyn() {
        return topicUseyn;
    }

    public void setTopicUseyn(int topicUseyn) {
        this.topicUseyn = topicUseyn;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getTopicPayType() {
        return topicPayType;
    }

    public void setTopicPayType(int topicPayType) {
        this.topicPayType = topicPayType;
    }

    public int getTopicType() {
        return topicType;
    }

    public void setTopicType(int topicType) {
        this.topicType = topicType;
    }

    public void setTopicPrice(double topicPrice) {
        this.topicPrice = topicPrice;
    }

    public double getTopicPrice() {
        return topicPrice;
    }
}
