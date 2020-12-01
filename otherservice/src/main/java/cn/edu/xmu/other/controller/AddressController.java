package cn.edu.xmu.other.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.model.bo.Address;
import cn.edu.xmu.other.model.vo.AddressVo;
import cn.edu.xmu.other.model.vo.NewAddressVo;
import cn.edu.xmu.other.service.AddressService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.aspectj.apache.bcel.generic.LineNumberGen;
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
import java.util.concurrent.LinkedTransferQueue;


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
    @ApiImplicitParam({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authrization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body",dataType ="AddressVo", name = "vo",value = "可修改的地址信息"，required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功"),
            @ApiResponse(code = 601,message = "达到地址簿上限")
    })
    @Audit
    @PutMapping("/address")
    public Object createAddress(@Validated @RequestBody AddressVo vo, BindingResult bindingResult,
                                @LoginUser @ApiIgnore @RequestParam(required = false)Long userId){
        logger.debug("insert address by userId"+ userId);

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
            return Common.getRetObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
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







}
