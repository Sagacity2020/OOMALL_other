package cn.edu.xmu.address.controller;

import cn.edu.xmu.address.model.bo.Address;
import cn.edu.xmu.address.model.bo.Region;
import cn.edu.xmu.address.model.vo.AddressVo;
import cn.edu.xmu.address.model.vo.RegionVo;
import cn.edu.xmu.address.service.AddressService;
import cn.edu.xmu.address.service.RegionService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
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
import javax.xml.stream.events.Comment;
import java.time.LocalDateTime;


@Api(value="地址", tags = "Address")
@RestController
@RequestMapping(value = "/address",produces = "application/json;charset=UTF-8")
public class AddressController {

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    private AddressService addressService;

    @Autowired
    private RegionService regionService;

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
    @PostMapping("/addresses")
    public Object createAddress(@Validated @RequestBody AddressVo vo, BindingResult bindingResult,
                                @LoginUser @ApiIgnore Long userId){


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
    public Object setDefaultAddress(@LoginUser @ApiIgnore @RequestParam(required = false) Long userId,@PathVariable("id") Long id){
        if(logger.isDebugEnabled()) {
            logger.debug("set default address by id" + id);
        }
        ReturnObject returnObject=addressService.setDefaultAddress(userId,id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * @Created at 12/3 15:30
     * @author zrh
     * @param userId
     * @param id
     * @param vo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "修改地址信息",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "Token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Integer",name = "id",value = "地址id",required = true),
            @ApiImplicitParam(paramType = "body",dataType = "AddressVo",name = "vo",value = "可修改的地址信息",required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功")
    })
    @Audit
    @PutMapping("addresses/{id}")
    public Object modifyAddressInfo(@LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                                    @PathVariable("id") Long id,@Validated @RequestBody AddressVo vo,BindingResult bindingResult)
    {
        logger.debug("update Address by Addressid: "+id);

        Object returnObject=Common.processFieldErrors(bindingResult,httpServletResponse);
        if(returnObject!=null) {
            return returnObject;
        }
        Address address=vo.createAddress();
        address.setCustomer_id(userId);
        address.setId(id);
        address.setGmtModified(LocalDateTime.now());

        ReturnObject retObject=addressService.updateAddress(address);
        return Common.decorateReturnObject(retObject);

    }


    /**
     * @Created at 12/3 19:27
     * @author zrh
     * @param id
     * @return
     */
    @ApiOperation(value = "删除地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization",value = "Token",required = true,dataType = "String",paramType = "header"),
            @ApiImplicitParam(name = "id",value = "地址id",required = true, dataType = "Integer",paramType = "path")

    })
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功"),
    })
    @Audit
    @DeleteMapping("/addresses/{id}")
    public Object deleteAddress(@PathVariable Long id){
        logger.debug("delete address id = "+id);
        ReturnObject returnObject= addressService.deleteAddress(id);
        return Common.decorateReturnObject(returnObject);

    }

    /**
     * @Created at 12/7 1:04
     * @author zrh
     * @param id
     * @return
     */
    @ApiOperation(value = "查询上级地区")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization",value = "Token",required = true,dataType = "String",paramType = "header"),
            @ApiImplicitParam(name = "id",value = "地区id",required = true,dataType = "Integer",paramType = "path")

    })
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @Audit
    @GetMapping("region/{id}/ancestor")
    public Object queryParentRegion(@PathVariable Long id){
        logger.debug("query region id ="+id);
        ReturnObject returnObject=regionService.queryPreRegion(id);
        return Common.decorateReturnObject(returnObject);

    }

    /**
     * @Created at 12/8 23:07
     * @author zrh
     * @param departId
     * @param id
     * @param vo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "增加子地区")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization",value = "Token",required = true,paramType = "header",dataType = "String"),
            @ApiImplicitParam(name = "id",value = "地区id",required = true,dataType = "Integer",paramType = "path"),
            @ApiImplicitParam(name = "vo",value = "新建地区信息",required = true,dataType = "RegionVo",paramType = "body")
    })
    @ApiResponses({
            @ApiResponse(code =0,message = "成功"),
            @ApiResponse(code=602,message = "地区已废弃")
    })
    @Audit
    @PostMapping("regions/{id}/subregions")
    public Object newSubRegion(@Depart @ApiIgnore @RequestParam(required = false)Long departId,  @PathVariable Long id, @Validated @RequestBody RegionVo vo, BindingResult bindingResult){
            if(departId.equals(-2L))
            {
                return Common.decorateReturnObject(new ReturnObject(ResponseCode.FIELD_NOTVALID,String.format("无权限增加地区")));
            }
            Object returnObject = Common.processFieldErrors(bindingResult,httpServletResponse);
            if(null!=returnObject){
                return  returnObject;
            }
            logger.debug("新建地区id："+id);
            Region region=new Region(vo);
            region.setPid(id);
            region.setGmtCreate(LocalDateTime.now());
            region.setState((byte) 1);
            ReturnObject retObject = regionService.newSubRegion(region);
            return Common.decorateReturnObject(retObject);


    }

    /**
     * @Created at 12/10 18:10
     * @author zrh
     * @param departId
     * @param id
     * @param vo
     * @param bindingResult
     * @return
     */

    @ApiOperation(value = "修改地区")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization",value = "Tokem",required = true,paramType = "header",dataType = "String"),
            @ApiImplicitParam(name = "id",value = "地区id",required = true,dataType = "Integer",paramType = "path"),
            @ApiImplicitParam(name="vo",value = "修改地区信息",required = true,dataType = "RegionVo",paramType = "body")

    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code=602,message = "地区已废弃")
    })
    @Audit
    @PutMapping("/regions/{id}")
    public Object changeRegion(@Depart @ApiIgnore @RequestParam(required = false)Long departId, @PathVariable Long id, @Validated @RequestBody RegionVo vo,BindingResult bindingResult){
        Object returnObject=Common.processFieldErrors(bindingResult,httpServletResponse);
        if(returnObject!=null) {
            return returnObject;
        }
        logger.debug("修改地区id："+id);
//        ReturnObject<Integer> retObject=regionService.isRegion(id);
//        Integer ret=retObject.getData();
//        logger.debug(ret.toString());
//        if(!ret.equals(1)){
//            ReturnObject returnObject1=new ReturnObject(retObject.getCode(),retObject.getErrmsg());
//            return Common.decorateReturnObject(returnObject1);
//        }
        Region region=new Region();
        region.setName(vo.getName());
        region.setId(id);
        region.setPostalCode(vo.getPostalCode());
        ReturnObject returnObject1=regionService.updateRegion(region);
        return  Common.decorateReturnObject(returnObject1);

    }

    @ApiOperation(value = "管理员让地区无效")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization",value = "Tokem",required = true,paramType = "header",dataType = "String"),
            @ApiImplicitParam(name = "id",value = "地区id",required = true,dataType = "Integer",paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code=602,message = "地区已废弃")
    })
    @Audit
    @DeleteMapping("/regions/{id}")
    public Object deleteRegion(@Depart @ApiIgnore @RequestParam(required = false) Long departId,@PathVariable Long id){
            logger.debug("delete region id  = "+id);

            ReturnObject returnObject=regionService.deleteRegion(id);
            return Common.decorateReturnObject(returnObject);
    }





}
