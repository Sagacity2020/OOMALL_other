package cn.edu.xmu.cart.model.vo;

import cn.edu.xmu.cart.model.bo.Cart;
import cn.edu.xmu.oomall.goods.model.CouponActivity;

import java.time.LocalDateTime;

public class CartPage{
    private Long id;
    private Long customerId;
    private Long goodsSkuId;
    private Integer quantity;
    private Long price;
    private CouponActivity couponActivity;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public CartPage(Cart bo){
        this.id=bo.getId();
        this.customerId=bo.getCustomerId();
        this.goodsSkuId=bo.getGoodsSkuId();
        this.quantity=bo.getQuantity();
        this.price=bo.getPrice();
        this.couponActivity=bo.getCouponActivity();
        this.gmtCreate=bo.getGmtCreate();
        this.gmtModified=bo.getGmtModified();
    }

}
