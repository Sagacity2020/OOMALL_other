package cn.edu.xmu.share.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.share.model.po.BeSharePo;
import cn.edu.xmu.share.model.vo.BeShareRetVo;
import cn.edu.xmu.share.model.vo.GoodSkuVo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BeShare implements VoObject {
    private Long id;
    private Long goodsSkuId;
    private GoodSkuVo goodSkuVo = new GoodSkuVo();
    private Long sharerId;
    private Long shareId;
    private Long customerId;
    private Long orderId;
    private Integer rebate;
    private LocalDateTime gmtCreat;
    private LocalDateTime gmtModified;
    private Long shareActivityId;

    public BeShare()
    {

    }

    /**
     * 构造函数
     *
     * @author zxh
     * @param po 用PO构造
     * createdBy zxh 2020/12/1
     */
    public BeShare(BeSharePo po)
    {
        this.id = po.getId();
        this.goodsSkuId = po.getGoodsSkuId();
        this.sharerId = po.getSharerId();
        this.customerId = po.getCustomerId();
        this.shareId = po.getShareId();
        this.orderId = po.getOrderId();
        this.rebate = po.getRebate();
        this.gmtCreat = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
        this.shareActivityId = po.getShareActivityId();
    }

    /**
     * 生成BeShareRetVo对象作为返回前端
     *
     * @author zxh
     * @return BeShareRetVo
     * createdBy zxh 2020/12/1
     */
    @Override
    public BeShareRetVo createVo() {
        BeShareRetVo beShareRetVo=new BeShareRetVo();
        beShareRetVo.setId(id);
        beShareRetVo.setSharerId(sharerId);
        beShareRetVo.setSku(new Sku(goodSkuVo));
        beShareRetVo.setRebate(rebate);
        beShareRetVo.setShareId(shareId);
        beShareRetVo.setCustomerId(customerId);
        beShareRetVo.setOrderId(orderId);
        beShareRetVo.setGmtCreate(gmtCreat);
        return beShareRetVo;
    }

    /**
     * 生成BeShareSimpleRetVo对象作为返回前端
     * @author zxh
     * @return Object
     * createdBy zxh 2020/12/1
     */
    @Override
    public Object createSimpleVo() {
        return null;
    }

}
