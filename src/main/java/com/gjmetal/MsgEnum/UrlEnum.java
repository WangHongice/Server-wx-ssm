package com.gjmetal.MsgEnum;

/**
 * @Package: com.gjmetal.MsgEnum
 * @Description:
 * @Author: liangtf
 * @Version: 1.0
 */
public enum UrlEnum {
    ACCESSTOKEN("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s","服务号获取全局AccessToken"),
    TEMPLATEMSGTOUSER("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=","根据模板ID发送消息到微信服务器"),
    NOTEMPLATEMSGTYPE("https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=","无模板ID分组发送消息"),
    NOTEMPLATEMSGOPENID("https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=","无模板ID openId集合发送消息"),
    CREATETAG("https://api.weixin.qq.com/cgi-bin/tags/create?access_token="," 创建标签 "),
    GETTAGS("https://api.weixin.qq.com/cgi-bin/tags/get?access_token=","获取公众号已创建的标签"),
    EDITTAG("https://api.weixin.qq.com/cgi-bin/tags/update?access_token=","编辑标签"),
    DELTAG("https://api.weixin.qq.com/cgi-bin/tags/delete?access_token=","删除标签"),
    GETTAGUSER("https://api.weixin.qq.com/cgi-bin/user/tag/get?access_token=","获取标签下粉丝列表"),
    SETUSERTYPE("https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?access_token=","批量为用户打标签"),
    DELUSERTYPE("https://api.weixin.qq.com/cgi-bin/tags/members/batchuntagging?access_token=","批量为用户取消标签"),
    GETUSERTYPE("https://api.weixin.qq.com/cgi-bin/tags/getidlist?access_token=","获取用户身上的标签列表"),
    GETWXUSER("https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=%s","获取用户的基本信息"),
    SETREMARK("https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token=","为用户设置备注"),
    GETTICKET("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=","临时二维码获取ticket")
    ;

    private String url;

    private String desc;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    private UrlEnum(String url,String desc){
        this.url=url;
        this.desc=desc;
    }
}
