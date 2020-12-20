package cn.edu.xmu.footprint.model.vo;

import cn.edu.xmu.goods.dto.GoodsSkuDTO;
import lombok.Data;

@Data
public class GoodsSku {
    private Long id;
    private String name;
    private String skuSn;
    private String imageUrl;
    private Integer inventory;
    private Integer originalPrice;
    private Integer price;
    private Boolean disable;

    public GoodsSku(GoodsSkuDTO dto){
        id=dto.getId();
        name=dto.getName();
        skuSn=dto.getSkuSn();
        imageUrl=dto.getImageUrl();
        inventory=dto.getInventory();
        originalPrice=dto.getOriginalPrice();
        price=dto.getPrice();
        disable=dto.getDisable();
    }
}
