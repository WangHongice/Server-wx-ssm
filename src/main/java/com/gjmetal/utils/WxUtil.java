package com.gjmetal.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.gjmetal.MsgEnum.UrlEnum;
import com.gjmetal.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Package: com.gjmetal.utils
 * @Description:
 * @Author: liangtf
 * @Version: 1.0
 */
@Component
public class WxUtil {
    /**
     * 微信Appid
     */
    @Value("${WX.APPID}")
    private  String APPID;
    /**
     * 微信Appseceret
     */
    @Value("${WX.APPSECERET}")
    private  String  APPSECERET;

    /**
     * 微信获取用户信息返回国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语
     * 默认zh_CN
     */
    @Value("${WX.LANG}")
    private String LANG;


    // 企业号

    /**
     * 企业号id
     */
    @Value("${WX.CORPID}")
    private String CORPID;

    /**
     * 企业号应用SECRECT
     */
    @Value("${WX.SECRECT}")
    private String SECRECT;

    @Value("${WX.APISECRECT}")
    private String APISECRECT;

    private final Logger logger= LoggerFactory.getLogger(WxUtil.class);


    /***
     * 获取Access_token
     * @return
     */
    public  String  getAllAccessToken() {
        String url = String.format(UrlEnum.ACCESSTOKEN.getUrl(), APPID, APPSECERET);
        String message = httpClient(url, null);
        Map<String,String> resultMap = JSONObject.parseObject(message, Map.class);
        if (!resultMap.isEmpty() && resultMap.get("access_token") != null) {
            return resultMap.get("access_token");
        } else {
            return null;
        }
    }

    /***
     * 根据模板ID
     * 发送消息到微信服务器
     */
    public String sendWechatMsgToUser(TemplateMessage templateMessage, String AccessToken) {
        String url = UrlEnum.TEMPLATEMSGTOUSER.getUrl() + AccessToken;
        String message = httpClient(url, templateMessage);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        if (!resultMap.isEmpty() &&  resultMap.get("errcode")!=null&&(Integer)resultMap.get("errcode") == 0) {
            // 推送成功
            return JSONObject.toJSONString(resultMap.get("errmsg"));
        } else {
            // 给用户推送消息异常，失败原因
            logger.error("给用户推送消息异常，失败原因"+ resultMap.get("errmsg")+resultMap.get("errcode"));
        }

        return null;
    }

    /***
     * 无模板ID
     * 分组发送消息
     */
    public String sendWechatMsg(Message noTemplateMessage, String AccessToken) {
        String url = UrlEnum.NOTEMPLATEMSGTYPE.getUrl() + AccessToken;
        String message = httpClient(url, noTemplateMessage);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        //   System.out.println(JSONObject.toJSON(message));

        if (!resultMap.isEmpty() &&  resultMap.get("errcode")!=null&&(Integer)resultMap.get("errcode") == 0) {
            // 推送成功
            return JSONObject.toJSONString(resultMap.get("errmsg"));
        } else {
            // 给用户推送消息异常，失败原因
            logger.error("给用户推送消息异常，失败原因： "+ resultMap.get("errmsg")+resultMap.get("errcode"));
        }

        return null;
    }

    /***
     * 无模板ID
     * openId发送消息
     */
    public String sendWechatMsg(MessageOpenId messageOpenId,String AccessToken){
        String url=UrlEnum.NOTEMPLATEMSGOPENID.getUrl()+AccessToken;
        String message = httpClient(url, messageOpenId);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        //   System.out.println(JSONObject.toJSON(message));

        if (!resultMap.isEmpty() &&  resultMap.get("errcode")!=null&&(Integer)resultMap.get("errcode") == 0) {
            // 推送成功
            return JSONObject.toJSONString(resultMap.get("errmsg"));
        } else {
            // 给用户推送消息异常，失败原因
            logger.error("给用户推送消息异常，失败原因： "+ resultMap.get("errmsg")+resultMap.get("errcode"));
        }

        return null;
    }

    //////////////// 用户及标签  //////////////////////////

    // 标签管理
    // 1. 创建标签 (一个公众号，最多可以创建100个标签。)

    public String createTag(Map<String,Tag> map, String AccessToken){
        String url = UrlEnum.CREATETAG.getUrl()+ AccessToken;
        String message = httpClient(url, map);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        if (!resultMap.isEmpty() &&  resultMap.get("tag")!=null) {

            return  JSONObject.toJSONString(resultMap.get("tag"));
        } else {
            // TODO 失败原因
            logger.error("创建标签异常，失败原因： "+ resultMap.get("errmsg")+resultMap.get("errcode"));
        }
        return null;
    }

    // 2. 获取公众号已创建的标签

    public List<Tag> getTags(String AccessToken){
        String url = UrlEnum.GETTAGS.getUrl() + AccessToken;
        String message = httpClient(url, null);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        if(!resultMap.isEmpty()&&resultMap.get("tags")!=null){
            String tags = JSONObject.toJSONString(resultMap.get("tags"));
            List<Tag> tagList = JSON.parseObject(tags, new TypeReference<List<Tag>>() {
            });
            return tagList;
        }
        return null;
    }

    // 3. 编辑标签

    public String editTag(Map<String,Tag> map, String AccessToken){
        String url = UrlEnum.EDITTAG.getUrl() + AccessToken;
        String message = httpClient(url, map);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        if (!resultMap.isEmpty() && resultMap.get("errmsg")!= null &&(Integer)resultMap.get("errcode") == 0) {
            return  JSONObject.toJSONString(resultMap.get("errmsg"));
        } else {
            // TODO 失败原因
            logger.error("编辑标签异常，失败原因： "+ resultMap.get("errmsg")+resultMap.get("errcode"));
        }
        return null;
    }


    // 4. 删除标签

    public String delTag(Tag tag, String AccessToken){
        String url = UrlEnum.DELTAG.getUrl()+ AccessToken;
        String message = httpClient(url, tag);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        if (!resultMap.isEmpty() && resultMap.get("errmsg")!= null &&(Integer)resultMap.get("errcode") == 0) {
            return  JSONObject.toJSONString(resultMap.get("errmsg"));
        } else {
            logger.error("删除标签异常，失败原因： "+ resultMap.get("errmsg")+resultMap.get("errcode"));
        }
        return null;
    }

    // 5. 获取标签下粉丝列表

    public TagUser getTagUser(Map<String,String> map, String AccessToken){
        String url =UrlEnum.GETTAGUSER.getUrl() + AccessToken;
        String message = httpClient(url, map);
        TagUser tagUser = JSONObject.parseObject(message, TagUser.class);
        if(tagUser!=null){
            return tagUser;
        } else {
            logger.error("获取标签下用户异常");
            return null;
        }

    }


    // 用户管理
    // 1. 批量为用户打标签

    public String setUserType(UserType userType, String AccessToken) {
        String url = UrlEnum.SETUSERTYPE.getUrl() + AccessToken;
        String message = httpClient(url, userType);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        //   System.out.println(JSONObject.toJSON(message));

        if (!resultMap.isEmpty() &&  resultMap.get("errcode")!=null&&(Integer)resultMap.get("errcode") == 0) {

            return JSONObject.toJSONString(resultMap.get("errmsg"));
        } else {
            // 给用户推送消息异常，失败原因
            logger.error("给用户打标签异常，失败原因： "+ resultMap.get("errmsg")+resultMap.get("errcode"));
        }

        return null;
    }

    // 2. 批量为用户取消标签

    public String delUserType(UserType userType, String AccessToken) {
        String url = UrlEnum.DELUSERTYPE.getUrl() + AccessToken;
        String message = httpClient(url, userType);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        //   System.out.println(JSONObject.toJSON(message));

        if (!resultMap.isEmpty() &&  resultMap.get("errcode")!=null&&(Integer)resultMap.get("errcode") == 0) {

            return JSONObject.toJSONString(resultMap.get("errmsg"));
        } else {
            // 给用户打标签异常，失败原因
            logger.error("给用户打标签异常，失败原因： "+ resultMap.get("errmsg")+resultMap.get("errcode"));
        }

        return null;
    }

    // 3. 获取用户身上的标签列表

    public List<String> getUserType(Map<String,String> map,String AccessToken){

        String url = UrlEnum.GETUSERTYPE.getUrl() + AccessToken;
        String message = httpClient(url, map);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        if(!resultMap.isEmpty()&&resultMap.get("tagid_list")!=null){
            String tagid_list = JSONObject.toJSONString(resultMap.get("tagid_list"));
            List<String> tagIdList = JSON.parseObject(tagid_list, new TypeReference<List<String>>() {
            });
            return tagIdList;
        }else {
            // TODO 失败原因
            logger.error("编辑标签异常，失败原因： "+ resultMap.get("errmsg"));
        }
        return null;
    }


    // 4.获取用户的基本信息

    public WxUser getWxUser(String openId,String AccessToken){
        if(StringUtils.isBlank(LANG)){
            LANG="zh_CN";
        }
        // String url="https://api.weixin.qq.com/cgi-bin/user/info?access_token="+AccessToken+"&openid="+openId+"&lang="+LANG;
        String url=String.format(UrlEnum.GETWXUSER.getUrl(),AccessToken,openId,LANG);
        String message = httpClient(url, null);
        WxUser wxUser = JSONObject.parseObject(message, WxUser.class);
        if(wxUser!=null){
            return wxUser;
        }
        return null;
    }

    // 5.为用户设置备注

    public String setRemark(Map<String,String> map,String AccessToken){
        String url=UrlEnum.SETREMARK.getUrl()+AccessToken;
        String message = httpClient(url, map);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        if (!resultMap.isEmpty() &&  resultMap.get("errcode")!=null&&(Integer)resultMap.get("errcode") == 0) {

            return JSONObject.toJSONString(resultMap.get("errmsg"));
        } else {
            logger.error("给用户设置备注异常，失败原因： "+ resultMap.get("errmsg")+resultMap.get("errcode"));
        }
        return null;

    }

    // 1.临时二维码获取ticket

    public String getTicket(Ticket ticket,String AccessToken){
        String url=UrlEnum.GETTICKET.getUrl()+AccessToken;
        String message = httpClient(url, ticket);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        if(resultMap!=null&&resultMap.get("ticket")!=null){
            return JSONObject.toJSONString(resultMap.get("ticket"));
        }
        return null;
    }


    // 微信企业号



    /***
     * 获取Access_token
     * @return
     */
    public  String  getAllCoAccessToken() {
        String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s", CORPID, SECRECT);
        String message = httpClient(url, null);

        Map<String,String> resultMap = JSONObject.parseObject(message, Map.class);

        // 企业号 errcode 为0时为成功标志
        String errcode = JSONObject.toJSONString(resultMap.get("errcode"));
        if (!resultMap.isEmpty() && resultMap.get("access_token") != null&&StringUtils.equals(errcode,"0")) {
            return resultMap.get("access_token");
        } else {
            logger.error("企业号获取token失败 错误码:"+resultMap.get("errcode")+"错误信息 :"+resultMap.get("errmsg"));
            return null;
        }
    }
    public  String  getAllCoApiAccessToken() {
        String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s", CORPID, APISECRECT);
        String message = httpClient(url, null);

        Map<String,String> resultMap = JSONObject.parseObject(message, Map.class);

        // 企业号 errcode 为0时为成功标志
        String errcode = JSONObject.toJSONString(resultMap.get("errcode"));
        if (!resultMap.isEmpty() && resultMap.get("access_token") != null&&StringUtils.equals(errcode,"0")) {
            return resultMap.get("access_token");
        } else {
            logger.error("企业号获取token失败 错误码:"+resultMap.get("errcode")+"错误信息 :"+resultMap.get("errmsg"));
            return null;
        }
    }

    /***
     * 发送应用信息
     * @param coMessagePush
     * @param coAccessToken
     * @return
     */
    public String sendCoMessagePush(CoMessagePush coMessagePush,String coAccessToken){
        String url="https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token="+coAccessToken;
        String message = httpClient(url, coMessagePush);
        Map<String,String> resultMap = JSONObject.parseObject(message, Map.class);
        String errcode = JSONObject.toJSONString(resultMap.get("errcode"));
        if (!resultMap.isEmpty()&&resultMap.get("errmsg")!=null&&StringUtils.equals(errcode,"0")){
            // 可能出现发送接收人无权限或不存在，发送仍然执行，但会返回无效的部分
            /*System.out.println(resultMap.get("invaliduser")+resultMap.get("invalidparty")+resultMap.get("invalidtag"));*/
            return resultMap.get("errmsg");
        }else{
            logger.error("发送应用消息失败 错误码:"+resultMap.get("errcode")+"错误信息"+resultMap.get("errmsg"));
            return null;
        }
    }

    // 1.创建标签

    public String createCoTag(Map<String,String> map,String coAccessToken){
        String url="https://qyapi.weixin.qq.com/cgi-bin/tag/create?access_token="+coAccessToken;
        String message = httpClient(url, map);
        System.out.println("message = " + message);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        String errcode = JSONObject.toJSONString(resultMap.get("errcode"));
        if(!resultMap.isEmpty()&&resultMap.get("errmsg")!=null&&resultMap.get("tagid")!=null&&StringUtils.equals(errcode,"0")){
            return JSONObject.toJSONString(resultMap.get("tagid"));
        }else {
            logger.error("创建标签失败 错误吗:"+ errcode +"错误信息"+resultMap.get("errmsg"));
            return null;
        }

    }

    // 2.更改标签名称

    public String editCoTag(Map<String,String> map,String coAccessToken){
        String url="https://qyapi.weixin.qq.com/cgi-bin/tag/update?access_token="+coAccessToken;
        String message = httpClient(url, map);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        String errcode = JSONObject.toJSONString(resultMap.get("errcode"));
        if(!resultMap.isEmpty()&&resultMap.get("errmsg")!=null&&StringUtils.equals(errcode,"0")){
            return JSONObject.toJSONString(resultMap.get("errmsg"));
        }else {
            logger.error("更改标签名称失败 错误吗:"+ errcode +"错误信息"+resultMap.get("errmsg"));
            return null;
        }
    }

    // 3.删除标签

    public String delCoTag(String tagid,String coAccessToken){
        String url=String.format("https://qyapi.weixin.qq.com/cgi-bin/tag/delete?access_token=%s&tagid=%s",coAccessToken,tagid);
        String message = httpClient(url, null);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        String errcode = JSONObject.toJSONString(resultMap.get("errcode"));
        if(!resultMap.isEmpty()&&resultMap.get("errmsg")!=null&&StringUtils.equals(errcode,"0")){
            return JSONObject.toJSONString(resultMap.get("errmsg"));
        }else {
            logger.error("更改标签名称失败 错误吗:"+ errcode +"错误信息"+resultMap.get("errmsg"));
            return null;
        }
    }

    // 4.获取标签成员

    public List<CoUser> getCoTypeUser(String tagid,String coAccessToken){
        String url=String.format("https://qyapi.weixin.qq.com/cgi-bin/tag/get?access_token=%s&tagid=%s",coAccessToken,tagid);
        String message = httpClient(url, null);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        String errcode = JSONObject.toJSONString(resultMap.get("errcode"));
        String coTypeUserlist=JSONObject.toJSONString(resultMap.get("userlist"));
        if(!resultMap.isEmpty()&&resultMap.get("errmsg")!=null&&StringUtils.equals(errcode,"0")){
            List<CoUser> coUsers = JSON.parseObject(coTypeUserlist, new TypeReference<List<CoUser>>() {
            });
            return coUsers;
        }else{
            logger.error("获取标签成员失败 错误吗:"+ errcode +"错误信息"+resultMap.get("errmsg"));
            return null;
        }
    }

    // 5.增加标签成员

    public String addCoUser(CoTypeUser coTypeUser,String coAccessToken){
        String url="https://qyapi.weixin.qq.com/cgi-bin/tag/addtagusers?access_token="+coAccessToken;
        String message = httpClient(url, coTypeUser);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        String errcode = JSONObject.toJSONString(resultMap.get("errcode"));
        if(!resultMap.isEmpty()&&resultMap.get("errmsg")!=null&&StringUtils.equals(errcode,"0")){
            return JSONObject.toJSONString(resultMap.get("errmsg"));
        }else{
            logger.error("增加标签成员失败 错误吗:"+ errcode +"错误信息"+resultMap.get("errmsg"));
            return null;
        }

    }

    // 6.删除标签成员

    public String delCoTypeUser(CoTypeUser coTypeUser,String coAccessToken){
        String url="https://qyapi.weixin.qq.com/cgi-bin/tag/deltagusers?access_token="+coAccessToken;
        String message = httpClient(url, coTypeUser);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        String errcode = JSONObject.toJSONString(resultMap.get("errcode"));
        if(!resultMap.isEmpty()&&resultMap.get("errmsg")!=null&&StringUtils.equals(errcode,"0")){
            return JSONObject.toJSONString(resultMap.get("errmsg"));
        }else{
            logger.error("删除标签成员失败 错误吗:"+ errcode +"错误信息"+resultMap.get("errmsg"));
            return null;
        }

    }
    // 7.获取标签列表

    public List<CoTag> getCoTagList(String coAccessToken){
        String url="https://qyapi.weixin.qq.com/cgi-bin/tag/list?access_token="+coAccessToken;
        String message = httpClient(url, null);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        String errcode = JSONObject.toJSONString(resultMap.get("errcode"));
        String taglist = JSONObject.toJSONString(resultMap.get("taglist"));
        if(!resultMap.isEmpty()&&resultMap.get("errmsg")!=null&&StringUtils.equals(errcode,"0")){
            List<CoTag> tagList = JSON.parseObject(taglist, new TypeReference<List<CoTag>>() {
            });
            return tagList;
        }else {
            logger.error("获取标签列表失败 错误吗:"+ errcode +"错误信息"+resultMap.get("errmsg"));
            return null;
        }
    }

    // 1.创建部门

    public String createParty(Map<String,String> map,String coAccessToken){
        String url="https://qyapi.weixin.qq.com/cgi-bin/department/create?access_token="+coAccessToken;
        String message = httpClient(url, map);
        Map<String,Object> resultMap = JSONObject.parseObject(message, Map.class);
        String errcode = JSONObject.toJSONString(resultMap.get("errcode"));
        if(!resultMap.isEmpty()&&resultMap.get("errmsg")!=null&&StringUtils.equals(errcode,"0")){
            return JSONObject.toJSONString(resultMap.get("errmsg"));
        }else {
            logger.error("创建部门失败 错误吗:"+ errcode +"错误信息"+resultMap.get("errmsg"));
            return null;
        }

    }







    /***
     * 封装httpclient
     * @param data
     * @return
     */
    public String httpClient(String url,Object data){

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpClient client=new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        if(data!=null){
            String jsonString = JSONObject.toJSONString(data);
            // 创建请求内容
            StringEntity entity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);

            post.setEntity(entity);
        }
        HttpResponse execute;
//      CloseableHttpResponse execute1;
        try {
//          execute1 = httpClient.execute(post);
            execute = client.execute(post);
        } catch (Exception e) {
            logger.error("与微信api接口连接失败");
            return null;
        }
        if (execute != null && execute.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
//          HttpEntity entity = execute1.getEntity();
            HttpEntity resEntity = execute.getEntity();
            String message = null;
//          String message1 = null;
            try {
              //  message1 = EntityUtils.toString(entity, "UTF-8");
                message = EntityUtils.toString(resEntity, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("返回消息转换异常");
            }
            // System.out.println("message1 = " + message1);
            return message;
        }
        return null;
    }

    /**
     * 解析xml
     * @param request
     * @return
     */
    public Map<String, String> parseXml(HttpServletRequest request){
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();
        // 从request中取得输入流
        InputStream inputStream =null;

        try{
            inputStream= request.getInputStream();
            // 读取输入流
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            // 得到xml根元素
            Element root = document.getRootElement();
            // 得到根元素的所有子节点
            List<Element> elementList = root.elements();

            // 遍历所有子节点
            for (Element e : elementList){
                map.put(e.getName(), e.getText());
            }
        }catch(Exception e){
            e.getStackTrace();
        }finally {
            // 释放资源
            try {
                if(inputStream!=null){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 解析xml
     * @param msg
     * @return
     */
    public Map<String, String> parseXml(String msg){
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();
        // 从request中取得输入流
        InputStream inputStream =null;

        try{
            inputStream= new ByteArrayInputStream(msg.getBytes("UTF-8"));//将要转换的'资源'换成流
            // 读取输入流
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            // 得到xml根元素
            Element root = document.getRootElement();
            // 得到根元素的所有子节点
            List<Element> elementList = root.elements();

            // 遍历所有子节点
            for (Element e : elementList){
                map.put(e.getName(), e.getText());
            }
        }catch(Exception e){
            e.getStackTrace();
        }finally {
            // 释放资源
            try {
                if(inputStream!=null){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
    /**
     * 构造文本消息
     * 微信公众号
     * @param map
     * @param content
     * @return
     */
    public String buildTextMessage(Map<String,String> map, String content) {

        return String.format( "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[text]]></MsgType>" +
                        "<Content><![CDATA[%s]]></Content>" + "</xml>",
                map.get("FromUserName"), map.get("ToUserName"), getUtcTime(), content);
    }

    private static String getUtcTime() {
        Date dt = new Date();// 如果不需要格式,可直接用dt,dt就是当前系统时间
        DateFormat df = new SimpleDateFormat("yyyyMMddhhmm");// 设置显示格式
        String nowTime = df.format(dt);
        long dd =  0L;
        try {
            dd = df.parse(nowTime).getTime();
        } catch (Exception e) {
            e.getStackTrace();
        }
        return String.valueOf(dd);
    }

    /**
     * 构造文本消息
     * 企业号 未使用
     */
    public String buildTextCoMessage(Map<String,String> map,String content){
        return String.format( "<xml>"+
                                "<ToUserName><![CDATA[%s]]></ToUserName>"+
                                "<FromUserName><![CDATA[%s]]></FromUserName>"+
                                "<CreateTime>%s</CreateTime>"+
                                "<MsgType><![CDATA[text]]></MsgType>"+
                                "<Content><![CDATA[%s]]></Content>"+ "</xml>",
                map.get("FromUserName"), map.get("ToUserName"), getUtcTime(), content
        );
    }

}
