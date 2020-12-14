package cn.edu.xmu.cart.model.bo;

import cn.edu.xmu.cart.model.po.ShoppingCartPo;
import cn.edu.xmu.cart.model.vo.CartPage;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.goods.model.dto.CouponActivity;
import lombok.Data;

import java.time.LocalDateTime;

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
    private CouponActivity couponActivity;
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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getGoodsSkuId() {
        return goodsSkuId;
    }

    public void setGoodsSkuId(Long goodsSkuId) {
        this.goodsSkuId = goodsSkuId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public CouponActivity getCouponActivity() {
        return couponActivity;
    }

    public void setCouponActivity(CouponActivity couponActivity) {
        this.couponActivity = couponActivity;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
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
