package cn.edu.xmu.cart.model.vo;

import cn.edu.xmu.cart.model.bo.Cart;
import cn.edu.xmu.goods.model.dto.CouponActivity;

import java.time.LocalDateTime;

public class CartRetVo {
    private Long id;
    private Long goodsSkuId;
    private String skuName;
    private Integer quantity;
    private Long price;
    private CouponActivity couponActivity;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public CartRetVo(Cart bo){
        this.id=bo.getId();
        this.goodsSkuId=bo.getGoodsSkuId();
        this.skuName=bo.getSkuName();
        this.quantity=bo.getQuantity();
        this.price=bo.getPrice();
        this.couponActivity=bo.getCouponActivity();
        this.gmtCreate=bo.getGmtCreate();
        this.gmtModified=bo.getGmtModified();
    }
}
