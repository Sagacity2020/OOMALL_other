package cn.edu.xmu.time.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.time.model.vo.TimeSegmentVo;
import cn.edu.xmu.time.service.TimeSegmentService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Api(value="时间段服务",tags = "timeSegment")
@RestController
@RequestMapping(value = "/time",produces = "application/json;charset=UTF-8")
public class TimeSegmentController {
    private static final Logger logger = LoggerFactory.getLogger(TimeSegmentController.class);
    @Autowired
    private TimeSegmentService timeSegmentService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 获取广告时间段
     * @author zwl
     * @param page 页数
     * @param pageSize 每页大小
     * @return
     * @Date:  2020/12/6 21:37
     */

    @ApiOperation(value = "获取广告时间段", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "did", value = "店id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("/shops/{did}/advertisement/timesegments")
    public Object selectAdTimeSegments(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize)
    {
        logger.debug("selectAdTimeSegments: page = "+ page +"  pageSize ="+pageSize);
        page = (page == null)?1:page;
        pageSize = (pageSize == null)?10:pageSize;

        ReturnObject<PageInfo<VoObject>> returnObject =  timeSegmentService.selectAdTimeSegments(page, pageSize);
        return Common.getPageRetObject(returnObject);
    }

    /**
     * 新增广告时间段
     * @author zwl
     * @param vo
     * @return
     * @Date:  2020/12/6 21:37
     */
    @ApiOperation(value = "新增广告时间段", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "TimeSegmentVo", name = "vo", value = "新增广告时间段信息", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "did", value = "店id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 610, message = "开始时间大于结束时间"),
    })
    @Audit
    @PostMapping("/shops/{did}/advertisement/timesegments")
    public Object insertAdTimeSegment(@Validated @RequestBody TimeSegmentVo vo, BindingResult bindingResult)
    {
        //logger.debug("insert AdTimeSegment by userId:" + userId);
//        if(vo.getBeginTime().isEmpty())
//        {
//            return new ReturnObject<>(ResponseCode.Log_BEGIN_NULL);
//        }
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (returnObject != null) {
            //logger.info("incorrect data received while modifyUserInfo id = " + id);
            return returnObject;
        }

        ReturnObject<VoObject> retObject = timeSegmentService.insertAdTimeSegment(vo.creatTimeSegment());
        httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(retObject);
    }

    /**
     * 删除时间段
     * @author zwl
     * @param id
     * @return
     * @Date:  2020/12/6 21:41
     */
    @ApiOperation(value = "删除时间段", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "时间段id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "did", value = "店id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @DeleteMapping("/shops/{did}/advertisement/timesegments/{id}")
    public Object deleteAdTimeSegment(@PathVariable("id") Long id) {
        //logger.debug("delete role");
        ReturnObject<Object> returnObject = timeSegmentService.deleteAdTimeSegment(id);
        return Common.decorateReturnObject(returnObject);
    }
}
