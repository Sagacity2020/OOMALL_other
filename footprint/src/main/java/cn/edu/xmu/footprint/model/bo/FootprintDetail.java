package cn.edu.xmu.footprint.model.bo;

import cn.edu.xmu.footprint.model.po.FootPrintPo;
import cn.edu.xmu.footprint.model.vo.FootprintRetVo;
import cn.edu.xmu.footprint.model.vo.GoodsSku;
import cn.edu.xmu.goods.dto.GoodsSkuDTO;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FootprintDetail implements VoObject {
    private Long id;
    private GoodsSku goodsSku;
    private LocalDateTime gmtCreate;

    public void setGoodsSku(GoodsSkuDTO goodsSkuDTO) {
        this.goodsSku = new GoodsSku(goodsSkuDTO);
    }

    public FootprintDetail(FootPrintPo po){
        id=po.getId();
        gmtCreate=po.getGmtCreate();
    }

    @Override
    public FootprintRetVo createVo() {
        return new FootprintRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

    public Long getId() {
        return id;
    }

    public GoodsSku getGoodsSku() {
        return goodsSku;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }
}
