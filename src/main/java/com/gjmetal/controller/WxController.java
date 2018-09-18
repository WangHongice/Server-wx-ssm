package com.gjmetal.controller;

import com.gjmetal.pojo.CoMessagePush;
import com.gjmetal.pojo.Message;
import com.gjmetal.pojo.MessageOpenId;
import com.gjmetal.pojo.MessagePush;
import com.gjmetal.service.WxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Package: com.gjmetal.controller
 * @Description:
 * @Author: liangtf
 * @Version: 1.0
 */
@Api(tags = "发送模板,普通消息. 微信认证.接受回复(使用微信公众号测试接口,这不提供测试)")
@RestController
@RequestMapping("/signature")
public class WxController {
    @Autowired
    private WxService wxService;

    // 微信服务号

    /***
     * 认证
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @GetMapping
    public String signature(@RequestParam String signature, @RequestParam String timestamp,
                            @RequestParam String nonce, @RequestParam String echostr) {
        return wxService.signature(signature,timestamp,nonce,echostr);
    }

    /***
     * 微信发送post消息接口
     */
    @PostMapping
    public String parseWXXml(HttpServletRequest request){
        return wxService.parseWXXml(request);
    }

    /**
     * 其他服务调用
     * 发送模板消息
     * 批量人员
     * @param messagePush
     * @return
     */
    @ApiOperation("批量人员发送模板消息")
    @PostMapping(value = "/messagepush")
    public String templateToUser(@RequestBody MessagePush messagePush){
        return wxService.sendWechatMsgToUser(messagePush);
    }

    /***
     * 其他服务调用
     * 发送模板消息
     * 批量分组
     * @param messagePush
     * @return
     */
    @ApiOperation("批量分组发送模板消息")
    @PostMapping("/messagetypelist")
    public String templateTypeList(@RequestBody MessagePush messagePush){
        return wxService.sendTemplateTypeList(messagePush);
    }


    /***
     * 其他服务调用
     * 分组发送普通消息
     * @param message
     * @return
     */
    @ApiOperation("分组发送普通消息")
    @PostMapping("/typemessage")
    public String typeToUser(@RequestBody Message message){
        return wxService.sendWechatMsg(message);
    }

    /***
     * 其他服务调用
     * openId组发送普通消息
     * @param messageOpenId
     * @return
     */
    @ApiOperation("openId组发送普通消息")
    @PostMapping("/openidmessage")
    public String openIdToUser(@RequestBody MessageOpenId messageOpenId){
        return wxService.sendWechatMsg(messageOpenId);
    }



    // 微信企业号

    // 认证

    @GetMapping("/company")
    public String signatureCompany(@RequestParam String msg_signature,@RequestParam String timestamp,@RequestParam String nonce,@RequestParam String echostr){
        return wxService.signatureCompany(msg_signature,timestamp,nonce,echostr);
    }

    /***
     *
     */





    /**
     * 企业号应用发送post消息接口
     * @param request
     * @return
     */
    @PostMapping("/company")
    public String parseCoWxXml(HttpServletRequest request,@RequestParam String msg_signature,@RequestParam String timestamp,@RequestParam String nonce){
        return wxService.parseCoWxXml(request,msg_signature,timestamp,nonce);
    }


    /***
     * 企业号推送信息
     * @param coMessagePush
     * @return
     */
    @PostMapping("/sendCoMessagePush")
    public String sendCoMessagePush(@RequestBody CoMessagePush coMessagePush){
        return wxService.sendCoMessagePush(coMessagePush);
    }




}
