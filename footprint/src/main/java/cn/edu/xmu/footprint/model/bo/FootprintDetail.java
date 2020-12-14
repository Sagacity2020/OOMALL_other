package cn.edu.xmu.footprint.model.bo;

import cn.edu.xmu.footprint.model.po.FootPrintPo;
import cn.edu.xmu.footprint.model.vo.FootprintRetVo;
import cn.edu.xmu.footprint.model.vo.GoodsSkuDTO;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;
import org.apache.tomcat.jni.Local;

import java.time.LocalDateTime;

@Data
public class FootprintDetail implements VoObject {
    private Long id;
    private GoodsSkuDTO goodsSku;
    private LocalDateTime gmtCreate;

    public void setGoodsSku(GoodsSkuDTO goodsSku) {
        this.goodsSku = goodsSku;
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

    public GoodsSkuDTO getGoodsSku() {
        return goodsSku;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }
}
