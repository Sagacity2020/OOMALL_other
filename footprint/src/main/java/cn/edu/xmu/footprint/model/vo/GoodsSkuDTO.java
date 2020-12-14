package cn.edu.xmu.footprint.model.vo;

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
