package com.gjmetal.pojo;

/**
 * @Package: com.gjmetal.pojo
 * @Description:
 * @Author: liangtf
 * @Version: 1.0
 */

/**
 * 企业标签对象
 */
public class CoTag {
    private String tagid;
    private String tagname;

    public String getTagid() {
        return tagid;
    }

    public void setTagid(String tagid) {
        this.tagid = tagid;
    }

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    @Override
    public String toString() {
        return "CoTag{" +
                "tagid='" + tagid + '\'' +
                ", tagname='" + tagname + '\'' +
                '}';
    }
}
