package cn.edu.xmu.cart.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "可填写的信息")
public class CartVo {
    private Long goodSkuID;
    private Integer quantity;
}
