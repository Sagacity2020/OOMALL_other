package cn.edu.xmu.advertisement.controller;

import cn.edu.xmu.advertisement.model.bo.Advertisement;
import cn.edu.xmu.advertisement.model.vo.AdvertisementCreateVo;
import cn.edu.xmu.advertisement.model.vo.AdvertisementStateVo;
import cn.edu.xmu.advertisement.service.AdvertisementService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Api(value = "广告", tags = "Advertisement")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/advertise", produces = "application/json;charset=UTF-8")
public class AdvertisementController {
    private  static  final Logger logger = LoggerFactory.getLogger(AdvertisementController.class);

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private AdvertisementService advertisementService;

    /**
     * 获得广告的所有状态
     * @return
     */
    @ApiOperation(value = "获得广告的所有状态")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "Token",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @Audit
    @GetMapping("advertisement/states")
    public Object getAllStates(){
        Advertisement.State[] states=Advertisement.State.class.getEnumConstants();
        List<AdvertisementStateVo> stateVos=new ArrayList<>();
        for(int i=0;i<states.length;i++){
            stateVos.add(new AdvertisementStateVo(states[i]));
        }
        return ResponseUtil.ok(new ReturnObject<List>(stateVos).getData());
    }



    @ApiOperation(value = "管理员在广告时段下新建广告")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "Token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "广告时段id",required = true),
            @ApiImplicitParam(paramType = "body",dataType = "AdvertisementCreateVo",name = "vo",value = "可填写的广告信息",required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code = 404,message = "参数不合法"),
            @ApiResponse(code = 603,message = "达到广告时段上限")
    })
    @Audit
    @PostMapping("timesegments/{id}/advertisement")
    public Object createAdvertisement(@PathVariable Long id, @Validated @RequestBody AdvertisementCreateVo vo, BindingResult bindingResult){
        if(logger.isDebugEnabled()){
            logger.debug("createAdvertisement id="+id+"vo="+vo);
        }

        Object returnObject= Common.processFieldErrors(bindingResult,httpServletResponse);
        if(returnObject!=null){
            logger.info("incorrrect data recieved while createAdvertisement");
            return returnObject;
        }


        ReturnObject returnObj=advertisementService.createAdvertisement(id,vo);
        if(returnObj.getData()!=null){
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(returnObj);
        }
        else{
            return Common.getNullRetObj(new ReturnObject<>(returnObj.getCode(),returnObj.getErrmsg()),httpServletResponse);
        }
    }



/*
    @ApiOperation(value = "获取当前时段广告列表")
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @GetMapping("advertisement/current")
    public Object getCurrentAdvertisement(){
        if(logger.isDebugEnabled()){
            logger.debug("getCurrentAdvertisement");
        }

        LocalDate localDate=LocalDate.now();
        LocalTime localTime=LocalTime.now();
        ReturnObject returnObject=advertisementService.getCurrentAdvertisement(localDate,localTime);
        return returnObject;
    }

 */



    @ApiOperation(value = "管理员在广告时段下增加广告")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "Token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "tid",value = "广告时段id",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "广告id",required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code = 404,message = "参数不合法"),
            @ApiResponse(code = 603,message = "达到广告时段上限")
    })
    @Audit
    @PostMapping("timesegments/{tid}/advertisement/{id}")
    public Object insertAdvertisement(@PathVariable Long tid,@PathVariable Long id){
        if(logger.isDebugEnabled()){
            logger.debug("insertAdvertisement id="+id+"into tid="+tid);
        }


        ReturnObject returnObj=advertisementService.insertAdvertisement(tid,id);
        if(returnObj.getData()!=null){
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(returnObj);
        }
        else{
            return Common.getNullRetObj(new ReturnObject<>(returnObj.getCode(),returnObj.getErrmsg()),httpServletResponse);
        }
    }



    @ApiOperation(value = "管理员查看某一个广告时段的广告")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "Token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "广告时段id",required = true),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "page",value = "页码",required = false),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "pageSize",value = "每页数目",required = false)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code = 404,message = "参数不合法")
    })
    @Audit
    @GetMapping("timesegments/{id}/advertisement")
    public Object selectAdvertisementBySegId(@PathVariable Long id,
                                         @RequestParam(required = false, defaultValue = "1") Integer page,
                                         @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        if(logger.isDebugEnabled()){
            logger.debug("getAdvertisementBySegId id="+id+",page="+page+",pageSize="+pageSize);
        }

        Object object;

        if(page <= 0 || pageSize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        }
        else {
            object=Common.getPageRetObject(advertisementService.getAdvertisementBySegId(id, page, pageSize));
        }
        return object;
    }



    @ApiOperation(value = "用管理员上传广告图片")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "广告id",required = true),
            @ApiImplicitParam(paramType = "formData", dataType = "file", name = "img", value ="文件", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 506, message = "该目录文件夹没有写入的权限")
    })
    @Audit
    @PostMapping("/advertisement/{id}/uploadImg")
    public Object uploadImg(@PathVariable Long id,@RequestParam("img") MultipartFile multipartFile){
        logger.debug("uploadImg: id = "+ id +" img :" + multipartFile.getOriginalFilename());
        ReturnObject returnObject = advertisementService.uploadImg(id,multipartFile);

        return Common.getNullRetObj(returnObject, httpServletResponse);
    }




}
