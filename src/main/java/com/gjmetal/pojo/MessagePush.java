package com.gjmetal.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * @Package: com.gjmetal.pojo
 * @Description: 供第三方Controller层传输对象
 * @Author: liangtf
 * @Version: 1.0
 */
@ApiModel("发送模板的对象")
public class MessagePush {
    /**
     * 消息模板的ID
     */
    @ApiModelProperty("消息模板的ID")
    private String templateId;


    /**
     * 推送用户微信ID
     */
    @ApiModelProperty("推送用户微信ID集合")
    private List<String> vId;

    /**
     * 推送分组list
     */
    @ApiModelProperty("推送分组id集合")
    private List<String> typeList;


    /**
     * 消息模板的值
     */
    @ApiModelProperty("消息模板中的的值")
    private Map<String,String> map;

    /**
     * 暂未使用
     * 用户点击消息后跳转的链接
     */
    @ApiModelProperty("(暂未使用)用户点击消息后跳转的链接")
    private String url;

    public List<String> getvId() {
        return vId;
    }

    public void setvId(List<String> vId) {
        this.vId = vId;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<String> typeList) {
        this.typeList = typeList;
    }

    @Override
    public String toString() {
        return "MessagePush{" +
                "templateId='" + templateId + '\'' +
                ", vId=" + vId +
                ", typeList=" + typeList +
                ", map=" + map +
                ", url='" + url + '\'' +
                '}';
    }
}
