package com.gjmetal.controller;

import com.gjmetal.pojo.*;
import com.gjmetal.service.WxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Package: com.gjmetal.controller
 * @Description:
 * @Author: liangtf
 * @Version: 1.0
 */
@Api(tags="标签 用户 二维码")
@RestController
public class WxUserController {

    @Autowired
    private WxService wxService;
    // 标签管理
    // 1. 创建标签

    @ApiOperation("创建标签")
    @ApiImplicitParam(name = "name",value = "标签名称",paramType = "query")
    @GetMapping("/createtag")
    public String createTag(@RequestParam String name){
        return wxService.createTag(name);
    }

    // 2. 获取公众号已创建的标签

    @ApiOperation("获取公众号已创建的标签")
    @GetMapping("/tags")
    public List<Tag> getTags(){
        return wxService.getTags();
    }

    // 3. 编辑标签

    @ApiOperation("编辑标签")
    @PostMapping("/edit")
    public String editTag(@RequestBody Tag tag){
        return wxService.editTag(tag);
    }

    // 4. 删除标签

    @ApiOperation("删除标签")
    @ApiImplicitParam(name="id",value = "标签id",paramType = "query")
    @GetMapping("/deltag")
    public String delTag(@RequestParam String id){
        return wxService.delTag(id);
    }

    // 5. 获取标签下粉丝列表

    @ApiOperation("获取标签下粉丝列表")
    @ApiImplicitParam(name="id",value = "标签id",paramType = "query")
    @GetMapping("/taguser")
    public TagUser getTagUser(@RequestParam String id){
        return wxService.getTagUser(id);
    }

    // 用户管理
    // 1. 批量为用户打标签

    @ApiOperation("批量为用户打标签")
    @PostMapping("/setusertype")
    public String setUserType(@RequestBody UserType userType){
        return wxService.setUserType(userType);
    }

    // 2. 批量为用户取消标签

    @ApiOperation("批量为用户取消标签")
    @PostMapping("/delusertype")
    public String delUserType(@RequestBody UserType userType){
        return wxService.delUserType(userType);
    }

    // 3. 获取用户身上的标签列表

    @ApiOperation("获取用户身上的标签列表")
    @ApiImplicitParam(name="openId",value = "微信用户openId",paramType = "query")
    @GetMapping("/getusertype")
    public List<String> getUserType(@RequestParam String openId){
        return wxService.getUserType(openId);
    }

    // 4.获取用户的基本信息

    @ApiOperation("获取用户的基本信息")
    @ApiImplicitParam(name = "openId",value = "微信用户openId",paramType = "query")
    @GetMapping("/getwxuser")
    public WxUser getWxUser(@RequestParam String openId){
        return wxService.getWxUser(openId);
    }
    // 4.1获取用户的基本信息集合

    @ApiOperation("获取用户的基本信息集合")
    @ApiImplicitParam(name = "id",value = "标签id",paramType = "query")
    @GetMapping("/getwxuserlist")
    public List<WxUser> getWxUserList(@RequestParam String id){return wxService.getWxUserList(id);}


    // 5.为用户设置备注

    @GetMapping("/setremark")
    public String setRemark(String openId,String remark){
        return wxService.setRemark(openId,remark);
    }

    // 1.临时二维码获取ticket

    @ApiOperation("临时二维码获取ticket")
    @ApiImplicitParam(name = "id",value = "企业/服务号标签id",paramType = "query")
    @GetMapping("/getticket")
    public String getTicket(@RequestParam String id){
        return wxService.getTicket(id);
    }




   /* ///////////// 微信用户及标签 ///////////////


    // 1.创建标签

    @RequestMapping("/company/createtag")
    public String createCoTag(@RequestParam String tagname){
        return wxService.createCoTag(tagname);
    }

    // 2.更改标签名称

    @RequestMapping("/company/edit")
    public String editTag(@RequestParam String tagid,@RequestParam String tagname){
        return wxService.editCoTag(tagid,tagname);
    }

    // 3.删除标签

    @RequestMapping("/company/deltag")
    public String deltag(@RequestParam String tagid){
        return wxService.delCotag(tagid);
    }

    // 4.获取标签成员

    @RequestMapping("/company/getcotypeuser")
    public List<CoUser> getCoTypeUser(@RequestParam String tagid){
        return wxService.getCoTypeUser(tagid);
    }

    // 5.增加标签成员

    @PostMapping("/company/addcouser")
    public String addCoUser(@RequestBody CoTypeUser coTypeUser){
        return wxService.addCoUser(coTypeUser);
    }

    // 6.删除标签成员

    @PostMapping("/company/delcotypeuser")
    public String delCoTypeUser(@RequestBody CoTypeUser coTypeUser){
        return wxService.delCoTypeUser(coTypeUser);
    }

    // 7.获取标签列表

    @GetMapping("/company/getcotags")
    public List<CoTag> getCoTagList(){
        return wxService.getCoTagList();
    }

    // 1.创建部门

    @GetMapping("/company/createparty")
    public String createParty(@RequestParam String name){
        return wxService.createParty(name);
    }
*/


}
