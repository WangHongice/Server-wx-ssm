package com.gjmetal.pojo;

import java.util.Map;

/**
 * @Package: com.gjmetal.pojo
 * @Description:
 * @Author: liangtf
 * @Version: 1.0
 */

/***
 * 发给微信的消息模板
 */
public class TemplateMessage {
    private String touser; //用户OpenID

    private String template_id; //模板消息ID

    private String url; //URL置空，在发送后，点模板消息进入一个空白页面（ios），或无法点击（android）。

    /**
     * 注意要于模板设置的字段保持一致
     */
    private Map<String, String> data; //模板详细信息

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TemplateMessage{" +
                "touser='" + touser + '\'' +
                ", template_id='" + template_id + '\'' +
                ", url='" + url + '\'' +
                ", data=" + data +
                '}';
    }
}
