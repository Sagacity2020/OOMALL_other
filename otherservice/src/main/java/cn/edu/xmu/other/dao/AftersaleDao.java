package cn.edu.xmu.other.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.other.mapper.AftersaleServicePoMapper;
import cn.edu.xmu.other.model.bo.Aftersale;
import cn.edu.xmu.other.model.po.AftersaleServicePo;
import cn.edu.xmu.other.model.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Objects;

@Repository
public class AftersaleDao {
    private static final Logger logger = LoggerFactory.getLogger(AftersaleDao.class);

    @Autowired
    AftersaleServicePoMapper aftersaleMapper;

    public ReturnObject<Object> updateAftersale(Long id, AftersaleVo aftersaleVo) {

        AftersaleServicePo po = aftersaleMapper.selectByPrimaryKey(id);

        if(po.getBeDeleted().intValue()==1){
            logger.info("售后单已删除：id = " + id+",无法修改");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else if (Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.SENDBACKWAIT && Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.CHECK) {
            logger.info("无法修改此售后单信息：id = " + id);
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


    /*
    买家填写售后运单信息
     */
    public ReturnObject<Object> sendbackAftersale(Long id, AftersaleSendbackVo aftersaleVo){
        AftersaleServicePo po = aftersaleMapper.selectByPrimaryKey(id);
        if(po==null){
            logger.info("售后单不存在：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else if(po.getBeDeleted().intValue()==1){
            logger.info("售后单已删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else if (Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.SENDBACKWAIT) {
            logger.info("无法修改此售后单信息：id = " + id);
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



    /*
    买家确认售后单结束
     */
    public ReturnObject<Object> confirmAftersaleById(Long id){
        AftersaleServicePo po = aftersaleMapper.selectByPrimaryKey(id);

        if(po==null){
            logger.info("售后单不存在：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else if(po.getBeDeleted().intValue()==1){
            logger.info("售后单已删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else if (Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.REFUNDWAIT && Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.DELIVERING) {
            logger.info("无法修改此售后单信息：id = " + id);
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



    /*
    卖家寄出维修好（调换）的货物
     */
    public ReturnObject<Object> deliverAftersale(Long id, Long shopId, AftersaleDeliverVo vo) {
        AftersaleServicePo po = aftersaleMapper.selectByPrimaryKey(id);
        if(po==null){
            logger.info("售后单不存在：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else if(po.getBeDeleted().intValue()==1){
            logger.info("售后单已删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else if(po.getShopId()!=shopId){
            logger.info("没有权限修改此售后单信息：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        else if (Aftersale.State.getTypeByCode(po.getState().intValue())!=Aftersale.State.DILIVERWAIT) {
            logger.info("无法修改此售后单信息：id = " + id);
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



    /*
    买家取消或逻辑删除售后单
     */
    public ReturnObject<Object> deleteAftersale(Long id)
    {
        AftersaleServicePo po=aftersaleMapper.selectByPrimaryKey(id);

        if(po==null){
            logger.info("售后单不存在：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if(po.getBeDeleted().intValue()==1){
            logger.info("售后单已删除：id = " + id+",无法重复删除");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if (Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.SENDBACKWAIT && Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.CHECK && Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.SUCESS) {
            logger.info("无法取消或删除此售后单信息：id = " + id);
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }

        Aftersale aftersale=new Aftersale(po);
        AftersaleServicePo aftersalePo=aftersale.createCancelOrDeletePo();

        ReturnObject<Object> returnObject;

        int ret;
        try{
            ret=aftersaleMapper.updateByPrimaryKeySelective(aftersalePo);
        } catch (DataAccessException e) {
            logger.error("数据库错误: "+e.getMessage());
            returnObject=new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误:%s",e.getMessage()));
            return returnObject;
        }catch (Exception e){
            logger.error("严重错误："+e.getMessage());
            returnObject=new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s",e.getMessage()));
            return returnObject;
        }

        if(ret==0){
            logger.info("售后单不存在：id="+id);
            returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }else{
            logger.info("取消或删除售后单id="+id+"成功");
            returnObject=new ReturnObject<>();
        }

        return returnObject;
    }



    /*
    管理员同意/不同意售后申请
     */
    public ReturnObject<Object> confirmAftersale(Long shopId, Long id, AftersaleConfirmVo vo){
        AftersaleServicePo po=aftersaleMapper.selectByPrimaryKey(id);

        if(po==null){
            logger.info("售后单id="+id+"不存在");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if(po.getBeDeleted().intValue()==1){
            logger.info("售后单已删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if(po.getShopId()!=shopId){
            logger.info("没有权限审核售后单id="+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        if(Aftersale.State.getTypeByCode(po.getState().intValue())!=Aftersale.State.CHECK){
            logger.info("无法审核售后单id="+id);
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }

        Aftersale aftersale=new Aftersale(po);
        AftersaleServicePo aftersalePo=aftersale.createConfirmPo(vo);

        ReturnObject<Object> returnObject;

        int ret;
        try{
            ret=aftersaleMapper.updateByPrimaryKeySelective(aftersalePo);
        }catch (DataAccessException e) {
            logger.error("数据库错误: "+e.getMessage());
            returnObject=new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误:%s",e.getMessage()));
            return returnObject;
        }catch (Exception e){
            logger.error("严重错误："+e.getMessage());
            returnObject=new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s",e.getMessage()));
            return returnObject;
        }

        if(ret==0){
            logger.info("售后单不存在：id="+id);
            returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }else{
            logger.info("审核售后单id="+id+"成功");
            returnObject=new ReturnObject<>();
        }

        return returnObject;
    }



    public ReturnObject<Object> recieveAftersale(Long shopId, Long id, AftersaleConfirmVo vo){
        AftersaleServicePo po=aftersaleMapper.selectByPrimaryKey(id);

        if(po==null){
            logger.info("售后单id="+id+"不存在");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if(po.getBeDeleted().intValue()==1){
            logger.info("售后单已删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if(po.getShopId()!=shopId){
            logger.info("没有权限修改售后单id="+id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        if(Aftersale.State.getTypeByCode(po.getState().intValue())!=Aftersale.State.SENDBACKING){
            logger.info("无法修改售后单id="+id+"的状态");
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }

        Aftersale aftersale=new Aftersale(po);
        AftersaleServicePo aftersalePo=aftersale.createRecievePo(vo);

        ReturnObject<Object> returnObject;

        int ret;
        try{
            ret=aftersaleMapper.updateByPrimaryKeySelective(aftersalePo);
        }catch (DataAccessException e) {
            logger.error("数据库错误: "+e.getMessage());
            returnObject=new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误:%s",e.getMessage()));
            return returnObject;
        }catch (Exception e){
            logger.error("严重错误："+e.getMessage());
            returnObject=new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s",e.getMessage()));
            return returnObject;
        }

        if(ret==0){
            logger.info("售后单不存在：id="+id);
            returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }else{
            logger.info("审核售后单id="+id+"成功");
            returnObject=new ReturnObject<>();
        }

        return returnObject;
    }
}
