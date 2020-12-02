package cn.edu.xmu.other.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.dao.AftersaleDao;
import cn.edu.xmu.other.model.bo.Aftersale;
import cn.edu.xmu.other.model.po.AftersaleServicePo;
import cn.edu.xmu.other.model.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AftersaleService {
    private Logger logger = LoggerFactory.getLogger(AftersaleService.class);

    private AftersaleDao aftersaleDao;

    /*
    买家修改售后单信息
     */
    @Transactional
    public ReturnObject<Object> updateAftersale(Long id, AftersaleVo vo) {
        return aftersaleDao.updateAftersale(id,vo);
    }

    /*
    买家提交售后单
     */
/*
    @Transactional
    public ReturnObject<AftersaleRetVo> createAftersale(Long id, CreateAftersaleVo vo) {
        ReturnObject<AftersaleRetVo> returnObject = null;

        orderItemPo=orderservice.getOrderItem(id);
        orderPo=orderservice.getOrder(orderItempPo.getId());

        vo.setCustomerId(orderPo.getCustomerId());
        vo.setShopId(orderPo.getShopId());

        Long refunds=(orderItemPo.getPrice()/orderItemPo.getQuantity())*vo.getQuantity();
        vo.setRefund(refunds);

        vo.setOrderItemId(id);

        AftersaleServicePo aftersalePo=aftersaleDao.createAftersale(vo);

        if(aftersalePo!=null) {
            Aftersale aftersale = new Aftersale(aftersalePo);

            AftersaleRetVo aftersaleRetVo = new AftersaleRetVo();

            aftersaleRetVo = aftersale.createVo();

            aftersaleRetVo.setOrderId(orderPo.getId());
            aftersaleRetVo.getOrderSn(orderPo.getOrderSn());
            aftersaleRetVo.setSkuId(orderItemPo.getGoodsSkuId());
            aftersaleRetVo.setSkuName(orderItemPo.getName());

            returnObject = new ReturnObject<>(aftersaleRetVo);
        }
        else
        {
            logger.info("订单明细不存在或已被删除：id = " + id);
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return returnObject;
    }
 */

    @Transactional
    public ReturnObject<Object> sendbackAftersale(Long id, AftersaleSendbackVo vo) {
        return aftersaleDao.sendbackAftersale(id,vo);
    }

    @Transactional
    public ReturnObject<Object> confirmAftersaleById(Long id) {
        return aftersaleDao.confirmAftersaleById(id);
    }

    @Transactional
    public ReturnObject<Object> deliverAftersale(Long id, Long shopId, AftersaleDeliverVo vo) {
        return aftersaleDao.deliverAftersale(id,shopId,vo);
    }
}
