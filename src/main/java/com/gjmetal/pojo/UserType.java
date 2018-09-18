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

/**
 * 为用户加标签对象
 */
@ApiModel("用户加标签对象")
public class UserType {
    @ApiModelProperty("标签id")
    private String  tagid;
    @ApiModelProperty("微信openID集合")
    private List<String> openid_list;

    public String getTagid() {
        return tagid;
    }

    public void setTagid(String tagid) {
        this.tagid = tagid;
    }

    public List<String> getOpenid_list() {
        return openid_list;
    }

    public void setOpenid_list(List<String> openid_list) {
        this.openid_list = openid_list;
    }

    @Override
    public String toString() {
        return "UserType{" +
                "tagid='" + tagid + '\'' +
                ", openid_list=" + openid_list +
                '}';
    }
}
