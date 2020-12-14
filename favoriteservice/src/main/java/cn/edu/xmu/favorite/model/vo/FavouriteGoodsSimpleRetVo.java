package cn.edu.xmu.favorite.model.vo;

import cn.edu.xmu.favorite.model.bo.FavouriteGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FavouriteGoodsSimpleRetVo {
    @ApiModelProperty(value="id")
    private Long id;
    @ApiModelProperty(value="用户id")
    private Long customerId;
    @ApiModelProperty(value="商品id")
    private Long goodsSpuId;

    public FavouriteGoodsSimpleRetVo(FavouriteGoods favouriteGoods){
        this.id=favouriteGoods.getId();
        this.customerId=favouriteGoods.getCustomerId();
        this.goodsSpuId=favouriteGoods.getGoodsSpuId();
    }
}
