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
}
