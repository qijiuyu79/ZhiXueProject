package com.example.administrator.zhixueproject.bean.memberManage;

import com.example.administrator.zhixueproject.bean.BaseBean;

import java.util.List;

/**
 * 勋章管理
 */

public class MedalBean extends BaseBean {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * medalTypeList : [{"medalTypeId":145,"medalTypeMig":"http://mpic.tiankong.com/a45/e04/a45e0429e5828421d9d08f780dccd5ac/640.jpg","medalTypeInfo":"被赞数超过200","medalTypeName":"宝宝最赞"}]
         * totalPage : 0
         * isUpdated : false
         * totalRecord : 0
         * timestamp : 1511252842708
         */

        private int totalPage;
        private boolean isUpdated;
        private int totalRecord;
        private String timestamp;
        private List<MedalInfoBean> medalTypeList;

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public boolean isIsUpdated() {
            return isUpdated;
        }

        public void setIsUpdated(boolean isUpdated) {
            this.isUpdated = isUpdated;
        }

        public int getTotalRecord() {
            return totalRecord;
        }

        public void setTotalRecord(int totalRecord) {
            this.totalRecord = totalRecord;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public List<MedalInfoBean> getMedalTypeList() {
            return medalTypeList;
        }

        public void setMedalTypeList(List<MedalInfoBean> medalTypeList) {
            this.medalTypeList = medalTypeList;
        }

    }
}
