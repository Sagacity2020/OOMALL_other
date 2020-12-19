package cn.edu.xmu.favorite.model.vo;

import cn.edu.xmu.favorite.model.bo.FavouriteGoods;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description ="收藏的商品视图对象")
public class FavouriteGoodsVo {
    @ApiModelProperty(value = "商品id")
    private Long goodsSkuId;

    @ApiModelProperty(value = "用户id")
    private Long customerId;

    public FavouriteGoods createFavouriteGoods()
    {
        FavouriteGoods favouriteGoods=new FavouriteGoods();
        favouriteGoods.setCustomerId(this.customerId);
        favouriteGoods.setGoodsSkuId(this.goodsSkuId);
        return favouriteGoods;
    }

}
