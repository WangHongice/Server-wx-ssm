package com.gjmetal.pojo;

/**
 * @Package: com.gjmetal.pojo
 * @Description:
 * @Author: liangtf
 * @Version: 1.0
 */

/***
 * 企业用户对象
 */
public class CoUser {
    private String userid;

    private String name;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CoUser{" +
                "userid='" + userid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
