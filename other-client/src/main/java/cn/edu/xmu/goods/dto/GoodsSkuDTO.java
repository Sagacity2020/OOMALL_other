package cn.edu.xmu.goods.dto;

import lombok.Data;

@Data
public class GoodsSkuDTO {
    private Long id;
    private Long shopId;
    private String name;
    private String skuSn;
    private String imgUrl;
    private Integer inventory;
    private Integer originalPrice;
    private Integer price;
    private Boolean disable;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSkuSn() {
        return skuSn;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public Integer getInventory() {
        return inventory;
    }

    public Integer getOriginalPrice() {
        return originalPrice;
    }

    public Integer getPrice() {
        return price;
    }

    public Boolean getDisable() {
        return disable;
    }

}
