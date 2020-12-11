package cn.edu.xmu.cart.model.bo;

import lombok.Data;

@Data
public class CouponActivity {
    private Long id;
    private Long goodSkuId;
    private String name;
    private String beginTime;
    private String endTIme;
}
