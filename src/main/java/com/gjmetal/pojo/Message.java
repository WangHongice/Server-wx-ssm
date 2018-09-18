package com.gjmetal.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * @Package: com.gjmetal.pojo
 * @Description:
 * @Author: liangtf
 * @Version: 1.0
 */

/***
 * 标签发送普通消息实体
 */
@ApiModel("标签发送普通消息实体")
public class Message {
    @ApiModelProperty("消息类型,下面的具体参考微信公众号中的标签发送普通消息")
    private String msgtype;
    @ApiModelProperty("参考https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1481187827_i0l21")
    private Map<String,String> filter;

    private Map<String,String> text;

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }

    public Map<String, String> getText() {
        return text;
    }

    public void setText(Map<String, String> text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Message{" +
                "msgtype='" + msgtype + '\'' +
                ", filter=" + filter +
                ", text=" + text +
                '}';
    }
}
