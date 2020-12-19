package cn.edu.xmu.customer.controller;

import cn.edu.xmu.customer.Util.IpUtil;
import cn.edu.xmu.customer.model.bo.Customer;
import cn.edu.xmu.customer.model.vo.*;
import cn.edu.xmu.customer.service.CustomerService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
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
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 ** @author 向姝可
 **/
@EnableSwagger2
@Api(value = "买家用户服务", tags = "user")
@RestController
@RequestMapping(value = "/user",produces = "application/json;charset=UTF-8")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    private  static  final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @ApiOperation(value="获得买家的所有状态")
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @GetMapping("users/states")
    public Object getAllStates(){
        if (logger.isDebugEnabled()) {
            logger.debug("getAllStates");
        }
        Customer.State[] states=Customer.State.class.getEnumConstants();
        List<StateVo> stateVos=new ArrayList<StateVo>();
        for(int i=0;i<states.length;i++){
            stateVos.add(new StateVo(states[i]));
        }
        return ResponseUtil.ok(new ReturnObject<List>(stateVos).getData());
    }

    @ApiOperation(value="注册用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name="vo", value="newCustomerInfo", required = true, dataType="NewCustomerVo", paramType="body")
    })
    @ApiResponses({
            @ApiResponse(code = 731, message = "用户名已被注册"),
            @ApiResponse(code = 732, message = "邮箱已被注册"),
            @ApiResponse(code = 733, message = "电话已被注册"),
            @ApiResponse(code = 0,message = "成功")
    })
    @PostMapping("users")
    public Object register(@Validated @RequestBody NewCustomerVo vo, BindingResult result){
        if (logger.isDebugEnabled()) {
            logger.debug("register");
        }
        if(result.hasErrors()){
            return Common.processFieldErrors(result,httpServletResponse);
        }
        ReturnObject returnObject=customerService.register(vo);
        if (returnObject.getData() != null) {
            logger.info("register: insert customer: " + returnObject.getData());
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(returnObject);
        } else {
            logger.info("register: insert customer fail: "+returnObject.getErrmsg());
            return Common.getNullRetObj(new ReturnObject<>(returnObject.getCode(), returnObject.getErrmsg()), httpServletResponse);
        }
    }

    @ApiOperation(value="买家修改自己信息")
    @Audit
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping("users")
    public Object updateCustomerInfo(@LoginUser @ApiIgnore @RequestParam(required = false) Long userId, @Validated @RequestBody CustomerVo customervo, BindingResult result){
        if (logger.isDebugEnabled()) {
            logger.debug("updateCustomerInfo: id="+userId);
        }
        if(result.hasErrors()){
            return Common.processFieldErrors(result,httpServletResponse);
        }
//        if(customervo.getBirthday()!=null&&!(customervo.getBirthday().isBlank())){
//            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            try{
//            LocalDate.parse(customervo.getBirthday(),df);}
//            catch (Exception e){
//                return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID,String.format("日期格式不正确")));
//            }
//        }
        ReturnObject returnObject=customerService.updateCustomerInfo(userId,customervo);
        return Common.decorateReturnObject(returnObject);
    }

    @ApiOperation(value="买家查看自己信息")
    @Audit
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("users")
    public Object getCustomerSelf(@LoginUser @ApiIgnore @RequestParam(required = false) Long userId){
        if (logger.isDebugEnabled()) {
            logger.debug("getCustomerSelfInfo");
        }
        ReturnObject returnObject = customerService.getCustomerById(userId);
        if (returnObject.getData() != null) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
            //return Common.getNullRetObj(new ReturnObject<>(returnObject.getCode(), returnObject.getErrmsg()));
        }
    }

   @ApiOperation(value="用户重置密码")
    @ApiResponses({
            @ApiResponse(code = 745, message = "与系统预留的邮箱不一致"),
            @ApiResponse(code = 746, message = "与系统预留的电话不一致"),
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping("users/password/reset")
    public Object resetPassword(@RequestBody ResetPwdVo vo, BindingResult bindingResult
            , HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest){
        if (logger.isDebugEnabled()) {
            logger.debug("resetPassword");
        }
        Object o = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(o != null){
            logger.debug("resetPassword failed");
            return o;
        }
        String ip = IpUtil.getIpAddr(httpServletRequest);
        ReturnObject returnObject = customerService.resetPassword(vo,ip);
        return Common.decorateReturnObject(returnObject);
    }

   @ApiOperation(value="用户修改密码")
    @ApiResponses({
            @ApiResponse(code = 745, message = "与系统预留的邮箱不一致"),
            @ApiResponse(code = 746, message = "与系统预留的电话不一致"),
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping("users/password")
    public Object modifyPassword(@RequestBody ModifyPwdVo vo, BindingResult bindingResult
            , HttpServletResponse httpServletResponse){
        if (logger.isDebugEnabled()) {
            logger.debug("modifyPassword");
        }
        Object o = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(o != null){
            logger.debug("modifyPassword failed");
            return o;
        }
        ReturnObject returnObject = customerService.modifyPassword(vo);
        return Common.decorateReturnObject(returnObject);
    }


    @ApiOperation(value = "用户名密码登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name="loginVo", value="loginVo", required = true, dataType="LoginVo", paramType="body")
    })
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @PostMapping("users/login")
    public Object login(@Validated @RequestBody LoginVo loginVo, BindingResult bindingResult
            , HttpServletResponse httpServletResponse){
        /* 处理参数校验错误*/
        Object o = Common.processFieldErrors(bindingResult, httpServletResponse);
        logger.debug("login: userName = "+loginVo.getUserName());
        if(o != null){
            return o;
        }

        //String ip = IpUtil.getIpAddr(httpServletRequest);
        ReturnObject<String> jwt = customerService.login(loginVo.getUserName(), loginVo.getPassword());

        if(jwt.getData() == null){
            logger.debug("login fail："+jwt.getErrmsg());
            return ResponseUtil.fail(jwt.getCode(), jwt.getErrmsg());
        }else{
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return ResponseUtil.ok(jwt.getData());
        }
    }

    @ApiOperation(value = "用户登出")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header")
    })
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @Audit
    @GetMapping("/users/logout")
    public Object logout(@LoginUser @ApiIgnore @RequestParam(required = false) Long userId,HttpServletRequest httpServletRequest){
        logger.debug("logout: userId = "+userId);
        //String jwt="1";
        String token=httpServletRequest.getHeader("authorization");
       ReturnObject<Boolean> success = customerService.logout(userId,token);
        if (success.getData() == null)  {
            logger.debug("logout fail: "+success.getErrmsg());
            return ResponseUtil.fail(success.getCode(), success.getErrmsg());
        }else {
            return ResponseUtil.ok();
        }
        //return ResponseUtil.ok();
    }

    @ApiOperation(value="管理员查看任意买家信息")
    @Audit
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("users/{id}")
    public Object getCustomerById(@PathVariable Long id,@Depart @ApiIgnore @RequestParam(required = false)Long did){
        if (logger.isDebugEnabled()) {
            logger.debug("getCustomerInfo: id = "+ id);
        }
        if(did.equals(-2L))
        {
            logger.debug("getCustomerInfo failed");
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID,String.format("没有权限")));
        }
        ReturnObject returnObject = customerService.getCustomerById(id);
        if (returnObject.getData() != null) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return Common.getRetObject(returnObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(returnObject.getCode(), returnObject.getErrmsg()), httpServletResponse);
        }
    }

    @ApiOperation(value="平台管理员解禁买家")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @Audit
    @PutMapping("/shops/{did}/users/{id}/release")
    public Object releaseCustomer(@PathVariable Long id,@Depart @ApiIgnore @RequestParam(required = false)Long did){
        if (logger.isDebugEnabled()) {
            logger.debug("releaseCustomer: id = "+ id);
        }
        if(did.equals(-2L))
        {
            logger.debug("releaseCustomer failed");
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW,String.format("没有权限")));
        }
        ReturnObject retObject=customerService.releaseCustomer(id);
        return Common.decorateReturnObject(retObject);
    }

    @ApiOperation(value="平台管理员封禁买家")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @Audit
    @PutMapping("/shops/{did}/users/{id}/ban")
    public Object banCustomer(@PathVariable Long id, @Depart @ApiIgnore @RequestParam(required = false)Long did){
        if (logger.isDebugEnabled()) {
            logger.debug("banCustomer: id = "+ id);
        }
        if(did.equals(-2L))
        {
            logger.debug("banCustomer failed");
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW,String.format("没有权限")));
        }
        ReturnObject retObject=customerService.banCustomer(id);
        return Common.decorateReturnObject(retObject);
    }

    @ApiOperation(value = "平台管理员获取所有用户列表")
    @Audit
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "userName", value = "用户名", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "email", value = "邮箱", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "mobile", value = "电话号码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false)
    })
    @GetMapping("/users/all")
    public Object getCustomerAll(@Depart @ApiIgnore @RequestParam(required = false)Long did,
                                 @RequestParam String  userName,
                                 @RequestParam String email,
                                 @RequestParam String mobile,
                                 @RequestParam(required = false, defaultValue = "1")  Integer page,
                                 @RequestParam(required = false, defaultValue = "10")  Integer pageSize){
        if (logger.isDebugEnabled()) {
            logger.debug("getCustomerAll");
        }
        if(did.equals(-2L))
        {
            logger.debug("getCustomerAll failed");
            return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.AUTH_NOT_ALLOW,String.format("没有权限")));
        }
        Object object = null;
        if(page<=0||pageSize<=0){
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        }else{
            ReturnObject<PageInfo<VoObject>> returnObject = customerService.getCustomerAll(userName,email,mobile, page, pageSize);
            //logger.debug("findUserById: getUsers = " + returnObject);
            object = Common.getPageRetObject(returnObject);
        }
        return object;
    }
}

