package cn.edu.xmu.share.model.bo;

import cn.edu.xmu.ooad.model.VoObject;

import cn.edu.xmu.share.model.po.SharePo;
import cn.edu.xmu.share.model.vo.GoodSkuVo;
import cn.edu.xmu.share.model.vo.ShareRetVo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Share implements VoObject{
    private Long id;
    private Long sharerId;
    private Long goodsSkuId;
    private GoodSkuVo goodSkuVo = new GoodSkuVo();
    private Integer quantity;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Long shareActivityId;

    public Share()
    {

    }
    /**
     * 构造函数
     *
     * @author zxh
     * @param po 用PO构造
     * createdBy zxh 2020/11/30 13:27
     * modifiedBy zxh 2020/11/30 13:27
     */
    public Share(SharePo po)
    {
        this.id=po.getId();
        this.sharerId=po.getSharerId();
        this.goodsSkuId=po.getGoodsSkuId();
        this.quantity=po.getQuantity();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
        this.shareActivityId=po.getShareActivityId();
    }


    /**
     * 生成ShareRetVo对象作为返回前端
     *
     * @author zxh
     * @return ShareRetVo
     * createdBy zxh 2020/11/30 13:27
     * modifiedBy zxh 2020/11/30 13:27
     */
    @Override
    public ShareRetVo createVo() {
        ShareRetVo shareRetVo=new ShareRetVo();
        shareRetVo.setId(id);
        shareRetVo.setSharerId(sharerId);
        shareRetVo.setSku(new Sku(goodSkuVo));
        shareRetVo.setQuantity(quantity);
        shareRetVo.setGmtCreate(gmtCreate);
        return shareRetVo;
    }

    /**
     * 生成ShareSimpleRetVo对象作为返回前端
     *
     * @author zxh
     * @return ShareRetVo
     * createdBy zxh 2020/11/30 13:27
     * modifiedBy zxh 2020/11/30 13:27
     */
    @Override
    public Object createSimpleVo() {
        return null;
    }
}

