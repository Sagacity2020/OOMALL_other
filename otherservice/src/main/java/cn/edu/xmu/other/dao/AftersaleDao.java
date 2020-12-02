package cn.edu.xmu.other.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.other.mapper.AftersaleServicePoMapper;
import cn.edu.xmu.other.model.bo.Aftersale;
import cn.edu.xmu.other.model.po.AftersaleServicePo;
import cn.edu.xmu.other.model.vo.AftersaleDeliverVo;
import cn.edu.xmu.other.model.vo.AftersaleSendbackVo;
import cn.edu.xmu.other.model.vo.AftersaleVo;
import cn.edu.xmu.other.model.vo.CreateAftersaleVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Objects;

@Repository
public class AftersaleDao {
    private static final Logger logger = LoggerFactory.getLogger(AftersaleDao.class);

    AftersaleServicePoMapper aftersaleMapper;

    public ReturnObject<Object> updateAftersale(Long id, AftersaleVo aftersaleVo) {

        AftersaleServicePo po = aftersaleMapper.selectByPrimaryKey(id);

        if (Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.SENDBACKWAIT && Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.CHECK) {
            logger.info("无法修改此售后单信息：id = " + id);
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }
        else if(po.getBeDeleted().intValue()==1){
            logger.info("售后单已删除：id = " + id);
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }

        Aftersale user = new Aftersale(po);
        AftersaleServicePo aftersalePo = user.createUpdatePo(aftersaleVo);


        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = aftersaleMapper.updateByPrimaryKeySelective(aftersalePo);
        } catch (DataAccessException e) {
            // 如果发生 Exception，判断是邮箱还是啥重复错误
                // 其他情况属未知错误

            logger.error("数据库错误：" + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
            return retObj;
        } catch (Exception e) {
            // 其他 Exception 即属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        // 检查更新有否成功
        if (ret == 0) {
            logger.info("售后单不存在：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("售后单 id = " + id + " 的信息已更新");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }
/*
    public AftersaleServicePo createAftersale(CreateAftersaleVo vo){
        AftersaleServicePo po=new AftersaleServicePo();

        po.setOrderItemId(vo.getOrderItemId());
        po.setCustomerId(vo.getCustomerId());
        po.setShopId(vo.getShopId());
        po.setType(vo.getType());
        po.setReason(vo.getReason());
        po.setConclusion(null);
        po.setRefund(vo.getRefund());
        po.setQuantity(vo.getQuantity());
        po.setRegionId(vo.getRegionId());
        po.setDetail(vo.getDetail());
        po.setConsignee(vo.getConsignee());
        po.setMobile(AES.encrypt(vo.getMobile(),Aftersale.AESPASS));
        po.setCustomerLogSn(null);
        po.setShopLogSn(null);
        po.setState(Aftersale.State.CHECK.getCode().byteValue());
        po.setGmtCreated(LocalDateTime.now());
        po.setGmtModified(null);

        int ret=aftersaleMapper.insertSelective(po);

        if(ret!=0){
            logger.info("售后单创建成功");
        }

        AftersaleServicePo aftersalePo=aftersaleMapper.selectByPrimaryKey(po.getId());

        return aftersalePo;
    }
 */


    public ReturnObject<Object> sendbackAftersale(Long id, AftersaleSendbackVo aftersaleVo){
        AftersaleServicePo po = aftersaleMapper.selectByPrimaryKey(id);

        if (Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.SENDBACKWAIT) {
            logger.info("无法修改此售后单信息：id = " + id);
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }
        else if(po.getBeDeleted().intValue()==1){
            logger.info("售后单已删除：id = " + id);
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }

        Aftersale user = new Aftersale(po);
        AftersaleServicePo aftersalePo = user.createSendbackPo(aftersaleVo);

        // 更新数据库
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = aftersaleMapper.updateByPrimaryKeySelective(aftersalePo);
        } catch (DataAccessException e) {
            // 如果发生 Exception，判断是邮箱还是啥重复错误
            // 其他情况属未知错误

            logger.error("数据库错误：" + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
            return retObj;
        } catch (Exception e) {
            // 其他 Exception 即属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        // 检查更新有否成功
        if (ret == 0) {
            logger.info("售后单不存在：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("售后单 id = " + id + " 的信息已更新，买家已发货");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    public ReturnObject<Object> confirmAftersaleById(Long id){
        AftersaleServicePo po = aftersaleMapper.selectByPrimaryKey(id);

        if (Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.REFUNDWAIT && Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.DELIVERING) {
            logger.info("无法修改此售后单信息：id = " + id);
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }
        else if(po.getBeDeleted().intValue()==1){
            logger.info("售后单已删除：id = " + id);
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }

        Aftersale user = new Aftersale(po);
        AftersaleServicePo aftersalePo = user.createConfirmPo();

        // 更新数据库
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = aftersaleMapper.updateByPrimaryKeySelective(aftersalePo);
        } catch (DataAccessException e) {
            // 如果发生 Exception，判断是邮箱还是啥重复错误
            // 其他情况属未知错误

            logger.error("数据库错误：" + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
            return retObj;
        } catch (Exception e) {
            // 其他 Exception 即属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        // 检查更新有否成功
        if (ret == 0) {
            logger.info("售后单不存在：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("售后单 id = " + id + " 的信息已更新，确认售后成功");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    public ReturnObject<Object> deliverAftersale(Long id, Long shopId, AftersaleDeliverVo vo) {
        AftersaleServicePo po = aftersaleMapper.selectByPrimaryKey(id);

        if (Aftersale.State.getTypeByCode(po.getState().intValue())!=Aftersale.State.DILIVERWAIT || shopId!=po.getShopId()) {
            logger.info("无法修改此售后单信息：id = " + id);
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }
        else if(po.getBeDeleted().intValue()==1){
            logger.info("售后单已删除：id = " + id);
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }

        Aftersale user = new Aftersale(po);
        AftersaleServicePo aftersalePo = user.createDeliverPo(vo);

        // 更新数据库
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = aftersaleMapper.updateByPrimaryKeySelective(aftersalePo);
        } catch (DataAccessException e) {
            // 如果发生 Exception，判断是邮箱还是啥重复错误
            // 其他情况属未知错误

            logger.error("数据库错误：" + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
            return retObj;
        } catch (Exception e) {
            // 其他 Exception 即属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        // 检查更新有否成功
        if (ret == 0) {
            logger.info("售后单不存在：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("售后单 id = " + id + " 的信息已更新，店家已发货");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }
}
