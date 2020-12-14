package cn.edu.xmu.favorite.model.vo;

import cn.edu.xmu.favorite.model.bo.FavouriteGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FavouriteGoodsRetVo {
    @ApiModelProperty(value="id")
    private Long id;
    @ApiModelProperty(value="用户id")
    private Long customerId;
    @ApiModelProperty(value="商品id")
    private Long goodsSpuId;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    public FavouriteGoodsRetVo(FavouriteGoods favouriteGoods)
    {
        this.id=favouriteGoods.getId();
        this.customerId=favouriteGoods.getCustomerId();
        this.goodsSpuId=favouriteGoods.getGoodsSpuId();
        this.gmtCreate=favouriteGoods.getGmtCreate();
        this.gmtModified=favouriteGoods.getGmtModified();
    }
}
