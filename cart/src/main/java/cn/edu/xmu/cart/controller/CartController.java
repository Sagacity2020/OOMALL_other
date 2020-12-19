package cn.edu.xmu.cart.controller;

import cn.edu.xmu.cart.model.bo.Cart;
import cn.edu.xmu.cart.model.vo.CartVo;
import cn.edu.xmu.cart.service.CartService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import lombok.extern.java.Log;
import org.apache.commons.lang3.ArchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.keyvalue.repository.config.QueryCreatorType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Retention;

@Api(value = "购物车",tags = "Cart")
@RestController
@RequestMapping(value = "/cart",produces = "application/json;charset=UTF-8")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    CartService cartService;

    /**
     * @Created at 12/13 18:12
     * @author zrh
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "获得购物车列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码",required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数量", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @GetMapping("/carts")
    public Object selectAllCarts(@LoginUser @ApiIgnore @RequestParam(required = false)Long userId,
                                 @RequestParam(required = true,defaultValue = "1")Integer page,
                                 @RequestParam(required = true,defaultValue = "10")Integer pageSize){
        logger.debug("seleteAllCart: page="+page+" pagaSize= "+pageSize);
        ReturnObject<PageInfo<VoObject>> returnObject= cartService.selectAllCart(userId,page,pageSize);
        return Common.getPageRetObject(returnObject);
    }

    /**
     * @Created at 12/11 18:18
     * @author zrh
     * @param userId
     * @return
     */
    @ApiOperation(value = "清空购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "Token",required = true)

    })
    @ApiResponses({
            @ApiResponse(code =0,message = "成功")
    })
    @Audit
    @DeleteMapping("/carts")
    public Object deleteAllCarts(@LoginUser @ApiIgnore @RequestParam(required = false)Long userId){
        logger.debug("userId: "+userId);
        ReturnObject returnObject=cartService.deleteCarts(userId,0L);
        return Common.decorateReturnObject(returnObject);
    }


    /**
     * @Created at 12/11 18:18
     * @author zrh
     * @param userId
     * @param id
     * @return
     */
    @ApiOperation(value = "删除购物车中商品")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "Token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Long",name = "id",value = "购物车id",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @Audit
    @DeleteMapping("/carts/{id}")
    public Object deleteCarts(@LoginUser @ApiIgnore @RequestParam(required = false)Long userId, @PathVariable Long id){
        logger.debug("delete cart id= "+id);
        ReturnObject returnObject=cartService.deleteCarts(userId,id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * @Created at 12/13 18:13
     * @author zrh
     * @param userId
     * @param vo
     * @param bindingResult
     * @return
     */
    @ApiOperation("加入购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "autorization",value = "Token",required = true),
            @ApiImplicitParam(paramType = "body",dataType = "CartVo",name = "vo",value = "可填写的信息",required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功")
    })
    @Audit
    @PostMapping("/carts")
    public Object addCarts(@LoginUser @ApiIgnore @RequestParam(required = false)Long userId, @Validated @RequestBody CartVo vo, BindingResult bindingResult){
        logger.debug("add cart userId: "+userId);
        Object returnObject=Common.processFieldErrors(bindingResult,httpServletResponse);
        if(returnObject!=null) {
            return returnObject;
        }
        Cart cart=new Cart();
        cart.setGoodsSkuId(vo.getGoodSkuID());
        cart.setCustomerId(userId);
        cart.setQuantity(vo.getQuantity());
        ReturnObject object=cartService.addCartGood(cart);
        if(object==null){
            return Common.getNullRetObj(object,httpServletResponse);
        }
        else{
            return Common.decorateReturnObject(object);
        }

    }

    /**
     * @Created at 12/13 21:20
     * @author zrh
     * @param userId
     * @param id
     * @param vo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "修改购物车规格或数量")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "Token",required = true),
            @ApiImplicitParam(paramType = "path",dataType = "String",name = "id",value = "购物车id",required = true),
            @ApiImplicitParam(paramType = "body",dataType = "CartVo",name = "vo",value = "可修改的信息",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @Audit
    @PutMapping("/carts/{id}")
    public Object changeCartById(@LoginUser @ApiIgnore @RequestParam(required = false)Long userId,@PathVariable Long id,@Validated @RequestBody CartVo vo,BindingResult bindingResult){
        logger.debug("change cart by id= "+id);
        Object object=Common.processFieldErrors(bindingResult,httpServletResponse);
        if(object!=null){
            return object;
        }
        ReturnObject returnObject=cartService.changCartGood(id,userId,vo);
        return Common.decorateReturnObject(returnObject);
    }



}
