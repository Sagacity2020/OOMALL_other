package cn.edu.xmu.cart.service;


import cn.edu.xmu.cart.dao.CartDao;
import cn.edu.xmu.cart.model.bo.Cart;
import cn.edu.xmu.cart.model.bo.CartRet;
import cn.edu.xmu.cart.model.po.ShoppingCartPo;
import cn.edu.xmu.cart.model.vo.CartRetVo;
import cn.edu.xmu.cart.model.vo.CartVo;
import cn.edu.xmu.goods.dto.CouponActivityDTO;
import cn.edu.xmu.goods.dto.GoodsSkuInfo;
import cn.edu.xmu.goods.service.CouponServiceInterface;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.goods.service.GoodsServiceInterface;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CartService {

    private Logger logger= LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartDao cartDao;

    @DubboReference(version = "0.0.1")
    public GoodsServiceInterface goodsService;

    @DubboReference(version = "0.0.1")
    public CouponServiceInterface couponService;


    /**
     * @Created at 12/13 15:40
     * @author zrh
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject<PageInfo<VoObject>> selectAllCart(Long userId, Integer page, Integer pageSize) {
        PageInfo<ShoppingCartPo> shoppingCartPoPageInfo=cartDao.seleteByUserId(userId,page,pageSize);
        List<VoObject> carts=new ArrayList<>();
        for(ShoppingCartPo po:shoppingCartPoPageInfo.getList()){
            Cart cart=new Cart(po);
            logger.error("123");
            List<CouponActivityDTO> couponActivityDTOS=new ArrayList<>();
            try {
                couponActivityDTOS = couponService.getCouponActivityAlone(cart.getCustomerId(), cart.getGoodsSkuId());
            }catch (Exception e){
                e.printStackTrace();
            }
            cart.setCouponActivity(couponActivityDTOS);
            logger.error(couponActivityDTOS.toString());
            GoodsSkuInfo goodsSkuInfo =null;
            try {
                goodsSkuInfo = goodsService.getGoodsSkuInfoAlone(cart.getGoodsSkuId());
            }catch (Exception e){
                e.printStackTrace();
            }
            cart.setSkuName(goodsSkuInfo.getSkuName());
//            cart.setPrice(goodsSkuInfo.getPrice());
            logger.error(goodsSkuInfo.getSkuName()+goodsSkuInfo.getPrice());
            carts.add(cart);
        }
        PageInfo<VoObject> returnObject=new PageInfo<>(carts);
        returnObject.setPages(shoppingCartPoPageInfo.getPages());
        returnObject.setPageSize(shoppingCartPoPageInfo.getPageSize());
        returnObject.setPageNum(shoppingCartPoPageInfo.getPageNum());
        returnObject.setTotal(shoppingCartPoPageInfo.getTotal());
        return new ReturnObject<>(returnObject);

    }

    /**
     * @Created at 12/11 18:18
     * @author zrh
     * @param userId
     * @param id
     * @return
     */
    public ReturnObject deleteCarts(Long userId, Long id) {
        if(id==0){
            return cartDao.deleteAllCarts(userId);
        }
        else {
            return cartDao.deleteCarts(userId, id);
        }
    }

    /**
     * @Created at 12/13 18:13
     * @author zrh
     * @param cart
     * @return
     */
    public ReturnObject addCartGood(Cart cart) {
        Long goodSkuId=cart.getGoodsSkuId();
        GoodsSkuInfo goodsSkuInfo =new GoodsSkuInfo();
        logger.error(goodSkuId.toString());
        try {
            goodsSkuInfo = goodsService.getGoodsSkuInfoAlone(goodSkuId);
        }catch (Exception e){
            e.printStackTrace();
        }
        logger.error("查询sku信息");
        logger.error(goodsSkuInfo.getSkuName());
        logger.error(goodsSkuInfo.getPrice().toString());
        logger.error("name="+goodsSkuInfo.getSkuName()+"price="+goodsSkuInfo.getPrice());
        if (goodsSkuInfo==null){
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("商品sku不存在"));
        }
        try {
            List<CouponActivityDTO> couponActivityDTOS = couponService.getCouponActivityAlone(cart.getCustomerId(), cart.getGoodsSkuId());
            cart.setCouponActivity(couponActivityDTOS);
            logger.error("123");
        }catch (Exception e){
            e.printStackTrace();
        }
        cart.setSkuName(goodsSkuInfo.getSkuName());
        cart.setPrice(goodsSkuInfo.getPrice());
        ReturnObject<Cart> cartReturnObject=cartDao.addCart(cart);
        if(cartReturnObject.getData()==null){
            return cartReturnObject;
        }
        Cart cart1=cartReturnObject.getData();
        CartRet cartRet=cart1.getRetCart();
        return new ReturnObject(cartRet);
    }


    /**
     * @Created at 12/13 21:19
     * @author zrh
     * @param id
     * @param userId
     * @param vo
     * @return
     */
    public ReturnObject changCartGood(Long id, Long userId, CartVo vo) {
        ReturnObject<ShoppingCartPo> returnObject=cartDao.seleteById(id,userId);
        if(returnObject.getData()==null){
            return returnObject;
        }

        ShoppingCartPo shoppingCartPo= returnObject.getData();
        logger.debug(shoppingCartPo.toString());
        Boolean ret=goodsService.anbleChange(vo.getGoodsSkuId(),shoppingCartPo.getGoodsSkuId());
        logger.debug(ret.toString());
        if(ret==true){
            Cart cart=new Cart();
            cart.setId(id);
            cart.setCustomerId(userId);
            cart.setGoodsSkuId(vo.getGoodsSkuId());
            cart.setQuantity(vo.getQuantity());
            cart.setPrice(goodsService.getGoodsSkuInfoAlone(vo.getGoodsSkuId()).getPrice());
            return cartDao.changeCartInfo(cart);
        }
        else{
            return new ReturnObject(ResponseCode.FIELD_NOTVALID,String.format("不属于一个spu商品"));
        }


    }
}
