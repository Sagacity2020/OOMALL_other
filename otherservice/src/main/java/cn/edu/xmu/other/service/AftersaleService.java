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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AftersaleService {
    private Logger logger = LoggerFactory.getLogger(AftersaleService.class);

    @Autowired
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

    /*
    买家填写售后运单信息
     */
    @Transactional
    public ReturnObject<Object> sendbackAftersale(Long id, AftersaleSendbackVo vo) {
        return aftersaleDao.sendbackAftersale(id,vo);
    }

    /*
    买家确认售后结束
     */
    @Transactional
    public ReturnObject<Object> confirmAftersaleById(Long id) {
        return aftersaleDao.confirmAftersaleById(id);
    }


    /*
    店家寄出维修好（调换）的货物
     */
    @Transactional
    public ReturnObject<Object> deliverAftersale(Long id, Long shopId, AftersaleDeliverVo vo) {
        return aftersaleDao.deliverAftersale(id,shopId,vo);
    }

    /*
    买家取消或者逻辑删除售后单
     */
    @Transactional
    public ReturnObject<Object> deleteAftersale(Long id){
        return aftersaleDao.deleteAftersale(id);
    }


    /*
    管理员同意/不同意
     */
    @Transactional
    public ReturnObject<Object> confirmAftersale(Long shopId,Long id,AftersaleConfirmVo vo){
        return aftersaleDao.confirmAftersale(shopId,id,vo);
    }


    @Transactional
    public ReturnObject<Object> recieveAftersale(Long shopId,Long id,AftersaleConfirmVo vo){
        return aftersaleDao.recieveAftersale(shopId,id,vo);
    }
}
