package cn.edu.xmu.cart.model.bo;

import cn.edu.xmu.cart.model.po.ShoppingCartPo;
import lombok.Data;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Created at 12/11 8:50
 * @author zrh
 */
@Data
public class Cart {
    private Long id;
    private Long customerId;
    private Long goodsSkuId;
    private Integer quantity;
    private Long price;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;


    public Cart(ShoppingCartPo po) {
        this.id=po.getId();
        this.customerId=po.getCustomerId();
        this.goodsSkuId=po.getGoodsSkuId();
        this.quantity=po.getQuantity();
        this.price=po.getPrice();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }
}
