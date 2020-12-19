package cn.edu.xmu.goods.service;

import cn.edu.xmu.goods.dto.CouponActivityDTO;
import cn.edu.xmu.goods.dto.GoodsSkuInfo;
import cn.edu.xmu.goods.dto.GoodsSkuDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @Created at 12/13 20:53
 * @author zrh
 */
public interface GoodsServiceInterface {

    GoodsSkuInfo getGoodsSkuInfoAlone(Long goodsSkuId);

    Boolean anbleChange(Long newGoodSkuId, Long goodSkuId);

    GoodsSkuDTO getSkuById(Long goodsSkuId);

    Boolean updateFlSegId(Long segId);

    /**
     * 查看一个skuId是否存在
     *
     * @author zxh
     * @param skuId
     * @return Boolean
     * @Date 2020/12/19 15:52
     */
    Boolean hasGoodsSku(Long skuId);

    /**
     * 获得一个skuId的shopId
     *
     * @author zxh
     * @param skuId
     * @return Long shopId
     * @Date 2020/12/19 15:52
     */
    Long getShopIdBySkuId(Long skuId);
}
