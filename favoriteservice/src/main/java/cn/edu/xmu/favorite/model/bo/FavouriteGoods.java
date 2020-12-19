package cn.edu.xmu.favorite.model.bo;

import cn.edu.xmu.favorite.model.po.FavouriteGoodsPo;
import cn.edu.xmu.favorite.model.vo.FavouriteGoodsRetVo;
import cn.edu.xmu.favorite.model.vo.FavouriteGoodsSimpleRetVo;
import cn.edu.xmu.favorite.model.vo.FavouriteGoodsVo;

import cn.edu.xmu.goods.dto.GoodsSkuDTO;
import cn.edu.xmu.ooad.model.VoObject;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * @author zwl
 * @param
 * @param
 * @return
 * @Date:  2020/12/7 21:53
 */
@Data
public class FavouriteGoods implements VoObject {
    private Long id;
    private Long customerId;
    private Long goodsSkuId;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    @ApiModelProperty(value = "商品信息")
    private GoodsSkuDTO goodsSku;

    /**
     *
     * @author zwl
     * @param
     * @param
     * @return
     * @Date:  2020/12/7 21:54
     */

    public FavouriteGoods(){}

    /**
     *
     * @author zwl
     * @param
     * @param
     * @return
     * @Date:  2020/12/7 21:54
     */
    public FavouriteGoods(FavouriteGoodsPo po)
    {
        this.id=po.getId();
        this.customerId=po.getCustomerId();
        this.goodsSkuId=po.getGoodsSkuId();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }
    /**
     *
     * @author zwl
     * @param
     * @param
     * @return
     * @Date:  2020/12/7 21:54
     */
    @Override
    public Object createVo()
    {
        return new FavouriteGoodsRetVo(this);
    }

    /**
     *
     * @author zwl
     * @param
     * @param
     * @return
     * @Date:  2020/12/7 21:54
     */
    @Override
    public Object createSimpleVo() {
        return new FavouriteGoodsSimpleRetVo(this);
    }

    public FavouriteGoodsPo createUpdatePo(FavouriteGoodsVo vo){
        FavouriteGoodsPo po=new FavouriteGoodsPo();
        po.setId(this.getId());
        po.setCustomerId(vo.getCustomerId());
        po.setGoodsSkuId(vo.getGoodsSkuId());
        po.setGmtCreate(null);
        po.setGmtModified(LocalDateTime.now());
        return po;
    }

    /**
     *
     * @author zwl
     * @param
     * @param
     * @return
     * @Date:  2020/12/7 21:54
     */
    public FavouriteGoodsPo gotFavouriteGoodsPo()
    {
        FavouriteGoodsPo po=new FavouriteGoodsPo();
        po.setId(this.getId());
        po.setCustomerId(this.getCustomerId());
        po.setGoodsSkuId(this.getGoodsSkuId());
        po.setGmtCreate(null);
        po.setGmtModified(LocalDateTime.now());
        return po;
    }
}
