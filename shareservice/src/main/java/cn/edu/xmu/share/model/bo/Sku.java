package cn.edu.xmu.share.model.bo;


import cn.edu.xmu.goods.dto.GoodsSkuDTO;
import lombok.Data;

@Data
public class Sku {
    private Long id;
    private String name;
    private String skuSn;
    private String imageUrl;
    private Integer inventory;
    private Integer originalPrice;
    private Integer price;
    private Boolean disable;

    public Sku(){

    }

    public Sku(GoodsSkuDTO vo){
        this.id = vo.getId();
        this.name = vo.getName();
        this.skuSn = vo.getSkuSn();
        this.imageUrl = vo.getImageUrl();
        this.inventory =vo.getInventory();
        this.originalPrice = vo.getOriginalPrice();
        this.price = vo.getPrice();
        this.disable = vo.getDisable();
    }
}