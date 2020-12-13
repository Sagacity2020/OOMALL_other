package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.oomall.goods.model.CouponActivity;

import java.util.List;

/**
 * @Created at 12/13 20:53
 * @author zrh
 */
public interface IGoodsService {
    List<CouponActivity> getCouponActivity(List<Long> goodsSkuIds);

    CouponActivity getCouponActivityAlone(Long goodsSkuId);

    Boolean anbleChange(Long goodSkuID, Long goodsSkuId);
}
