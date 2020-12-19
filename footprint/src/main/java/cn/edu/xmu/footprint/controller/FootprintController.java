package cn.edu.xmu.footprint.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.footprint.service.FootprintService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 ** @author 向姝可
 **/
@Api(value = "其他服务", tags = "footprint")
 @RestController /*Restful的Controller对象*/
@EnableSwagger2
 @RequestMapping(value = "/footprint", produces = "application/json;charset=UTF-8")
public class FootprintController {
    @Autowired
    private HttpServletResponse httpServletResponse;

    private  static  final Logger logger = LoggerFactory.getLogger(FootprintController.class);
    @Autowired
    private FootprintService footprintService;

    @ApiOperation(value = "增加足迹", produces = "application/json")
    @Audit
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "integer", name = "goodsSkuId", value = "skuId", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @PostMapping("skus/{id}/footprints")
    public Object insertFootprint(@LoginUser @ApiIgnore @RequestParam(required = false) Long userId, @PathVariable("id") Long goodsSkuId){
        logger.debug("insert Footprint userId:" + userId);
        ReturnObject<VoObject> retObject = footprintService.insertFootprint(userId, goodsSkuId);
        return Common.decorateReturnObject(retObject);
    }

    @ApiOperation(value = "管理员查看浏览记录", produces = "application/json")
    //@Audit
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path",dataType = "int",name="店ID",value = "did",required = true),
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "userId", value = "用户id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "beginTime", value = "开始时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "endTime", value = "结束时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false)
    })
    @GetMapping("/shops/{did}/footprints")
    public Object getFootprint(@RequestParam(required = false) Long  userId,
                               @RequestParam(required = false) String beginTime,
                               @RequestParam(required = false) String endTime,
                               @RequestParam(required = false, defaultValue = "1")  Integer page,
                               @RequestParam(required = false, defaultValue = "10")  Integer pageSize)  {
             Object object = null;
             DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
             if(beginTime!=null){
                 try{
                     LocalDateTime.parse(beginTime,df);
                 }
                 catch (Exception e){
                     return Common.getPageRetObject(new ReturnObject<>(new PageInfo<>(new ArrayList<>())));
                 }
             }
        if(endTime!=null){
            try{
                LocalDateTime.parse(endTime,df);
            }
            catch (Exception e){

                return Common.getPageRetObject(new ReturnObject<>(new PageInfo<>(new ArrayList<>())));
            }
        }
        if(beginTime!=null&&endTime!=null){
        LocalDateTime beginLdt=LocalDateTime.parse(beginTime,df);
        LocalDateTime endLdt=LocalDateTime.parse(endTime,df);
        if(beginTime!=null&&endTime!=null&&!beginTime.isBlank()&&!endTime.isBlank()){
            if(beginLdt.isAfter(endLdt)){
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.Log_Bigger), httpServletResponse);
            }
        }}
             if(page<=0||pageSize<=0){
                     object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
             }
             else{
                    ReturnObject<PageInfo<VoObject>> returnObject = footprintService.getFootprint(userId,beginTime,endTime, page, pageSize);
                    //logger.debug("findUserById: getUsers = " + returnObject);
                    object = Common.getPageRetObject(returnObject);
             }
             return object;
    }
}
