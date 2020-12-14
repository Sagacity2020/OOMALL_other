package cn.edu.xmu.favorite.controller;

import cn.edu.xmu.favorite.service.FavouriteGoodsService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;


@Api(value="商品收藏服务",tags = "favorite")
@RestController
@RequestMapping(value = "/favorites",produces = "application/json;charset=UTF-8")
public class FavouriteGoodsController {
    private static final Logger logger = LoggerFactory.getLogger(FavouriteGoodsController.class);
    @Autowired
    private FavouriteGoodsService favouriteGoodsService;

    @Autowired
    private HttpServletResponse httpServletResponse;
    /**
    * 买家查看收藏列表
    * @author zwl
    * @param customerId
    * @param page 页数
    * @param pageSize 每页大小
    * @return Object
    * @Date:  2020/12/6 21:27
    */
    @ApiOperation(value = "查看收藏列表", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @GetMapping("")
    public Object getSelfFavouriteGoods(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize,@LoginUser @RequestParam Long customerId)
    {
        page = (page == null)?1:page;
        pageSize = (pageSize == null)?10:pageSize;
        ReturnObject<PageInfo<VoObject>> returnObject =  favouriteGoodsService.getSelfFavouriteGoods(page, pageSize,customerId);
        logger.debug("getSelfFavouriteGoods: customer = " + customerId );
        return Common.getPageRetObject(returnObject);
    }

    /**
     * 收藏商品
     * @author zwl
     * @param customerId
     * @param skuId
     * @return
     * @Date:  2020/12/6 21:34
    */

    @ApiOperation(value = "收藏商品", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "skuId", required = true, dataType = "Long", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("/goods/{skuId}")
    public Object insertFavouriteGoods(@LoginUser @ApiIgnore Long customerId, @PathVariable("skuId") Long skuId)
    {
        ReturnObject<VoObject> retObject =  favouriteGoodsService.insertFavouriteGoods(customerId,skuId);
        logger.debug("getSelfFavouriteGoods: customer = " + customerId );
        return Common.decorateReturnObject(retObject);
    }

    /**
     * 取消收藏
     * @author zwl
     * @param id
     * @return
     * @Date:  2020/12/6 21:35
    */

    @ApiOperation(value = "取消收藏商品", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "id", required = true, dataType = "Long", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @DeleteMapping("/{id}")
    public Object deleteFavouriteGoods(@PathVariable("id") Long id)
    {
        //System.out.println("****"+customerId);
        ReturnObject<Object> retObject =  favouriteGoodsService.deleteFavouriteGoods(id);
        logger.debug("deleteSelfFavouriteGoods: id = " + id );
        return Common.decorateReturnObject(retObject);
    }

}
