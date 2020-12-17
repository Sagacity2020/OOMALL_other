package cn.edu.xmu.share.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;

import cn.edu.xmu.share.model.bo.Rule;
import cn.edu.xmu.share.model.bo.Stategy;
import cn.edu.xmu.share.model.vo.ShareActivityVo;
import cn.edu.xmu.share.service.ShareService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 分享控制器
 * @author zxh
 * Modified at 2020/12/1
 **/
@Api(value = "分享服务", tags = "share")
@RestController /*Restful的Controller对象*/
@RequestMapping(produces = "application/json;charset=UTF-8")
public class ShareController {
    private  static  final Logger logger = LoggerFactory.getLogger(ShareController.class);

    @Autowired
    private ShareService shareService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 未解决问题，不分页时如何弄，分享活动需要调用商品模块的api,返回时那个页数怎么弄
     * @author zxh
     * @date Created in 2020/12/1 10:44
     **/
    @Audit
    @ApiOperation(value = "买家查询所有分享记录",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer",  name = "skuId",      value ="SPU Id",    required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "beginTime",        value ="开始时间",  required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "endTime",        value ="结束时间",  required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pageSize",      value ="每页数目",  required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/shares")
    public Object getShares(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long sharerId,
            @RequestParam(required = false) Long  skuId,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pageSize) {
        LocalDateTime begin = null, end = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if(beginTime != null)
        {
            if(beginTime.matches("^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$"))
                begin = LocalDateTime.parse(beginTime, formatter);
            else
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID,"开始时间格式不正确"), httpServletResponse);
        }

        if(endTime != null)
        {
            if(endTime.matches("^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$"))
                end = LocalDateTime.parse(endTime, formatter);
            else
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID,"结束时间格式不正确"), httpServletResponse);
        }
        //如果不分页？？？？
        page = (page == null)?1:page;
        pageSize = (pageSize == null)?10:pageSize;
        ReturnObject<PageInfo<VoObject>> returnObject = shareService.getShares(sharerId, skuId, begin, end, page, pageSize);
        logger.debug("getshares: sharerId = " + sharerId);
        return Common.getPageRetObject(returnObject);
    }
    /**
     * 管理员查询所有分享记录
     * 未解决问题，不分页时如何弄，分享活动需要调用商品模块的api,返回时那个页数怎么弄
     * @author zxh
     * @date Created in 2020/12/1 10:44
     **/
    @Audit
    @ApiOperation(value = "管理员查询所有分享记录",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",  dataType = "Integer",  name = "did",        value ="店铺Id",  required = true),
            @ApiImplicitParam(paramType = "path",  dataType = "Integer",  name = "id",      value ="SKU Id",    required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pageSize",      value ="每页数目",  required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/shops/{did}/skus/{id}/shares")
    public Object getSharesAdmin(
            @PathVariable Long  did,
            @PathVariable Long  id,
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pageSize) {
        page = (page == null)?1:page;
        pageSize = (pageSize == null)?10:pageSize;
        ReturnObject<PageInfo<VoObject>> returnObject = shareService.getSharesAdmin(did, id, page, pageSize);
        logger.debug("getshares admin: shopId = " + id);
        return Common.getPageRetObject(returnObject);
    }

    /**
     * 发起分享
     * 未解决问题，对于分享链接怎么整
     * @author zxh
     * @date Created in 2020/12/1
     * @param id 商品skuId
     * @return  Object 分享链接
     */
    @ApiOperation(value="发起分享")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "商品skuId", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
   @Audit
   @PostMapping("skus/{id}/shares")
    public Object createShare(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long sharerId,
            @PathVariable Long id){
        logger.debug("create share by sharerId : sharerId = "+ sharerId );
        ReturnObject<VoObject> retObject=shareService.createShare(sharerId, id);
        if (retObject.getData() != null) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }

    /**
     * 浏览分享
     * 到底是被分享者id还是分享者id body
     * @author zxh
     * @date Created in 2020/12/1
     * @param vo:vo对象
     * @return  Object 分享链接
     */
    /*
    @ApiOperation(value="生成分享成功状态信息", produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path",  dataType = "Integer",  name = "spuId",      value ="SPU Id",    required = true),
            @ApiImplicitParam(paramType = "body", dataType = "BeShareVo", name = "vo", value = "新建分享成功信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
   // @Audit
    @PostMapping("/goods/{spuId}/beshared")
    public Object createBeShare(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long customerId,
            @RequestParam Long  spuId,
            @Validated @RequestBody BeShareVo vo, BindingResult result){
        logger.debug("create be share : customerId = "+ customerId +"  sharerId = "+ vo.getSharerId());
        if(result.hasErrors()){
            logger.debug("validate fail");
            return Common.processFieldErrors(result,httpServletResponse);
        }
        ReturnObject retObject=shareService.createBeShare(customerId, spuId, vo);
        if (retObject.getData() != null) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }
    */
    /**
     * 用户查询自己的分享成功记录
     * 未解决问题，不分页时如何弄，分享活动需要调用商品模块的api,返回时那个页数怎么弄
     * @author zxh
     * @date Created in 2020/12/1 10:44
     **/
    @Audit
    @ApiOperation(value = "买家查询所有分享记录",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer",  name = "skuId",      value ="SKU Id",    required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "beginTime",        value ="开始时间",  required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "endTime",        value ="结束时间",  required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pageSize",      value ="每页数目",  required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/beshared")
    public Object getBeShared(
            @LoginUser @ApiIgnore @RequestParam(required = false) Long sharerId,
            @RequestParam(required = false) Long  skuId,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pageSize) {
        LocalDateTime begin = null, end = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(beginTime != null)
        {
            if(beginTime.matches("^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$"))
                begin = LocalDateTime.parse(beginTime, formatter);
            else
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID,"开始时间格式不正确"), httpServletResponse);
        }

        if(endTime != null)
        {
            if(endTime.matches("^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$"))
                end = LocalDateTime.parse(endTime, formatter);
            else
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID,"结束时间格式不正确"), httpServletResponse);
        }
        //如果不分页？？？？
        page = (page == null)?1:page;
        pageSize = (pageSize == null)?10:pageSize;
        ReturnObject<PageInfo<VoObject>> returnObject = shareService.getBeShared(sharerId, skuId, begin, end, page, pageSize);
        logger.debug("getshares: sharerId = " + sharerId);
        return Common.getPageRetObject(returnObject);
    }

    /**
     * 管理员查询自己店铺的分享成功记录
     * 未解决问题，不分页时如何弄，分享活动需要调用商品模块的api,返回时那个页数怎么弄
     * @author zxh
     * @date Created in 2020/12/1 10:44
     **/
    @Audit
    @ApiOperation(value = "买家查询所有分享记录",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",  dataType = "Integer",  name = "did",        value ="店铺Id",  required = true),
            @ApiImplicitParam(paramType = "path",  dataType = "Integer",  name = "id",      value ="SKU Id",    required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "beginTime",        value ="开始时间",  required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "endTime",        value ="结束时间",  required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pageSize",      value ="每页数目",  required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/shops/{did}/skus/{id}/beshared")
    public Object getBeSharedAdmin(
            @PathVariable Long  did,
            @PathVariable Long  id,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pageSize) {
        LocalDateTime begin = null, end = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(beginTime != null)
        {
            if(beginTime.matches("^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$"))
                begin = LocalDateTime.parse(beginTime, formatter);
            else
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID,"开始时间格式不正确"), httpServletResponse);
        }

        if(endTime != null)
        {
            if(endTime.matches("^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$"))
                end = LocalDateTime.parse(endTime, formatter);
            else
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID,"结束时间格式不正确"), httpServletResponse);
        }
        //如果不分页？？？？
        page = (page == null)?1:page;
        pageSize = (pageSize == null)?10:pageSize;
        ReturnObject<PageInfo<VoObject>> returnObject = shareService.getBeSharedAdmin(did, id, begin, end, page, pageSize);
        logger.debug("getbeSharedAdmin: shopId = " + id);
        return Common.getPageRetObject(returnObject);
    }





    /**
     * 查询分享活动
     *
     * @author zxh
     * @date Created in 2020/12/2
     * @return  分享活动
     */
    @Audit
    @ApiOperation(value = "查询分享活动",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer",  name = "skuId",      value ="SKU Id",    required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer",  name = "shopId",        value ="店铺Id",  required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pageSize",      value ="每页数目",  required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/shareactivities")
    public Object getShareActivities(
            @RequestParam(required = false) Long  skuId,
            @RequestParam(required = false) Long  shopId,
            @RequestParam(required = false, defaultValue = "1")  Integer page,
            @RequestParam(required = false, defaultValue = "10")  Integer pageSize) {

        //如果不分页？？？？
        page = (page == null)?1:page;
        pageSize = (pageSize == null)?10:pageSize;
        ReturnObject<PageInfo<VoObject>> returnObject =  shareService.getShareActivities(skuId, shopId, page, pageSize);

        return Common.getPageRetObject(returnObject);
    }

    /**
     * 新建分享活动
     *
     * @author zxh
     * @date Created in 2020/12/2
     * @param shopId 商铺Id
     * @param id 商品skuId
     * @param vo:vo对象
     * @return  Object 分享活动
     */
    @ApiOperation(value="新建分享活动")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path",  dataType = "Integer",  name = "id",      value ="SKU Id",    required = true),
            @ApiImplicitParam(paramType = "path",  dataType = "Integer",  name = "shopId",        value ="店铺Id",  required = true),
            @ApiImplicitParam(paramType = "body", dataType = "ShareActivityVo", name = "vo", value = "新建分享活动信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("/shops/{shopId}/skus/{id}/shareactivities")
    public Object createShareActivity(
            //需要验证身份吗？shopId与token里的Id一致吗
            @PathVariable Long  shopId,
            @PathVariable Long  id,
            @Validated @RequestBody ShareActivityVo vo, BindingResult result){
        logger.debug("create shareActivity : shopId = "+ shopId +"  skuId = "+ id +" shareActivity = " + vo);
        Object returnObject = Common.processFieldErrors(result, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        if(vo.getStrategy().equals(""))
        {
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID,"分享规则不能为空"), httpServletResponse);
        }
        Stategy s = JacksonUtil.toObj(StringEscapeUtils.unescapeJava(vo.getStrategy()), Stategy.class);
        if(s.getFirstOrAvg() == null)
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID,"分享规则格式不正确"), httpServletResponse);
        List<Rule> rule = s.getRule();
        for(Rule r : rule)
        {
            if(r.getNum() == null || r.getRate() == null)
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID,"分享规则格式不正确"), httpServletResponse);
        }

        ReturnObject retObject=shareService.createShareActivity(shopId, id, vo);
        if (retObject.getData() != null) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            logger.debug("fail");
            return Common.getRetObject(retObject);
        } else {
            logger.debug("fail");
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }

    /**
     * 修改分享活动
     *
     * @author zxh
     * @date Created in 2020/12/2
     * @param shopId 商铺Id
     * @param id 分享活动Id
     * @param vo:vo对象
     * @return  Object 分享活动
     */
    @ApiOperation(value="修改分享活动", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path",  dataType = "Integer",  name = "id",      value ="分享活动Id",    required = true),
            @ApiImplicitParam(paramType = "path",  dataType = "Integer",  name = "shopId",        value ="店铺Id",  required = true),
            @ApiImplicitParam(paramType = "body", dataType = "ShareActivityVo", name = "vo", value = "新建分享活动信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 610, message = "开始时间大于结束时间")
    })
    @Audit
    @PutMapping("/shops/{shopId}/shareactivities/{id}")
    public Object putShareActivity(
            //需要验证身份吗？shopId与token里的Id一致吗
            @PathVariable("shopId") Long  shopId,
            @PathVariable("id") Long  id,
            @Validated @RequestBody ShareActivityVo vo, BindingResult result){
        logger.debug("put shareActivity : shopId = "+ shopId +"  shareActivity = "+ id);
        Object returnObject = Common.processFieldErrors(result, httpServletResponse);
        if (null != returnObject) {
            logger.debug("validate fail");
            return returnObject;
        }
        if(vo.getStrategy().equals(""))
        {
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID,"分享规则不能为空"), httpServletResponse);
        }
        Stategy s = JacksonUtil.toObj(StringEscapeUtils.unescapeJava(vo.getStrategy()), Stategy.class);
        if(s.getFirstOrAvg() == null)
            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID,"分享规则格式不正确"), httpServletResponse);
        List<Rule> rule = s.getRule();
        for(Rule r : rule)
        {
            if(r.getNum() == null || r.getRate() == null)
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID,"分享规则格式不正确"), httpServletResponse);
        }
        ReturnObject retObject=shareService.putShareActivity(shopId, id, vo);
        return Common.decorateReturnObject(retObject);
    }

    /**
     * 删除分享活动
     *
     * @author zxh
     * @date Created in 2020/12/2
     * @param shopId 商铺Id
     * @param id 分享活动Id
     * @return  Object 分享活动
     */
    @ApiOperation(value="删除分享活动", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path",  dataType = "Integer",  name = "id",      value ="分享活动Id",    required = true),
            @ApiImplicitParam(paramType = "path",  dataType = "Integer",  name = "shopId",  value ="店铺Id",  required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @DeleteMapping("shops/{shopId}/shareactivities/{id}")
    public Object deleteShareActivity(
            //需要验证身份吗？shopId与token里的Id一致吗
            @PathVariable("shopId") Long  shopId,
            @PathVariable("id") Long  id){
        logger.debug("delete shareActivity : shopId = "+ shopId +"  shareActivity = "+ id);

        ReturnObject retObject=shareService.deleteShareActivity(shopId, id);
        return Common.decorateReturnObject(retObject);
    }


    /**
     * 上线分享活动
     *
     * @author zxh
     * @date Created in 2020/12/2
     * @param shopId 商铺Id
     * @param id 分享活动Id
     * @return  Object 分享活动
     */
    @ApiOperation(value="修改分享活动状态", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path",  dataType = "Integer",  name = "id",      value ="分享活动Id",    required = true),
            @ApiImplicitParam(paramType = "path",  dataType = "Integer",  name = "shopId",        value ="店铺Id",  required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("/shops/{shopId}/shareactivities/{id}/online")
    public Object putShareActivityState(
            //需要验证身份吗？shopId与token里的Id一致吗
            @PathVariable("shopId") Long  shopId,
            @PathVariable("id") Long  id){
        if (logger.isDebugEnabled()) {
            logger.debug("put shareActivityState : shopId = "+ shopId +"  shareActivity = "+ id);
        }
        ReturnObject retObject=shareService.putShareActivityState(shopId, id);
        return Common.decorateReturnObject(retObject);
    }
}
