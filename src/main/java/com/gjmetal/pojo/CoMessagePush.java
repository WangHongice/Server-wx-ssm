package com.gjmetal.pojo;

/**
 * @Package: com.gjmetal.pojo
 * @Description:
 * @Author: liangtf
 * @Version: 1.0
 */

import java.util.List;
import java.util.Map;

/**
 * 企业发送应用消息实体
 */
public class CoMessagePush {

    private String touser;

    private String toparty;

    private String totag;

    private String msgtype;

    private String agentid;

    private Map<String,String> text;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getToparty() {
        return toparty;
    }

    public void setToparty(String toparty) {
        this.toparty = toparty;
    }

    public String getTotag() {
        return totag;
    }

    public void setTotag(String totag) {
        this.totag = totag;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getAgentid() {
        return agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid;
    }

    public Map<String, String> getText() {
        return text;
    }

    public void setText(Map<String, String> text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "CoMessagePush{" +
                "touser='" + touser + '\'' +
                ", toparty='" + toparty + '\'' +
                ", totag='" + totag + '\'' +
                ", msgtype='" + msgtype + '\'' +
                ", agentid='" + agentid + '\'' +
                ", text=" + text +
                '}';
    }
}
