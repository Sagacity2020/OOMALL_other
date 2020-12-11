package cn.edu.xmu.cart.service;


import cn.edu.xmu.cart.dao.CartDao;
import cn.edu.xmu.cart.model.bo.Cart;
import cn.edu.xmu.cart.model.bo.CouponActivity;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CartService {

    private Logger logger= LoggerFactory.getLogger(CartService.class);

    @Autowired
    CartDao cartDao;


//
//    public ReturnObject<PageInfo<VoObject>> selectAllCart(Long userId, Integer page, Integer pageSize) {
//       ReturnObject<List<Cart>> cartList=cartDao.seleteByUserId(userId);
//       List<Cart> carts=cartList.getData();
//       List<CouponActivity> couponActivity=goodsService.getCouponActivity(carts);
//
//
//
//
//
//    }

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
}
