package cn.edu.xmu.share.model.vo;


import lombok.Data;

@Data
public class GoodSkuVo {
    private Long id;
    private Long shopId;
    private String name;
    private String skuSn;
    private String imgUrl;
    private Integer inventory;
    private Integer originalPrice;
    private Integer price;
    private Boolean disable;
}
