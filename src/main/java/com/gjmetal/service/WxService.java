package com.gjmetal.service;

import com.gjmetal.MsgEnum.MsgType;
import com.gjmetal.pojo.*;
import com.gjmetal.utils.WxUtil;
import com.gjmetal.utils.com.qq.weixin.mp.aes.AesException;
import com.gjmetal.utils.com.qq.weixin.mp.aes.WXBizMsgCrypt;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Package: com.gjmetal.service
 * @Description:
 * @Author: liangtf
 * @Version: 1.0
 */
@Service
public class WxService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private WxUtil wxUtil;

    private final Logger logger = LoggerFactory.getLogger(WxService.class);

    private static String TEXT_MESSAGE = "Hello,这里是国金测试~~~~~~~~~";
    /**
     * TOKEN：
     */
    @Value("${WX.TOKEN}")
    private String TOKEN;

    /**
     * 二维码过期时间
     */
    @Value("${WX.EXPIRESECONDS}")
    private String expire_seconds;


    // 微信企业号token

    @Value("${WX.CORPID}")
    private String CORPID;

    @Value("${WX.COTOKEN}")
    private String COTOKEN;

    @Value("${WX.EncodingAESKey}")
    private String EncodingAESKey;


    // 微信服务号

    /***
     *
     * 模板批量发送给用户
     */
    public String sendWechatMsgToUser(MessagePush messagePush) {
        StringBuffer sb = new StringBuffer("");
        try {
            //  数据校验
            if (messagePush == null) {
                return "参数有误";
            }
            List<String> vIdList = messagePush.getvId();
            if (CollectionUtils.isEmpty(vIdList)) {
                return "用户openid有误";
            }
            for (String s : vIdList) {
                //获取AccessToken
                String accessToken = AccessToken();
                // 封装消息模板
                TemplateMessage message = new TemplateMessage();
                message.setTemplate_id(messagePush.getTemplateId());
                message.setTouser(s);
                message.setData(messagePush.getMap());

                //数据校验
                if (StringUtils.isBlank(accessToken) || StringUtils.isBlank(message.getTemplate_id()) || StringUtils.isBlank(message.getTouser())) {
                    continue;
                }
                String msg = wxUtil.sendWechatMsgToUser(message, accessToken);
                sb.append(msg);
            }
            return sb.toString();
        } catch (Exception e) {
            e.getStackTrace();
        }
        return "";
    }

    /***
     *
     * 模板批量发送给分组
     */
    public String sendTemplateTypeList(MessagePush messagePush) {
        // 数据校验
        if (messagePush == null) {
            return "参数有误";
        }
        List<String> list = messagePush.getTypeList();
        if (list == null || CollectionUtils.isEmpty(list)) {
            return "分组标签id错误";
        }
        List<Tag> tags = getTags();
        if (CollectionUtils.isEmpty(tags)) {
            return "暂未分组标签";
        }
        List<String> collect = tags.stream().map((tag) -> tag.getId()).collect(Collectors.toList());
        for (String s : list) {

            boolean contains = collect.contains(s);
            if (!contains) {
                // 分组id有误
                return "分组标签id错误";
            }
        }

        // 获取标签下的所有openId存Set后调用批量发送模板接口
        Set<String> set = new HashSet<>();
        for (String s : list) {
            List<String> openids = getTagUser(s).getData().get("openid");
            for (String openid : openids) {
                set.add(openid);
            }
        }
        List<String> vIdList = new ArrayList<>(set);
        messagePush.setvId(vIdList);
        return sendWechatMsgToUser(messagePush);
    }


    /***
     * 分组发送给用户普通消息
     * @return
     */
    public String sendWechatMsg(Message message) {
        // 数据校验
        if (message == null || message.getText() == null || message.getMsgtype() == null || message.getFilter() == null) {
            return "数据有误";
        }
        return wxUtil.sendWechatMsg(message, AccessToken());
    }


    /**
     * OpendId组(要求至少两个)发送给用户普通消息
     *
     * @param messageOpenId
     * @return
     */
    public String sendWechatMsg(MessageOpenId messageOpenId) {
        // 数据校验
        if (messageOpenId == null || messageOpenId.getTouser() == null || messageOpenId.getMsgtype() == null || messageOpenId.getText() == null) {
            return "数据有误";
        }
        List<String> touser = messageOpenId.getTouser();
        if (CollectionUtils.isEmpty(touser) || touser.size() < 2) {
            return "用户openId至少两个";
        }
        return wxUtil.sendWechatMsg(messageOpenId, AccessToken());
    }


    /**
     * 验证服务器
     *
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    public String signature(String signature, String timestamp,
                            String nonce, String echostr) {
        String[] strings = new String[]{TOKEN, timestamp, nonce};
        StringBuilder builder = new StringBuilder();
        Arrays.sort(strings);
        for (int i = 0; i < strings.length; i++) {
            builder.append(strings[i]);
        }
        String res = sha1(builder.toString());

        if (signature.equalsIgnoreCase(res)) {
            return echostr;
        }
        return "";
    }

    private String sha1(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = digest.digest(str.getBytes());
            return toHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String toHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            char[] chars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            char[] temp = new char[2];
            temp[0] = chars[(b >>> 4) & 0x0F];
            temp[1] = chars[b & 0x0F];
            stringBuilder.append(temp);
        }
        return stringBuilder.toString();
    }

    /**
     * 解析xml测试
     */
    public String parseWXXml(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();

        String testMessage = "";
        try {
            map = wxUtil.parseXml(request);
          /* for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                System.out.println(key+"  "+value);
            }*/
            if (map.isEmpty()) {
                return "微信返回xml解析出错";
            }
            if (MsgType.EVENT.toString().equalsIgnoreCase(map.get("MsgType"))) {
                // 关注时判断Ticket EventKey
                String ticket = map.get("Ticket");
                String eventKey = map.get("EventKey");
                if (ticket != null && eventKey != null) {
                    //将用户添加到对应的标签分组
                    String id = eventKey.substring(eventKey.lastIndexOf("_") + 1);
                    // 调用服务号
                    List<Tag> tags = getTags();
                    List<String> collect = tags.stream().map((t) -> t.getId()).collect(Collectors.toList());
                    if (collect.contains(id)) {
                        UserType userType = new UserType();
                        userType.setTagid(id);
                        userType.setOpenid_list(Arrays.asList(map.get("FromUserName")));
                        String result = setUserType(userType);
                        if (result == null) {
                            // 关注时
                            logger.error("关注时出错");
                        }
                    }
                }
                return wxUtil.buildTextMessage(map, TEXT_MESSAGE);
            }
            testMessage = wxUtil.buildTextMessage(map, "测试,正在努力开发中.........");
        } catch (Exception e) {
            e.getStackTrace();
        }
        return testMessage;
    }

    /**
     * 保存token到redis，返回token
     */
    public String AccessToken() {
        try {
            // redis获取token
            String accessToken = redisTemplate.opsForValue().get("AccessToken");
            if (accessToken != null) {
                return accessToken;
            } else {
                // redis无则重新获取放到redis
                String token = wxUtil.getAllAccessToken();
                redisTemplate.opsForValue().set("AccessToken", token, 7140L, TimeUnit.SECONDS);
                return token;
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        return "";
    }

    //////////////// 用户及标签  //////////////////////////

    // 标签管理
    // 1. 创建标签 (一个公众号，最多可以创建100个标签。)

    public String createTag(String name) {

        //  标签名称校验 	标签名（30个字符以内）
        if (StringUtils.isBlank(name) || name.length() > 30 || name.trim().length() <= 0) {
            return "标签名30个非空字符以内";
        }
        //  校验名称是否存在
        List<Tag> tags = getTags();
        if (!CollectionUtils.isEmpty(tags)) {
            for (Tag tagtemp : tags) {
                if (StringUtils.equals(tagtemp.getName(), name.trim())) {
                    return "标签已存在";
                }
            }
        }
        Tag t = new Tag();
        t.setName(name.trim());
        Map<String, Tag> map = new HashMap<>();
        map.put("tag", t);
        return wxUtil.createTag(map, AccessToken());
    }

    // 2. 获取公众号已创建的标签

    public List<Tag> getTags() {
        return wxUtil.getTags(AccessToken());
    }
    // 3. 编辑标签

    public String editTag(Tag tag) {

        // 数据校验
        if (tag == null || tag.getId() == null || tag.getName() == null || tag.getName().length() > 30 || tag.getName().trim().length() <= 0) {
            return "参数有误";
        }

        List<Tag> tags = getTags();
        if (CollectionUtils.isEmpty(tags)) {
            return "暂无标签分组";
        }
        List<String> collect = tags.stream().map((t) -> t.getId()).collect(Collectors.toList());
        if (!collect.contains(tag.getId())) {
            return "标签id名有误";
        }
        List<String> nameList = tags.stream().map((t) -> t.getName()).collect(Collectors.toList());
        if (nameList.contains(tag.getName().trim())) {
            return "标签分组名称已存在";
        }
        Map<String, Tag> map = new HashMap<>();
        tag.setName(tag.getName().trim());
        map.put("tag", tag);
        return wxUtil.editTag(map, AccessToken());
    }

    // 4. 删除标签

    public String delTag(String id) {

        // 数据校验
        List<Tag> tags = getTags();
        if (CollectionUtils.isEmpty(tags)) {
            return "暂无标签";
        }
        List<String> collect = tags.stream().map((t) -> t.getId()).collect(Collectors.toList());
        if (!collect.contains(id)) {
            return "标签id名有误";
        }
        Tag tag = new Tag();
        tag.setId(id);
        return wxUtil.delTag(tag, AccessToken());
    }


    // 5. 获取标签下粉丝列表(另一个参数String next_openid不填默认从头开始拉取 )

    public TagUser getTagUser(String id) {

        // 数据校验
        List<Tag> tags = getTags();
        if (CollectionUtils.isEmpty(tags)) {
            return null;
        }
        List<String> collect = tags.stream().map((t) -> t.getId()).collect(Collectors.toList());
        if (!collect.contains(id)) {
            return null;
        }

        Tag tag = new Tag();
        tag.setId(id);
        Map<String, String> map = new HashMap<>();
        map.put("tagid", id);
        return wxUtil.getTagUser(map, AccessToken());
    }
    // 用户管理
    // 1. 批量为用户打标签

    public String setUserType(UserType userType) {
        // 数据校验
        if (userType == null || userType.getTagid() == null || userType.getOpenid_list() == null || userType.getOpenid_list().size() <= 0) {
            return "参数有误";
        }
        List<Tag> tags = getTags();
        List<String> collect = tags.stream().map((tag) -> tag.getId()).collect(Collectors.toList());
        if (!collect.contains(userType.getTagid())) {
            return "分组标签id有误";
        }
        return wxUtil.setUserType(userType, AccessToken());
    }

    // 2. 批量为用户取消标签

    public String delUserType(UserType userType) {
        // 数据校验
        if (userType == null || userType.getTagid() == null || userType.getTagid() == null) {
            return "参数有误";
        }
        List<Tag> tags = getTags();
        List<String> collect = tags.stream().map((tag) -> tag.getId()).collect(Collectors.toList());
        if (!collect.contains(userType.getTagid())) {
            return "分组标签id有误";
        }
        return wxUtil.delUserType(userType, AccessToken());
    }

    // 3. 获取用户身上的标签列表

    public List<String> getUserType(String openId) {
        Map<String, String> map = new HashMap<>();
        map.put("openid", openId);
        return wxUtil.getUserType(map, AccessToken());
    }
    // 4.获取用户的基本信息

    public WxUser getWxUser(String openId) {
        return wxUtil.getWxUser(openId, AccessToken());
    }

    public List<WxUser> getWxUserList(String id) {
        List<WxUser> list = new ArrayList<>();
        TagUser tagUser = getTagUser(id);
        Map<String, List<String>> data = tagUser.getData();
        List<String> openids = data.get("openid");
        for (String openid : openids) {
            WxUser wxUser = getWxUser(openid);
            if (wxUser != null) {
                list.add(wxUser);
            }
        }
        return list;
    }
    // 5.为用户设置备注

    public String setRemark(String openId, String remark) {

        if (openId == null || remark == null || remark.length() > 30 || remark.trim().length() <= 0) {
            return "参数有误";
        }
        Map<String, String> map = new HashMap<>();
        map.put("openid", openId);
        map.put("remark", remark.trim());
        return wxUtil.setRemark(map, AccessToken());
    }


    ////////生成二维码加入分组标签/////

    // 1.临时二维码获取ticket

    public String getTicket(String id) {
        // 数据校验
        List<Tag> tags = getTags();
        if (CollectionUtils.isEmpty(tags)) {
            return "暂无标签";
        }
        List<String> collect = tags.stream().map((t) -> t.getId()).collect(Collectors.toList());
        if (!collect.contains(id)) {
            return "标签id名有误";
        }
        String encodeTicket = "";

        try {
            // 从redis中取ticket
            String ticketTemp = redisTemplate.opsForValue().get("Ticket::" + id);
            if (ticketTemp != null) {
                encodeTicket = URLEncoder.encode(ticketTemp.replace("\"", ""), "UTF-8");
                return encodeTicket;
            } else {
                Map<String, String> innerMap = new HashMap<>();
                innerMap.put("scene_str", id);
                Map<String, Map<String, String>> outMap = new HashMap<>();
                outMap.put("scene", innerMap);
                Ticket ticket = new Ticket(expire_seconds, "QR_STR_SCENE", outMap);
                ticketTemp = wxUtil.getTicket(ticket, AccessToken());
                if (ticketTemp != null) {
                    // urlEncode
                    encodeTicket = URLEncoder.encode(ticketTemp.replace("\"", ""), "UTF-8");
                    try {
                        if (encodeTicket != null) {
                            // 将ticket存到redis
                            redisTemplate.opsForValue().set("Ticket::" + id, encodeTicket, 25, TimeUnit.DAYS);
                        }
                    } catch (Exception e) {
                        e.getStackTrace();
                        logger.error("ticket存入redis失败");
                    }
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

        return encodeTicket;
    }

    // 2.用ticket换取二维码 (前段发送根据上面的ticket发送ajax)


    // 微信企业号

    /**
     * 验证微信企业号
     *
     * @param msg_signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    public String signatureCompany(String msg_signature, String timestamp, String nonce, String echostr) {

        try {
            WXBizMsgCrypt wxBizMsgCrypt = new WXBizMsgCrypt(COTOKEN, EncodingAESKey, CORPID);
            String msg = wxBizMsgCrypt.VerifyURL(msg_signature, timestamp, nonce, echostr);
            System.out.println("msg = " + msg);
            return msg;
        } catch (AesException e) {
            e.printStackTrace();
        }

        return null;
    }

    /***
     * 解析xml
     * @param request
     * @return
     */
    public String parseCoWxXml(HttpServletRequest request, String msg_signature, String timestamp, String nonce) {
        Map<String, String> map = new HashMap<>();
        try {
            // 解析消息
            ServletInputStream inputStream = request.getInputStream();
            String postData = IOUtils.toString(inputStream, "UTF-8");
            WXBizMsgCrypt wxBizMsgCrypt = new WXBizMsgCrypt(COTOKEN, EncodingAESKey, CORPID);
            String msg = wxBizMsgCrypt.DecryptMsg(msg_signature, timestamp, nonce, postData);
            map = wxUtil.parseXml(msg);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                System.out.println(key + "  " + value);
            }
            // 当邀请时根据部门id加入对应的标签id
            String changeType = map.get("ChangeType");
            String department = map.get("Department");
            //邀请时 changType 为create_user 且不是根目录邀请时
            if (StringUtils.endsWithIgnoreCase("create_user", changeType) && !StringUtils.endsWithIgnoreCase("1", department)) {
                CoTypeUser coTypeUser = new CoTypeUser();
                int tagid = Integer.valueOf(department).intValue() - 1;
                coTypeUser.setTagid(tagid + "");
                coTypeUser.setUserlist(Arrays.asList(map.get("UserID")));
                addCoUser(coTypeUser);
            }
            // 被动回复
            // 明文
            /*
            String message = wxUtil.bu ildTextCoMessage(map, "测试,努力开发中.....");
            System.out.println("message = " + message);
            return message;*/

            // 加密
            String msg1 = wxBizMsgCrypt.EncryptMsg("欢迎关注", timestamp, nonce);
            System.out.println("msg1 = " + msg1);
            return msg1;

        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }

    /**
     * 保存token到redis，返回token
     */
    public String coAccessToken() {
        try {
            // redis获取token
            String accessToken = redisTemplate.opsForValue().get("coAccessToken");
            if (accessToken != null) {
                return accessToken;
            } else {
                // redis无则重新获取放到redis
                String token = wxUtil.getAllCoAccessToken();
                redisTemplate.opsForValue().set("coAccessToken", token, 7140L, TimeUnit.SECONDS);
                return token;
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        return "";
    }

    /**
     * 保存token到redis，返回token
     */
    public String coApiAccessToken() {
        try {
            // redis获取token
            String accessToken = redisTemplate.opsForValue().get("coApiAccessToken");
            if (accessToken != null) {
                return accessToken;
            } else {
                // redis无则重新获取放到redis
                String token = wxUtil.getAllCoApiAccessToken();
                redisTemplate.opsForValue().set("coApiAccessToken", token, 7140L, TimeUnit.SECONDS);
                return token;
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        return "";
    }

    /***
     * 发送应用信息
     * @param coMessagePush
     * @return
     */
    public String sendCoMessagePush(CoMessagePush coMessagePush) {
        // 数据校验
        coMessagePush.setMsgtype("text");
        coMessagePush.setAgentid("1000002");
        return wxUtil.sendCoMessagePush(coMessagePush, coAccessToken());
    }


    //////////////// 企业用户及标签  //////////////////////////

    // 1.创建标签（创建的标签属于该应用，只有该应用才可以增删成员）对通讯录Api

    public String createCoTag(String tagname) {
        // tagid 采用自增

        Map<String, String> map = new HashMap<>();
        map.put("tagname", tagname);
        // 数据校验
        return wxUtil.createCoTag(map, coAccessToken());
    }

    // 2.更改标签名称

    public String editCoTag(String tagid, String tagname) {

        // 数据校验

        Map<String, String> map = new HashMap<>();
        map.put("tagid", tagid);
        map.put("tagname", tagname);
        return wxUtil.editCoTag(map, coAccessToken());
    }

    // 3.删除标签

    public String delCotag(String tagid) {
        // 数据校验

        return wxUtil.delCoTag(tagid, coAccessToken());
    }

    // 4.获取标签成员,但返回列表仅包含应用可见范围的成员

    public List<CoUser> getCoTypeUser(String tagid) {

        // 数据校验

        return wxUtil.getCoTypeUser(tagid, coAccessToken());
    }

    // 5.增加标签成员

    public String addCoUser(CoTypeUser coTypeUser) {
        // 数据校验
        return wxUtil.addCoUser(coTypeUser, coAccessToken());
    }

    // 6.删除标签成员

    public String delCoTypeUser(CoTypeUser coTypeUser) {
        // 数据校验

        return wxUtil.delCoTypeUser(coTypeUser, coAccessToken());
    }
    // 7.获取标签列表

    public List<CoTag> getCoTagList() {
        return wxUtil.getCoTagList(coAccessToken());
    }
    // 1.创建部门列表 同时创建对应的标签

    public String createParty(String name) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("parentid", "1");
        createCoTag(name);
        return wxUtil.createParty(map, coApiAccessToken());

    }


}
