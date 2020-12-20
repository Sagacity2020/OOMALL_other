package cn.edu.xmu.aftersale.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.aftersale.mapper.AftersaleServicePoMapper;
import cn.edu.xmu.aftersale.model.bo.Aftersale;
import cn.edu.xmu.aftersale.model.po.AftersaleServicePo;
import cn.edu.xmu.aftersale.model.po.AftersaleServicePoExample;
import cn.edu.xmu.aftersale.model.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 */

@Repository
public class AftersaleDao {
    private static final Logger logger = LoggerFactory.getLogger(AftersaleDao.class);

    @Autowired
    private AftersaleServicePoMapper aftersaleMapper;

    public ReturnObject<Object> updateAftersale(Long id, AftersaleVo aftersaleVo) {


        ReturnObject<Object>returnObject=selectAftersale(id,null);
        AftersaleServicePo po=(AftersaleServicePo)returnObject.getData();

        if(po==null){
            return returnObject;
        }


        if (Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.SENDBACKWAIT && Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.CHECK) {
            logger.info("无法修改此售后单信息：id = " + id);
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }

        Aftersale user = new Aftersale(po);
        AftersaleServicePo aftersalePo = user.createUpdatePo(aftersaleVo);


        ReturnObject<Object> retObj=modifyAftersale(aftersalePo,id);
        if(retObj.getCode().equals(ResponseCode.OK)){
            logger.info("售后单 id = " + id + " 的信息已更新");
        }

        return retObj;
    }



    public ReturnObject<AftersaleServicePo> createAftersale(Aftersale aftersale){
//        AftersaleServicePoExample example=new AftersaleServicePoExample();
//        AftersaleServicePoExample.Criteria criteria=example.createCriteria();
//        criteria.andOrderItemIdEqualTo(aftersale.getOrderItemId());
//
//        List<AftersaleServicePo> pos=aftersaleMapper.selectByExample(example);
//        for(AftersaleServicePo po:pos){
//            if(Aftersale.State.getTypeByCode(po.getState().intValue())!=Aftersale.State.CANCEL || Aftersale.State.getTypeByCode(po.getState().intValue())!=Aftersale.State.DISAGREE){
//                logger.info("该订单id="+aftersale.getOrderItemId()+"已经申请过售后服务或者正在申请售后服务，无法再次申请");
//                return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
//            }
//        }

        AftersaleServicePo aftersalePo=aftersale.createInsertPo();

        int ret=aftersaleMapper.insertSelective(aftersalePo);

        if (ret == 0) {
            logger.info("新增售后单失败");
        }
        else{
            logger.info("新建售后单成功");
        }

        return new ReturnObject<>(aftersalePo);
    }


    /**
     * 买家填写售后运单信息
     * @param id
     * @param aftersaleVo
     * @return
     */

    public ReturnObject<Object> sendbackAftersale(Long id, AftersaleSendbackVo aftersaleVo){

        ReturnObject<Object>returnObject=selectAftersale(id,null);
        AftersaleServicePo po=(AftersaleServicePo)returnObject.getData();

        if(po==null){
            return returnObject;
        }

        if (Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.SENDBACKWAIT) {
            logger.info("无法修改此售后单信息：id = " + id);
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }

        Aftersale user = new Aftersale(po);
        AftersaleServicePo aftersalePo = user.createSendbackPo(aftersaleVo);

        // 更新数据库
        ReturnObject<Object> retObj=modifyAftersale(aftersalePo,id);
        if(retObj.getCode().equals(ResponseCode.OK)){
            logger.info("售后单 id = " + id + " 的信息已更新,买家发货成功");
        }

        return retObj;
    }


    /**
     * 买家确认售后单结束
     * @param id
     * @return
     */
    public ReturnObject<Object> confirmAftersaleById(Long id){

        ReturnObject<Object>returnObject=selectAftersale(id,null);
        AftersaleServicePo po=(AftersaleServicePo)returnObject.getData();

        if(po==null){
            return returnObject;
        }

        if (Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.REFUNDWAIT && Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.DELIVERING) {
            logger.info("无法修改此售后单信息：id = " + id);
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }


        Aftersale user = new Aftersale(po);
        AftersaleServicePo aftersalePo = user.createConfirmPo();

        // 更新数据库
        ReturnObject<Object> retObj=modifyAftersale(aftersalePo,id);
        if(retObj.getCode().equals(ResponseCode.OK)){
            logger.info("售后单 id = " + id + " 的信息已更新,确认售后结束成功");
        }

        return retObj;
    }


    /**
     * 卖家寄出货物
     * @param id
     * @param shopId
     * @param vo
     * @return
     */
    public ReturnObject<Object> deliverAftersale(Long id, Long shopId, AftersaleDeliverVo vo,Long orderId) {
        ReturnObject<Object>returnObject=selectAftersale(id,shopId);
        AftersaleServicePo po=(AftersaleServicePo)returnObject.getData();

        if(po==null){
            return returnObject;
        }

        if (Aftersale.State.getTypeByCode(po.getState().intValue())!=Aftersale.State.DILIVERWAIT || po.getType().intValue()==1) {
            logger.info("无法修改此售后单信息：id = " + id);
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }



        Aftersale user = new Aftersale(po);
        AftersaleServicePo aftersalePo = user.createDeliverPo(vo,orderId);


        // 更新数据库
        ReturnObject<Object> retObj=modifyAftersale(aftersalePo,id);
        if(retObj.getCode().equals(ResponseCode.OK)){
            logger.info("售后单 id = " + id + " 的信息已更新,店家发货成功");
        }

        return retObj;
    }


    /**
     * 买家取消或逻辑删除售后单
     * @param id
     * @return
     */
    public ReturnObject<Object> deleteAftersale(Long id)
    {
        ReturnObject<Object>returnObject=selectAftersale(id,null);
        AftersaleServicePo po=(AftersaleServicePo)returnObject.getData();

        if(po==null){
            return returnObject;
        }

        if (Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.SENDBACKWAIT && Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.CHECK && Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.SUCESS && Aftersale.State.getTypeByCode(po.getState().intValue()) != Aftersale.State.CANCEL) {
            logger.info("无法取消或删除此售后单信息：id = " + id);
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }


        Aftersale aftersale=new Aftersale(po);
        AftersaleServicePo aftersalePo=aftersale.createCancelOrDeletePo();

        ReturnObject<Object> retObj=modifyAftersale(aftersalePo,id);
        if(retObj.getCode().equals(ResponseCode.OK)){
            logger.info("售后单 id = " + id + " 已取消或已删除");
        }

        return retObj;
    }


    /**
     * 管理员同意/不同意售后申请
     * @param shopId
     * @param id
     * @param vo
     * @return
     */
    public ReturnObject<Object> confirmAftersale(Long shopId, Long id, AftersaleConfirmVo vo){

        ReturnObject<Object>returnObject=selectAftersale(id,shopId);
        AftersaleServicePo po=(AftersaleServicePo)returnObject.getData();

        if(po==null){
            return returnObject;
        }

        if(Aftersale.State.getTypeByCode(po.getState().intValue())!=Aftersale.State.CHECK){
            logger.info("无法审核售后单id="+id);
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }


        Aftersale aftersale=new Aftersale(po);
        AftersaleServicePo aftersalePo=aftersale.createConfirmPo(vo);

        ReturnObject<Object> retObj=modifyAftersale(aftersalePo,id);

        if(retObj.getCode().equals(ResponseCode.OK)){
            logger.info("售后单 id = " + id + " 审核成功");
        }

        return retObj;
    }


    /**
     * 店家收到换（退）货
     * @param shopId
     * @param id
     * @param vo
     * @return
     */
    public ReturnObject<Object> recieveAftersale(Long shopId, Long id, AftersaleConfirmVo vo){

        ReturnObject<Object>returnObject=selectAftersale(id,shopId);
        AftersaleServicePo po=(AftersaleServicePo)returnObject.getData();

        if(po==null){
            return returnObject;
        }

        if(Aftersale.State.getTypeByCode(po.getState().intValue())!=Aftersale.State.SENDBACKING){
            logger.info("无法修改售后单id="+id+"的状态");
            return new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW);
        }

        Aftersale aftersale=new Aftersale(po);
        AftersaleServicePo aftersalePo=aftersale.createRecievePo(vo);

        ReturnObject<Object> retObj=modifyAftersale(aftersalePo,id);
        if(retObj.getCode().equals(ResponseCode.OK)){
            logger.info("售后单 id = " + id + " 的信息已更新,店家验收成功");
        }

        return retObj;
    }


    /**
     * 买家查询某个售后信息
     * @param id
     * @return
     */
    public ReturnObject<Object> getAftersaleById(Long id){

        ReturnObject returnObject=selectAftersale(id,null);
        if(returnObject.getData()!=null){
            logger.info("获取售后单id="+id+"成功");
        }
        return returnObject;
    }


    /**
     * 店家查询某个售后
     * @param shopId
     * @param id
     * @return
     */
    public ReturnObject<Object> getAftersaleByShopId(Long shopId,Long id){

        ReturnObject returnObject=selectAftersale(id,shopId);
        if(returnObject.getData()!=null){
            logger.info("获取店铺id="+shopId+"售后单id="+id+"成功");
        }
        return returnObject;
    }


    /**
     * 买家查询所有售后单
     * @param userId
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @param type
     * @param state
     * @return
     */
    public ReturnObject<PageInfo<AftersaleServicePo>>getAftersaleByUserId(Long userId,
                                                                          LocalDateTime beginTime, LocalDateTime endTime,
                                                                          Integer page, Integer pageSize,
                                                                          Integer type,Integer state){
        AftersaleServicePoExample example=new AftersaleServicePoExample();
        AftersaleServicePoExample.Criteria criteria=example.createCriteria();
        criteria.andCustomerIdEqualTo(userId);

        if(beginTime!=null){
            criteria.andGmtCreateGreaterThanOrEqualTo(beginTime);
        }
        if(endTime!=null){
            criteria.andGmtCreateLessThanOrEqualTo(endTime);
        }
        if(type!=null){
            criteria.andTypeEqualTo(type.byteValue());
        }
        if(state!=null){
            criteria.andStateEqualTo(state.byteValue());
        }

        ReturnObject returnObject=selectAftersaleByExample(example,page,pageSize);

        if(returnObject.getData()!=null){
            logger.info("获取用户id="+userId+"的售后单成功");
        }
        return returnObject;
    }




    public ReturnObject<PageInfo<AftersaleServicePo>>getAllAftersale(Long shopId,
                                                                     LocalDateTime beginTime, LocalDateTime endTime,
                                                                     Integer page, Integer pageSize,
                                                                     Integer type,Integer state){
        AftersaleServicePoExample example=new AftersaleServicePoExample();
        AftersaleServicePoExample.Criteria criteria=example.createCriteria();
        if(shopId==0){
            criteria.andShopIdIsNotNull();
        }
        else {
            criteria.andShopIdEqualTo(shopId);
        }

        if(beginTime!=null){
            criteria.andGmtCreateGreaterThanOrEqualTo(beginTime);
        }
        if(endTime!=null){
            criteria.andGmtCreateLessThanOrEqualTo(endTime);
        }
        if(type!=null){
            criteria.andTypeEqualTo(type.byteValue());
        }
        if(state!=null){
            criteria.andStateEqualTo(state.byteValue());
        }

        ReturnObject returnObject=selectAftersaleByExample(example,page,pageSize);

        if(returnObject.getData()!=null){
            logger.info("获取店铺id="+shopId+"的售后单成功");
        }
        return returnObject;
    }


    private ReturnObject<Object>selectAftersale(Long id,Long shopId){

        ReturnObject<Object>returnObject;
        AftersaleServicePo po;

        try {
            po = aftersaleMapper.selectByPrimaryKey(id);
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

        if(po==null){
            logger.info("售后单不存在：id = " + id);
            returnObject= new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else if(po.getBeDeleted().intValue()==1){
            logger.info("售后单已删除：id = " + id);
            returnObject= new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else if(shopId!=null && !po.getShopId().equals(shopId) && shopId!=0){
            logger.info("没有权限修改或查看售后单id="+id);
            returnObject= new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        else{
            returnObject=new ReturnObject<>(po);
        }
        return returnObject;
    }


    private ReturnObject<Object>modifyAftersale(AftersaleServicePo aftersalePo,Long id){
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = aftersaleMapper.updateByPrimaryKeySelective(aftersalePo);
        } catch (DataAccessException e) {
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
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    private ReturnObject<PageInfo<AftersaleServicePo>> selectAftersaleByExample(AftersaleServicePoExample example,
                                                                                Integer page, Integer pageSize) {
        //PageHelper.startPage(page, pageSize);

        List<AftersaleServicePo> aftersalePos = null;
        try {
            //不加限定条件查询所有
            aftersalePos = aftersaleMapper.selectByExample(example);
            /*List<Aftersale> ret = new ArrayList<>(aftersalePos.size());
            for (AftersaleServicePo po : aftersalePos) {
                Aftersale aftersale = new Aftersale(po);
                ret.add(aftersale);
            }
            PageInfo<Aftersale> aftersalePage = PageInfo.of(ret);

             */
            PageInfo<AftersaleServicePo> retObj=new PageInfo<>(aftersalePos);
            return new ReturnObject<>(retObj);
        } catch (DataAccessException e) {
            logger.error("selectAllRole: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }


    /**
     * 内部api，确定订单是否进行售后
     * @param orderItemId
     * @return
     */
    public boolean checkIsAftersale(Long orderItemId){
        AftersaleServicePoExample example=new AftersaleServicePoExample();
        AftersaleServicePoExample.Criteria criteria=example.createCriteria();
        criteria.andOrderItemIdEqualTo(orderItemId);
        criteria.andTypeEqualTo((byte)1);
        criteria.andStateNotEqualTo(Aftersale.State.CANCEL.getCode().byteValue());
        try{
            List<AftersaleServicePo> pos=aftersaleMapper.selectByExample(example);
            if(pos.size()==0){
                return true;
            }
            else{
                return false;
            }
        } catch (DataAccessException e) {
            logger.error("selectAllRole: DataAccessException:" + e.getMessage());
            return false;
        } catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return false;
        }
    }
}
