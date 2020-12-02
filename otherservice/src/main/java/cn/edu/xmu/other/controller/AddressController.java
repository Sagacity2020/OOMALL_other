package cn.edu.xmu.other.controller;

import cn.edu.xmu.other.service.AddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@Api(value="地址", tags = "Address")
@RestController
@RequestMapping(value = "/address",produces = "application/json;charset=UTF-8")
public class AddressController {
/*
    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    private AddressService addressService;

    @ApiOperation(value = "新建地址")
    @ApiImplicitParam({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authrization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body",dataType ="AddressVo", name = "vo",value = "可修改的地址信息"，required = true)
    })

 */




}
