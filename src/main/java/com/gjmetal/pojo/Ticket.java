package com.gjmetal.pojo;

/**
 * @Package: com.gjmetal.pojo
 * @Description:
 * @Author: liangtf
 * @Version: 1.0
 */

import java.util.Map;

/**
 * 获取ticket的实体对象,不是返回ticket对象
 * 参见https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1443433542生成带参二维码
 */
public class Ticket {
    private String expire_seconds;

    private String action_name;

    private Map<String,Map<String,String>> action_info;

    public Ticket(String expire_seconds, String action_name, Map<String, Map<String, String>> action_info) {
        this.expire_seconds = expire_seconds;
        this.action_name = action_name;
        this.action_info = action_info;
    }

    public String getExpire_seconds() {
        return expire_seconds;
    }

    public void setExpire_seconds(String expire_seconds) {
        this.expire_seconds = expire_seconds;
    }

    public String getAction_name() {
        return action_name;
    }

    public void setAction_name(String action_name) {
        this.action_name = action_name;
    }

    public Map<String, Map<String, String>> getAction_info() {
        return action_info;
    }

    public void setAction_info(Map<String, Map<String, String>> action_info) {
        this.action_info = action_info;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "expire_seconds='" + expire_seconds + '\'' +
                ", action_name='" + action_name + '\'' +
                ", action_info=" + action_info +
                '}';
    }
}
