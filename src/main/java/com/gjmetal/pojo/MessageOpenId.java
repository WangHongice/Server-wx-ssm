package com.gjmetal.pojo;

/**
 * @Package: com.gjmetal.pojo
 * @Description:
 * @Author: liangtf
 * @Version: 1.0
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/***
 * OpenId组发送普通消息实体
 */
@ApiModel("OpenId组发送普通消息实体")
public class MessageOpenId {
    @ApiModelProperty("openId集合,具体参考https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1481187827_i0l21")
    private List<String> touser;

    private String msgtype;

    private Map<String,String> text;

    public List<String> getTouser() {
        return touser;
    }

    public void setTouser(List<String> touser) {
        this.touser = touser;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public Map<String, String> getText() {
        return text;
    }

    public void setText(Map<String, String> text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "MessageOpenId{" +
                "touser=" + touser +
                ", msgtype='" + msgtype + '\'' +
                ", text=" + text +
                '}';
    }
}
