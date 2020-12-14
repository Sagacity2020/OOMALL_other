package cn.edu.xmu.aftersale.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.aftersale.dao.AftersaleDao;
import cn.edu.xmu.aftersale.model.bo.Aftersale;
import cn.edu.xmu.aftersale.model.po.AftersaleServicePo;
import cn.edu.xmu.aftersale.model.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author
 */
@Service
public class AftersaleService{
    private Logger logger = LoggerFactory.getLogger(AftersaleService.class);

    @Autowired
    private AftersaleDao aftersaleDao;

    /**
     * 买家修改售后单信息
     * @param id
     * @param vo
     * @return
     */
    @Transactional
    public ReturnObject<Object> updateAftersale(Long id, AftersaleVo vo) {
        return aftersaleDao.updateAftersale(id,vo);
    }

    /*
    买家提交售后单
     */

    @Transactional
    public ReturnObject<Aftersale> createAftersale(Long id, CreateAftersaleVo vo, Long userId) {
        ReturnObject<Aftersale> returnObject = null;

       /* orderItemPo=orderservice.getOrderItem(id);
       if(orderItemPo==null){
       logger.info("订单明细不存在或已被删除：id = " + id);
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
       }
        orderPo=orderservice.getOrder(orderItempPo.getOrderId());


        aftersaleBo.setShopId(orderPo.getShopId());

        Long refunds=(orderItemPo.getPrice()/orderItemPo.getQuantity())*vo.getQuantity();
        aftersaleBo.setRefund(refunds);

        */


        Aftersale aftersaleBo=vo.createAftersale();
        aftersaleBo.setOrderItemId(id);
        aftersaleBo.setShopId(1L);
        aftersaleBo.setCustomerId(userId);
        aftersaleBo.setRefund(100L);

        aftersaleBo.setServiceSn(Common.genSeqNum());

        ReturnObject<AftersaleServicePo> returnObj=aftersaleDao.createAftersale(aftersaleBo);
        AftersaleServicePo po=returnObj.getData();

        if(po==null){
            returnObject=new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }
        else{
            Aftersale aftersale=new Aftersale(po);
            aftersale.setOrderId(10L);
            aftersale.setSkuId(1L);
            aftersale.setOrderSn("20201204");
            aftersale.setSkuName("ipad");

            /*
            aftersale.setOrderId(orderPo.getId());
            aftersale.setSkuId(orderItemPo.getGoodsSkuId());
            aftersale.setSkuName(orderItemPo.getName());
            aftersale.setOrderSn(orderPo.getOrderSn());
             */
            returnObject=new ReturnObject<>(aftersale);
        }
        return returnObject;
    }





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
    public ReturnObject<Object> confirmAftersale(Long shopId, Long id, AftersaleConfirmVo vo){
        return aftersaleDao.confirmAftersale(shopId,id,vo);
    }


    /*
    店家收到退（换）货
     */
    @Transactional
    public ReturnObject<Object> recieveAftersale(Long shopId,Long id,AftersaleConfirmVo vo){
        return aftersaleDao.recieveAftersale(shopId,id,vo);
    }


    /*
    买家查询某个售后单
     */
    @Transactional
    public ReturnObject<Object> getAftersaleById(Long id){
        ReturnObject<Object> returnObj=aftersaleDao.getAftersaleById(id);

        AftersaleServicePo po=(AftersaleServicePo)returnObj.getData();

        if(po==null){
            return returnObj;
        }
        else {
            Aftersale aftersale = new Aftersale(po);
            aftersale.setOrderId(10L);
            aftersale.setOrderSn("20201204");
            aftersale.setSkuId(1L);
            aftersale.setSkuName("ipad");

        /*
        orderItemPo=orderservice.getOrderItem(aftersale.getOrderItemId());
        orderPo=orderservice.getOrder(orderItempPo.getOrderId());

        aftersale.setOrderId(orderPo.getId());
        aftersale.setSkuId(orderItemPo.getGoodsSkuId());
        aftersale.setSkuName(orderItemPo.getName());
        aftersale.setOrderSn(orderPo.getOrderSn());
         */
            return new ReturnObject<>(aftersale);
        }
    }



    /*
    店家查询某个售后单
     */
    @Transactional
    public ReturnObject<Object> getAftersaleByShopId(Long shopId,Long id){
        ReturnObject<Object> returnObj=aftersaleDao.getAftersaleByShopId(shopId,id);

        AftersaleServicePo po=(AftersaleServicePo)returnObj.getData();

        if(po==null){
            return returnObj;
        }
        else {
            Aftersale aftersale = new Aftersale(po);
            aftersale.setOrderId(10L);
            aftersale.setOrderSn("20201204");
            aftersale.setSkuId(1L);
            aftersale.setSkuName("ipad");

        /*
        orderItemPo=orderservice.getOrderItem(aftersale.getOrderItemId());
        orderPo=orderservice.getOrder(orderItempPo.getOrderId());

        aftersale.setOrderId(orderPo.getId());
        aftersale.setSkuId(orderItemPo.getGoodsSkuId());
        aftersale.setSkuName(orderItemPo.getName());
        aftersale.setOrderSn(orderPo.getOrderSn());
         */
            return new ReturnObject<>(aftersale);
        }

    }



    /*
    买家查询所有售后单
     */
    @Transactional
    public ReturnObject<PageInfo<VoObject>>getAftersaleByUserId(Long userId,
                                                                LocalDateTime beginTime, LocalDateTime endTime,
                                                                Integer page, Integer pageSize,
                                                                Integer type,Integer state){
        PageHelper.startPage(page,pageSize);

        ReturnObject<PageInfo<AftersaleServicePo>> returnObject=aftersaleDao.getAftersaleByUserId(userId,beginTime,endTime,page,pageSize,type,state);


        PageInfo<AftersaleServicePo> pos = returnObject.getData();
        List<VoObject> ret = new ArrayList<>(pos.getList().size());

        if(pos!=null){
            for (AftersaleServicePo aftersaleServicePo : pos.getList()) {
                Aftersale aftersale=new Aftersale(aftersaleServicePo);
                aftersale.setOrderId(10L);
                aftersale.setOrderSn("20201204");
                aftersale.setSkuId(1L);
                aftersale.setSkuName("ipad");
                ret.add(aftersale);
                /*
        orderItemPo=orderservice.getOrderItem(aftersale.getOrderItemId());
        orderPo=orderservice.getOrder(orderItempPo.getOrderId());

        aftersale.setOrderId(orderPo.getId());
        aftersale.setSkuId(orderItemPo.getGoodsSkuId());
        aftersale.setSkuName(orderItemPo.getName());
        aftersale.setOrderSn(orderPo.getOrderSn());
         */
            }
        }
        PageInfo<VoObject> aftersalePage = new PageInfo<>(ret);
        aftersalePage.setPages(pos.getPages());
        aftersalePage.setPageNum(pos.getPageNum());
        aftersalePage.setPageSize(pos.getPageSize());
        aftersalePage.setTotal(pos.getTotal());

        return new ReturnObject<>(aftersalePage);

    }




    @Transactional
    public ReturnObject<PageInfo<VoObject>>getAllAftersale(Long shopId,
                                                           LocalDateTime beginTime, LocalDateTime endTime,
                                                           Integer page, Integer pageSize,
                                                           Integer type,Integer state){
        PageHelper.startPage(page,pageSize);
        ReturnObject<PageInfo<AftersaleServicePo>> returnObject=aftersaleDao.getAllAftersale(shopId,beginTime,endTime,page,pageSize,type,state);

        PageInfo<AftersaleServicePo> objs = returnObject.getData();
        List<VoObject> ret = new ArrayList<>(objs.getList().size());
        if(objs!=null){
            for (AftersaleServicePo aftersaleServicePo : objs.getList()) {
                Aftersale aftersale=new Aftersale(aftersaleServicePo);
                aftersale.setOrderId(10L);
                aftersale.setOrderSn("20201204");
                aftersale.setSkuId(1L);
                aftersale.setSkuName("ipad");
                ret.add(aftersale);
                /*
        orderItemPo=orderservice.getOrderItem(aftersale.getOrderItemId());
        orderPo=orderservice.getOrder(orderItempPo.getOrderId());

        aftersale.setOrderId(orderPo.getId());
        aftersale.setSkuId(orderItemPo.getGoodsSkuId());
        aftersale.setSkuName(orderItemPo.getName());
        aftersale.setOrderSn(orderPo.getOrderSn());
         */
            }
        }
        PageInfo<VoObject> aftersalePage = new PageInfo<>(ret);
        aftersalePage.setPages(objs.getPages());
        aftersalePage.setPageNum(objs.getPageNum());
        aftersalePage.setPageSize(objs.getPageSize());
        aftersalePage.setTotal(objs.getTotal());

        return new ReturnObject<>(aftersalePage);

    }
}
