package cn.edu.xmu.aftersale.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.aftersale.mapper.AftersaleServicePoMapper;
import cn.edu.xmu.aftersale.model.bo.Aftersale;
import cn.edu.xmu.aftersale.model.po.AftersaleServicePo;
import cn.edu.xmu.aftersale.model.vo.*;
import cn.edu.xmu.aftersale.service.AftersaleService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "售后服务", tags = "Aftersale")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/aftersale", produces = "application/json;charset=UTF-8")
public class AftersaleController {
    private  static  final Logger logger = LoggerFactory.getLogger(AftersaleController.class);

    @Autowired
    private AftersaleService aftersaleService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private AftersaleServicePoMapper poMapper;


    /*
    修改售后信息
    江欣霖
     */
    @ApiOperation(value = "修改售后信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "售后单id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AftersaleVo", name = "vo", value = "可修改的售后信息", required = true)

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 404, message = "参数不合法")
    })
    @Audit // 需要认证
    @PutMapping("aftersales/{id}")
    public Object updateAftersale(@PathVariable Long id, @Validated @RequestBody AftersaleVo vo, BindingResult bindingResult,
                                  @LoginUser @ApiIgnore @RequestParam(required = false) Long userId) {
        if (logger.isDebugEnabled()) {
            logger.debug("updateAftersale: id = "+ id +" vo = " + vo);
        }

        // 校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (returnObject != null) {
            logger.info("incorrect data received while updateAftersale id = " + id);
            return returnObject;
        }

        ReturnObject returnObj=checkCustomerId(userId,id);
        AftersaleServicePo po=(AftersaleServicePo)returnObj.getData();
        if(po==null){
            return Common.decorateReturnObject(returnObj);
        }

        returnObj = aftersaleService.updateAftersale(id, vo);

        return Common.decorateReturnObject(returnObj);
    }

    /*
    提交售后申请
    江欣霖
     */

    @ApiOperation(value = "买家提交售后单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "订单明细id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "CreateAftersaleVo", name = "vo", value = "售后服务信息", required = true)

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 404, message = "参数不合法")
    })
    @Audit // 需要认证
    @PostMapping("orderItems/{id}/aftersales")
    public Object createAftersale(@PathVariable Long id, @Validated @RequestBody CreateAftersaleVo vo, BindingResult bindingResult,
                                  @LoginUser @ApiIgnore @RequestParam(required = false) Long userId){
        if (logger.isDebugEnabled()) {
            logger.debug("createAftersale: id = "+ id +" vo = " + vo);
        }

        if(vo.getType()==null){
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
        }

        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (returnObject != null) {
            logger.info("incorrect data received while updateAftersale id = " + id);
            return returnObject;
        }

        ReturnObject returnObj = aftersaleService.createAftersale(id, vo,userId);
        if(returnObj.getData()!=null) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(returnObj);
        }
        else {
            return Common.getNullRetObj(new ReturnObject<>(returnObj.getCode(), returnObj.getErrmsg()), httpServletResponse);
        }
    }


    /*
    获取售后单的所有状态
    江欣霖
     */
    @ApiOperation(value="获得售后单的所有状态")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @Audit
    @GetMapping("aftersales/states")
    public Object getAllStates(){
        Aftersale.State[] states=Aftersale.State.class.getEnumConstants();
        List<AftersaleStateVo> stateVos=new ArrayList<AftersaleStateVo>();
        for(int i=0;i<states.length;i++){
            stateVos.add(new AftersaleStateVo(states[i]));
        }
        return ResponseUtil.ok(new ReturnObject<List>(stateVos).getData());
    }

    /*
    买家填写运单信息
    江欣霖
     */
    @ApiOperation(value = "买家填写售后的运单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "售后单id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AftersaleSendbackVo", name = "vo", value = "运单号", required = true)

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 404, message = "参数不合法")
    })
    @Audit // 需要认证
    @PutMapping("aftersales/{id}/sendback")
    public Object sendbackAftersale(@PathVariable Long id, @Validated @RequestBody AftersaleSendbackVo vo, BindingResult bindingResult,
                                    @LoginUser @ApiIgnore @RequestParam(required = false) Long userId) {
        if (logger.isDebugEnabled()) {
            logger.debug("sendbackAftersale: id = "+ id +" vo = " + vo);
        }
        if(vo.getCustomerLogSn()==null){
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID,String.format("运单信息不能为空")));
        }
        // 校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (returnObject != null) {
            logger.info("incorrect data received while sendbackAftersale id = " + id);
            return returnObject;
        }

        ReturnObject<Object> returnObj=checkCustomerId(userId,id);
        AftersaleServicePo po=(AftersaleServicePo)returnObj.getData();
        if(po==null){
            return Common.decorateReturnObject(returnObj);
        }

        returnObj = aftersaleService.sendbackAftersale(id, vo);
        return Common.decorateReturnObject(returnObj);
    }

    /*
    买家确认售后单结束
    江欣霖
     */
    @ApiOperation(value = "买家确认售后单结束")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "售后单id", required = true)

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 404, message = "参数不合法")
    })
    @Audit // 需要认证
    @PutMapping("aftersales/{id}/confirm")
    public Object confirmAftersaleById(@PathVariable Long id,@LoginUser @ApiIgnore @RequestParam(required = false) Long userId) {
        if (logger.isDebugEnabled()) {
            logger.debug("confirmAftersaleById: id = "+ id);
        }

        ReturnObject<Object> returnObj=checkCustomerId(userId,id);
        AftersaleServicePo po=(AftersaleServicePo)returnObj.getData();
        if(po==null){
            return Common.decorateReturnObject(returnObj);
        }

        returnObj = aftersaleService.confirmAftersaleById(id);
        return Common.decorateReturnObject(returnObj);
    }


    /*
    店家寄出维修好（调换）的货物
     */
    @ApiOperation(value = "店家寄出维修好（调换）的货物")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "售后单id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AftersaleDeliverVo", name = "vo", value = "运单号", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 404, message = "参数不合法")
    })
    @Audit // 需要认证
    @PutMapping("shops/{shopId}/aftersales/{id}/deliver")
    public Object deliverAftersale(@PathVariable Long id, @PathVariable Long shopId,@Validated @RequestBody AftersaleDeliverVo vo,BindingResult bindingResult) {
        if (logger.isDebugEnabled()) {
            logger.debug("deliverAftersale: id = "+ id+" vo = " + vo);
        }
        // 校验前端数据
        if(vo.getShopLogSn()==null){
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID,String.format("运单信息不能为空")));
        }
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (returnObject != null) {
            logger.info("incorrect data received while deliverAftersale id = " + id);
            return returnObject;
        }

        ReturnObject returnObj = aftersaleService.deliverAftersale(id,shopId,vo);
        return Common.decorateReturnObject(returnObj);
    }



    /*
    买家取消或删除售后单
     */
    @ApiOperation(value = "买家取消或删除售后单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "售后单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 404, message = "参数不合法")
    })
    @Audit
    @DeleteMapping("aftersales/{id}")
    public Object deliverAftersale(@PathVariable Long id,@LoginUser @ApiIgnore @RequestParam(required = false) Long userId){
        if (logger.isDebugEnabled()) {
            logger.debug("deleteAftersale: id = "+ id);
        }

        ReturnObject<Object> returnObj=checkCustomerId(userId,id);
        AftersaleServicePo po=(AftersaleServicePo)returnObj.getData();
        if(po==null){
            return Common.decorateReturnObject(returnObj);
        }


        returnObj = aftersaleService.deleteAftersale(id);
        return Common.decorateReturnObject(returnObj);
    }


    @ApiOperation(value="管理员同意/不同意（退款，换货，维修）")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "Stirng",name = "auhorization",value = "Token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "shopId",value = "店铺id",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "售后单id",required = true),
            @ApiImplicitParam(paramType = "body",dataType = "AftersaleConfirmVo",name = "vo",value = "处理意见")
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code = 404,message = "参数不合法")
    })
    @Audit
    @PutMapping("shops/{shopId}/aftersales/{id}/confirm")
    public Object confirmAftersae(@PathVariable(value = "shopId")Long shopId,@PathVariable("id")Long id,@Validated @RequestBody AftersaleConfirmVo vo,BindingResult bindingResult){
        if(logger.isDebugEnabled()){
            logger.debug("confirmAftersale id="+id+"vo="+vo);
        }

        Object returnObject=Common.processFieldErrors(bindingResult,httpServletResponse);
        if(returnObject!=null){
            logger.info("incorrect data received while confirmAftersale id ="+id);
            return returnObject;
        }

        ReturnObject returnObj=aftersaleService.confirmAftersale(shopId,id,vo);
        return Common.decorateReturnObject(returnObj);
    }


    @ApiOperation(value="店家确认收到买家的退（换）货")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "Stirng",name = "auhorization",value = "Token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "shopId",value = "店铺id",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "售后单id",required = true),
            @ApiImplicitParam(paramType = "body",dataType = "AftersaleConfirmVo",name = "vo",value = "处理意见")
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code = 404,message = "参数不合法")
    })
    @Audit
    @PutMapping("shops/{shopId}/aftersales/{id}/recieve")
    public Object recieveAftersae(@PathVariable(value = "shopId")Long shopId,@PathVariable("id")Long id,@Validated @RequestBody AftersaleConfirmVo vo,BindingResult bindingResult){
        if(logger.isDebugEnabled()){
            logger.debug("confirmAftersale id="+id+"vo="+vo);
        }

        Object returnObject=Common.processFieldErrors(bindingResult,httpServletResponse);
        if(returnObject!=null){
            logger.info("incorrect data received while recieveAftersale id ="+id);
            return returnObject;
        }

        ReturnObject returnObj=aftersaleService.recieveAftersale(shopId,id,vo);
        return Common.decorateReturnObject(returnObj);
    }



    /*
    买家查询某个售后单
     */
    @ApiOperation(value="买家根据售后单id查询售后单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "Stirng",name = "auhorization",value = "Token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "售后单id",required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code = 404,message = "参数不合法")
    })
    @Audit
    @GetMapping("aftersales/{id}")
    public Object getAftersaleById(@PathVariable("id")Long id){
        if(logger.isDebugEnabled()){
            logger.debug("getAftersaleById id="+id);
        }

        ReturnObject returnObj=aftersaleService.getAftersaleById(id);

        if(returnObj.getData()!=null) {
            return Common.getRetObject(returnObj);
        }
        else {
            return Common.getNullRetObj(new ReturnObject<>(returnObj.getCode(), returnObj.getErrmsg()), httpServletResponse);
        }
    }



    /*
    店家查询某个售后单
     */
    @ApiOperation(value="店家根据售后单id查询售后单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "Stirng",name = "auhorization",value = "Token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "售后单id",required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code = 404,message = "参数不合法")
    })
    @Audit
    @GetMapping("shops/{shopId}/aftersales/{id}")
    public Object getAftersaleByShopId(@PathVariable("shopId")Long shopId,@PathVariable("id")Long id){
        if(logger.isDebugEnabled()){
            logger.debug("getAftersaleByShopId id="+id);
        }

        ReturnObject returnObj=aftersaleService.getAftersaleByShopId(shopId,id);

        if(returnObj.getData()!=null) {
            return Common.getRetObject(returnObj);
        }
        else {
            return Common.getNullRetObj(new ReturnObject<>(returnObj.getCode(), returnObj.getErrmsg()), httpServletResponse);
        }
    }



    @ApiOperation(value="买家查询所有售后单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "Stirng",name = "auhorization",value = "Token",required = true),
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "spuId",value = "SPU Id",required = false),
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "skuId",value = "SKU Id",required = false),
            @ApiImplicitParam(paramType = "query",dataType = "LocalDateTime",name = "beginTime",value = "开始时间",required = false),
            @ApiImplicitParam(paramType = "query",dataType = "LocalDateTime",name = "endTime",value = "结束时间",required = false),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "page",value = "页码",required = false),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "pageSize",value = "每页数目",required = false),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "type",value = "售后类型",required = false),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "state",value = "售后状态",required = false)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
    })
    @Audit
    @GetMapping("aftersales")
    public Object getAftersaleByUserId(@LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                                       @RequestParam (required = false)LocalDateTime beginTime,
                                       @RequestParam (required = false)LocalDateTime endTime,
                                       @RequestParam(required = false, defaultValue = "1") Integer page,
                                       @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                       @RequestParam (required = false)Integer type,
                                       @RequestParam (required = false)Integer state){
        if(logger.isDebugEnabled()){
            logger.debug("getAftersaleByUserId id="+userId+",page="+page+",pageSize="+pageSize);
        }

        Object object;

        if(page <= 0 || pageSize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        }
        else {
            object=Common.getPageRetObject(aftersaleService.getAftersaleByUserId(userId,beginTime, endTime, page, pageSize, type, state));
        }
        return object;
    }




    @ApiOperation(value="管理员查询所有售后单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "Stirng",name = "auhorization",value = "Token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "店铺id",required = true),
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "spuId",value = "SPU Id",required = false),
            @ApiImplicitParam(paramType = "query",dataType = "Long",name = "skuId",value = "SKU Id",required = false),
            @ApiImplicitParam(paramType = "query",dataType = "LocalDateTime",name = "beginTime",value = "开始时间",required = false),
            @ApiImplicitParam(paramType = "query",dataType = "LocalDateTime",name = "endTime",value = "结束时间",required = false),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "page",value = "页码",required = false),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "pageSize",value = "每页数目",required = false),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "type",value = "售后类型",required = false),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "state",value = "售后状态",required = false)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code = 404,message = "参数不合法")
    })
    @Audit
    @GetMapping("shops/{id}/aftersales")
    public Object getAllAftersale(@PathVariable("id") Long shopId,
                                  @RequestParam (required = false)LocalDateTime beginTime,
                                  @RequestParam (required = false)LocalDateTime endTime,
                                  @RequestParam(required = false, defaultValue = "1") Integer page,
                                  @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                  @RequestParam (required = false)Integer type,
                                  @RequestParam (required = false)Integer state){
        if(logger.isDebugEnabled()){
            logger.debug("getAllAftersale id="+shopId+",page="+page+",pageSize="+pageSize);
        }

        Object object;

        if(page <= 0 || pageSize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        }
        else {
            object=getPageRetObject(aftersaleService.getAllAftersale(shopId, beginTime, endTime, page, pageSize, type, state));
        }
        return object;
    }



    private ReturnObject<Object>checkCustomerId(Long userId,Long id){
        AftersaleServicePo po=poMapper.selectByPrimaryKey(id);
        ReturnObject returnObj;

        if(po==null){
            logger.info("售后单不存在：id = " + id);
            returnObj=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else {
            if (po.getCustomerId().equals(userId)) {
                returnObj=new ReturnObject<>(po);
            } else {
                logger.info("没有权限修改售后单id="+id);
                returnObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }
        }
        return returnObj;
    }


    private Object getPageRetObject(ReturnObject<PageInfo<VoObject>> returnObject) {
        ResponseCode code = returnObject.getCode();
        switch (code){
            case OK:

                PageInfo<VoObject> objs = returnObject.getData();
                if (objs != null){
                    List<Object> voObjs = new ArrayList<>(objs.getList().size());
                    for (Object data : objs.getList()) {
                        if (data instanceof VoObject) {
                            voObjs.add(((VoObject)data).createSimpleVo());
                        }
                    }

                    Map<String, Object> ret = new HashMap<>();
                    ret.put("list", voObjs);
                    ret.put("total", objs.getTotal());
                    ret.put("page", objs.getPageNum());
                    ret.put("pageSize", objs.getPageSize());
                    ret.put("pages", objs.getPages());
                    return ResponseUtil.ok(ret);
                }else{
                    return ResponseUtil.ok();
                }
            default:
                return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
        }
    }
}

