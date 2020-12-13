package cn.edu.xmu.cart.service;


import cn.edu.xmu.cart.dao.CartDao;
import cn.edu.xmu.cart.model.bo.Cart;
import cn.edu.xmu.cart.model.po.ShoppingCartPo;
import cn.edu.xmu.cart.model.vo.CartVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.CouponActivity;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
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

    @DubboReference
    private IGoodsService goodsService;


    /**
     * @Created at 12/13 15:40
     * @author zrh
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject<PageInfo<VoObject>> selectAllCart(Long userId, Integer page, Integer pageSize) {
       ReturnObject<List<Cart>> cartList=cartDao.seleteByUserId(userId);
       List<Cart> carts=cartList.getData();
       List<Long> goodsSkuIds=null;
       for(Cart cart:carts){
           goodsSkuIds.add(cart.getGoodsSkuId());
       }
       List<CouponActivity> couponActivitys=goodsService.getCouponActivity(goodsSkuIds);
       for(int i=0;i<carts.size();i++){
           carts.get(i).setCouponActivity(couponActivitys.get(i));
       }
       List<VoObject> ret  = new ArrayList<>(carts.size());
       for(Cart bo:carts){

           ret.add(bo);
       }
       PageInfo<VoObject> cartPage = PageInfo.of(ret);
       return new ReturnObject<>(cartPage);


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
        CouponActivity couponActivity=goodsService.getCouponActivityAlone(goodSkuId);
        cart.setCouponActivity(couponActivity);
        ReturnObject<Cart> cartReturnObject=cartDao.addCart(cart);
        return cartReturnObject;
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
        ReturnObject returnObject=cartDao.seleteById(id,userId);
        if(returnObject.getData()==null){
            return returnObject;
        }
        ShoppingCartPo shoppingCartPo= (ShoppingCartPo) returnObject.getData();
        Boolean ret=goodsService.anbleChange(vo.getGoodSkuID(),shoppingCartPo.getGoodsSkuId());
        if(ret==true){
            return cartDao.changeCartInfo(id,userId,vo);
        }
        else{
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("不属于一个spu商品"))；
        }


    }
}
