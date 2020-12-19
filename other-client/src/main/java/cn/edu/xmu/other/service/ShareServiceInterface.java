package cn.edu.xmu.other.service;

import cn.edu.xmu.other.dto.OrderDTO;

public interface ShareServiceInterface {

    /**
    * 计算返点
    *
    * @author zxh
    * @return Object
    * @Date 2020/12/15 20:37
    */
    Integer calRebate(OrderDTO orderDTO);


    /**
    * 获得beShareId
    *
    * @author zxh
    * @param customerId 被分享者Id
    * @param skuId skuId
    * @return Long
    * @Date 2020/12/15 20:37
    */
    Long getBeShareId(Long customerId, Long skuId);

    /**
    * 查询一个商品是否可以分享
    *
    * @author zxh
    * @param shopId
    * @param goodsSkuId
    * @return Boolean
    * @Date 2020/12/15 21:37
    */
    Boolean isShared(Long shopId, Long goodsSkuId);

}
