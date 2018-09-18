package com.gjmetal.pojo;

/**
 * @Package: com.gjmetal.pojo
 * @Description:
 * @Author: liangtf
 * @Version: 1.0
 */

import java.util.List;

/***
 * 企业增加标签成员实体
 */
public class CoTypeUser {

    private String tagid;

    private List<String> userlist;

    // 暂未使用
    private List<String> partylist;

    public String getTagid() {
        return tagid;
    }

    public void setTagid(String tagid) {
        this.tagid = tagid;
    }

    public List<String> getUserlist() {
        return userlist;
    }

    public void setUserlist(List<String> userlist) {
        this.userlist = userlist;
    }

    public List<String> getPartylist() {
        return partylist;
    }

    public void setPartylist(List<String> partylist) {
        this.partylist = partylist;
    }

    @Override
    public String toString() {
        return "CoTypeUser{" +
                "tagid='" + tagid + '\'' +
                ", userlist=" + userlist +
                ", partylist=" + partylist +
                '}';
    }
}
