package com.gjmetal.pojo;

/**
 * @Package: com.gjmetal.pojo
 * @Description:
 * @Author: liangtf
 * @Version: 1.0
 */

import java.util.List;
import java.util.Map;

/***
 * 标签下所有对象实体
 */
public class TagUser {
   private String count;

   private String next_openid;

   private Map<String,List<String>> data;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getNext_openid() {
        return next_openid;
    }

    public void setNext_openid(String next_openid) {
        this.next_openid = next_openid;
    }

    public Map<String, List<String>> getData() {
        return data;
    }

    public void setData(Map<String, List<String>> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TagUser{" +
                "count='" + count + '\'' +
                ", next_openid='" + next_openid + '\'' +
                ", data=" + data +
                '}';
    }
}
