package cn.edu.xmu.favorite.controller;

import cn.edu.xmu.favorite.service.FavouriteGoodsService;
import cn.edu.xmu.ooad.annotation.*;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;


@Api(value="商品收藏服务",tags = "favorite")
@RestController
@RequestMapping(value = "/favorite",produces = "application/json;charset=UTF-8")
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
    @Audit
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目", required = false)
            //@ApiImplicitParam(paramType = "query", dataType = "int", name = "customerId", value = "用户id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("/favorites")
    public Object getSelfFavouriteGoods(@LoginUser @ApiIgnore @RequestParam(required = false) Long userId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize)
    {
        //
        System.out.println("controller"+userId);

        page = (page == null)?1:page;
        pageSize = (pageSize == null)?10:pageSize;
        ReturnObject<PageInfo<VoObject>> returnObject =  favouriteGoodsService.getSelfFavouriteGoods(page, pageSize,userId);
        logger.debug("getSelfFavouriteGoods: customer = " + userId );

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
            @ApiImplicitParam(paramType = "path", dataType = "Long",name = "skuId", value="skuId",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("/favorites/goods/{skuId}")
    public Object insertFavouriteGoods(@LoginUser @ApiIgnore @RequestParam(required = false) Long customerId, @PathVariable("skuId") Long skuId)
    {
        System.out.println("post FavouriteGoods: customer = " + customerId +"skuId"+skuId);
        logger.info("post FavouriteGoods: customer = " + customerId +"skuId"+skuId);
        ReturnObject<VoObject> retObject =  favouriteGoodsService.insertFavouriteGoods(customerId,skuId);
        logger.debug("getSelfFavouriteGoods: customer = " + customerId );
        logger.error("post FavouriteGoods: customer = " + customerId );
        System.out.println("return"+retObject.toString());

        System.out.println("return"+retObject.getData());

        if (retObject.getData() != null) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.decorateReturnObject(retObject);
        } else {
            return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
        }

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
    @DeleteMapping("/favorites/{id}")
    public Object deleteFavouriteGoods(@LoginUser @ApiIgnore @RequestParam(required = false) Long customerId, @PathVariable("id") Long id)
    {
        //System.out.println("****"+customerId);
        ReturnObject<Object> retObject =  favouriteGoodsService.deleteFavouriteGoods(customerId,id);
        logger.debug("deleteSelfFavouriteGoods: id = " + id );
        return Common.decorateReturnObject(retObject);
    }

}
