package cn.edu.xmu.other.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.mapper.AftersaleServicePoMapper;
import cn.edu.xmu.other.model.bo.Aftersale;
import cn.edu.xmu.other.model.po.AftersaleServicePo;
import cn.edu.xmu.other.model.vo.*;
import cn.edu.xmu.other.service.AftersaleService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;

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
    AftersaleServicePoMapper poMapper;


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

        AftersaleServicePo po=poMapper.selectByPrimaryKey(id);
        ReturnObject returnObj;

        if(po==null){
            logger.info("售后单不存在：id = " + id);
            returnObj=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else {
            if (po.getCustomerId().equals(userId)) {
                returnObj = aftersaleService.updateAftersale(id, vo);
            } else {
                logger.info("没有权限修改售后单id="+id);
                returnObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }
        }
        return Common.decorateReturnObject(returnObj);
    }

    /*
    提交售后申请
    江欣霖
     */
    /*
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
    public Object createAftersale(@PathVariable Long id, @Validated @RequestBody CreateAftersaleVo vo, BindingResult bindingResult){
        if (logger.isDebugEnabled()) {
            logger.debug("createAftersale: id = "+ id +" vo = " + vo);
        }

        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (returnObject != null) {
            logger.info("incorrect data received while updateAftersale id = " + id);
            return returnObject;
        }
        ReturnObject returnObj = aftersaleService.createAftersale(id, vo);
        return Common.decorateReturnObject(returnObj);
    }
     */

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
    public Object sendbackAftersale(@PathVariable Long id, @Validated @RequestBody AftersaleSendbackVo vo, BindingResult bindingResult) {
        if (logger.isDebugEnabled()) {
            logger.debug("sendbackAftersale: id = "+ id +" vo = " + vo);
        }
        // 校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (returnObject != null) {
            logger.info("incorrect data received while sendbackAftersale id = " + id);
            return returnObject;
        }
        ReturnObject returnObj = aftersaleService.sendbackAftersale(id, vo);
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
    public Object confirmAftersaleById(@PathVariable Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("confirmAftersaleById: id = "+ id);
        }

        ReturnObject returnObj = aftersaleService.confirmAftersaleById(id);
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
    public Object deliverAftersale(@PathVariable Long id){
        if (logger.isDebugEnabled()) {
            logger.debug("deleteAftersale: id = "+ id);
        }

        ReturnObject returnObj = aftersaleService.deleteAftersale(id);
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
    public Object confirmAftersae(@PathParam(value = "shopId")Long shopId,@PathParam("id")Long id,@Validated @RequestBody AftersaleConfirmVo vo,BindingResult bindingResult){
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
    public Object recieveAftersae(@PathParam(value = "shopId")Long shopId,@PathParam("id")Long id,@Validated @RequestBody AftersaleConfirmVo vo,BindingResult bindingResult){
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
}
