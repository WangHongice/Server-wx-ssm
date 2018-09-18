package com.gjmetal.pojo;

/**
 * @Package: com.gjmetal.pojo
 * @Description:
 * @Author: liangtf
 * @Version: 1.0
 */


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 标签对象
 */
@ApiModel("标签对象")
public class Tag {
    @ApiModelProperty("标签id")
    private String id;
    @ApiModelProperty("标签名称")
    private String name;
    @ApiModelProperty("标签下的人数")
    private String count;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
