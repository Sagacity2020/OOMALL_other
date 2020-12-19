package cn.edu.xmu.goods.dto;

import lombok.Data;

@Data
public class GoodsSkuDTO {
    private Long id;
    private String name;
    private String skuSn;
    private String imgUrl;
    private Integer inventory;
    private Integer originalPrice;
    private Integer price;
    private Boolean disable;
}
