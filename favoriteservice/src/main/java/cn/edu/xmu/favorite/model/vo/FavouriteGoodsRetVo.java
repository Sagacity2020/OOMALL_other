package cn.edu.xmu.favorite.model.vo;

import cn.edu.xmu.favorite.model.bo.FavouriteGoods;
import cn.edu.xmu.goods.dto.GoodsSkuDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FavouriteGoodsRetVo {
    @ApiModelProperty(value="id")
    private Long id;

    @ApiModelProperty(value = "收藏时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "商品信息")
    private GoodsSkuDTO goodsSku;

    public FavouriteGoodsRetVo(GoodsSkuDTO goodsSku){

    }

    public FavouriteGoodsRetVo(FavouriteGoods favouriteGoods)
    {
        this.id=favouriteGoods.getId();
        this.goodsSku=favouriteGoods.getGoodsSku();
        this.gmtCreate=favouriteGoods.getGmtCreate();
    }
}
