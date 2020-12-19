package cn.edu.xmu.footprint.model.vo;

import cn.edu.xmu.footprint.model.bo.FootprintDetail;

import cn.edu.xmu.goods.dto.GoodsSkuDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "足迹视图对象")
public class FootprintRetVo {
    @ApiModelProperty(value = "足迹id")
    private Long id;

    @ApiModelProperty(value = "商品信息")
    private GoodsSku goodsSku;

    @ApiModelProperty(value = "浏览时间")
    private LocalDateTime gmtCreate;

    public FootprintRetVo(GoodsSku goodsSku){

    }

    public FootprintRetVo(FootprintDetail bo) {
        id=bo.getId();
        gmtCreate=bo.getGmtCreate();
        goodsSku=bo.getGoodsSku();
    }
}
