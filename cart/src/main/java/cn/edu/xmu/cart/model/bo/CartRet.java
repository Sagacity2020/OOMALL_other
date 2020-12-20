package cn.edu.xmu.cart.model.bo;

import cn.edu.xmu.goods.dto.CouponActivityDTO;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CartRet implements VoObject {

    private Long id;
    private Long customerId;
    private Long goodsSkuId;
    private Integer quantity;
    private String skuName;
    private Long price;
    private List<CouponActivityDTO> couponActivity;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public CartRet(){
    }

    public CartRet(CartRet cartRet) {
    }

    @Override
    public CartRet createVo() {
        return new CartRet(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
