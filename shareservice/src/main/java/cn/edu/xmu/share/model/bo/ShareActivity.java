package cn.edu.xmu.share.model.bo;

import cn.edu.xmu.ooad.model.VoObject;

import cn.edu.xmu.share.model.po.ShareActivityPo;
import cn.edu.xmu.share.model.vo.ShareActivityRetVo;
import cn.edu.xmu.share.model.vo.ShareActivityVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ShareActivity implements VoObject , Serializable {
    private Long id;
    private Long shopId;
    private Long goodsSkuId;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private String strategy;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Byte state;

    public ShareActivity()
    {

    }
    /**
     * 构造函数
     *
     * @author zxh
     * @param vo 用VO构造
     * createdBy zxh 2020/12/5
     */
    public ShareActivity(ShareActivityVo vo)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(vo.getBeginTime() != null)
            this.beginTime=LocalDateTime.parse(vo.getBeginTime(), formatter);
        if(vo.getEndTime() != null)
            this.endTime=LocalDateTime.parse(vo.getEndTime(), formatter);
        if(vo.getStrategy() != null)
            this.strategy=vo.getStrategy();
    }

    /**
     * 构造函数
     *
     * @author zxh
     * @param po 用PO构造
     * createdBy zxh 2020/12/2
     */
    public ShareActivity(ShareActivityPo po)
    {
        this.id = po.getId();
        this.shopId=po.getShopId();
        this.goodsSkuId=po.getGoodsSkuId();
        this.beginTime=po.getBeginTime();
        this.endTime=po.getEndTime();
        this.strategy=po.getStrategy();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
        this.state=po.getState();
    }

    /**
     * 生成ShareActivityRetVo对象作为返回前端
     *
     * @author zxh
     * @return ShareActivityRetVo
     * createdBy zxh 2020/12/2
     */
    @Override
    public ShareActivityRetVo createVo() {
        ShareActivityRetVo shareActivityRetVo=new ShareActivityRetVo();
        shareActivityRetVo.setId(id);
        shareActivityRetVo.setShopId(shopId);
        shareActivityRetVo.setGoodsSpuId(goodsSkuId);
        shareActivityRetVo.setBeginTime(beginTime);
        shareActivityRetVo.setEndTime(endTime);
        shareActivityRetVo.setStrategy(strategy);
        shareActivityRetVo.setState(state);
        return shareActivityRetVo;
    }

    /**
     * 生成ShareSimpleRetVo对象作为返回前端
     *
     * @author zxh
     * @return Object
     * createdBy zxh 2020/12/2
     */
    @Override
    public Object createSimpleVo() {
        return null;
    }
}
