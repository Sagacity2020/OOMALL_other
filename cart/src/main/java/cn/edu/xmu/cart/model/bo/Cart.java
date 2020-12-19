package cn.edu.xmu.cart.model.bo;

import cn.edu.xmu.cart.model.po.ShoppingCartPo;
import cn.edu.xmu.cart.model.vo.CartPage;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.goods.dto.CouponActivityDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @Created at 12/11 8:50
 * @author zrh
 */
@Data
public class Cart implements VoObject {
    private Long id;
    private Long customerId;
    private Long goodsSkuId;
    private Integer quantity;
    private String skuName;
    private Long price;
    private ArrayList<CouponActivityDTO> couponActivity;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;


    public Cart(ShoppingCartPo po) {
        this.id=po.getId();
        this.customerId=po.getCustomerId();
        this.goodsSkuId=po.getGoodsSkuId();
        this.quantity=po.getQuantity();
        this.price=po.getPrice();
        this.couponActivity=null;
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }
    public Cart(){

    }




    @Override
    public CartPage createVo() {
        return new CartPage(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

    public ShoppingCartPo createPo() {
        ShoppingCartPo po=new ShoppingCartPo();
        po.setId(this.getId());
        po.setCustomerId(this.getCustomerId());
        po.setGoodsSkuId(this.getGoodsSkuId());
        po.setQuantity(this.getQuantity());
        po.setPrice(this.getPrice());
        po.setGmtCreate(this.getGmtCreate());
        return po;
    }
}
