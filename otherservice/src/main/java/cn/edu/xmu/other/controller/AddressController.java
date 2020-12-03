package cn.edu.xmu.other.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.model.bo.Address;
import cn.edu.xmu.other.model.vo.AddressVo;
import cn.edu.xmu.other.service.AddressService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;


@Api(value="地址", tags = "Address")
@RestController
@RequestMapping(value = "/address",produces = "application/json;charset=UTF-8")
public class AddressController {

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    private AddressService addressService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * @Created at 2020/11/30 10:36
     * @author zrh
     * @param vo
     * @param bindingResult
     * @param userId
     * @return
     */
    @ApiOperation(value = "新建地址")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authrization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body",dataType ="AddressVo", name = "vo",value = "可修改的地址信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功"),
            @ApiResponse(code = 601,message = "达到地址簿上限")
    })
    @Audit
    @PostMapping("/address")
    public Object createAddress(@Validated @RequestBody AddressVo vo, BindingResult bindingResult,
                                @LoginUser @ApiIgnore Long userId){
//        JwtHelper.UserAndDepart userAndDepart = new JwtHelper.UserAndDepart(token);

        logger.debug("insert address by userId "+ userId);

        Object returnObject = Common.processFieldErrors(bindingResult,httpServletResponse);
        if(null != returnObject){
            logger.debug("validate fail");
            return returnObject;
        }
        Address address = vo.createAddress();
        address.setCustomer_id(userId);
        address.setGmtCreate(LocalDateTime.now());
        ReturnObject retObject = addressService.insertAddress(address);
        if (retObject.getData() != null) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            logger.debug(retObject.getData().toString());
            return Common.getRetObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }
    }


    /**
     * @Created at 12/1 0:20
     * @author zrh
     * @param page 页数
     * @param pageSize 每页大小
     */
    @ApiOperation(value = "查询地址",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码",required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数量", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @GetMapping("/addresses")
    public Object seleteAllAddress(@LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
        @RequestParam(required = true, defaultValue = "1") Integer page,
    @RequestParam(required = true,defaultValue = "10") Integer pageSize){
        logger.debug("seleteAllAddress: page= "+page+"  pageSize = "+pageSize);
        ReturnObject<PageInfo<VoObject>> returnObject= addressService.selectAllAddreses(userId,page,pageSize);
        return Common.getPageRetObject(returnObject);
    }


    /**
     * @Created at 12/2 14:02
     * @author zrh
     * @param id
     * @return
     */
    @ApiOperation(value = "设置默认地址",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "Token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Integer",name = "id",value = "地址id",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @Audit
    @PutMapping("/addresses/{id}/default")
    public Object setDefaultAddress(@PathVariable("id") Long id){
        if(logger.isDebugEnabled()) {
            logger.debug("set default address by id" + id);
        }
        ReturnObject returnObject=addressService.setDefaultAddress(id);
        return Common.decorateReturnObject(returnObject);
    }

//
//    @ApiOperation(value = "修改地址信息",produces = "application/json")
//    @ApiImplicitParams({
//            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "Token",required = true),
//            @ApiImplicitParam(paramType = "path",dataType = "Integer",name = "id",value = "地址id",required = true),
//            @ApiImplicitParam(paramType = "body",dataType = "AddressVo",name = "vo",value = "可修改的地址信息",required = true)
//    })
//    @ApiResponses({
//            @ApiResponse(code=0,message = "成功")
//    })
//    @Audit
//    @PutMapping("addresses/{id}")
//    public Object modifyAddressInfo(@LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
//                                    @PathVariable("id") Long id,@Validated @RequestBody AddressVo vo,BindingResult bindingResult)
//    {
//        logger.debug("update Address by Addressid: "+id);
//
//        Object returnObject=Common.processFieldErrors(bindingResult,httpServletResponse);
//        if(returnObject!=null){
//            return returnObject;
//        }
//
//    }






}
