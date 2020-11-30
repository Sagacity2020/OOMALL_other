package cn.edu.xmu.other.dao;


import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.mapper.AddressPoMapper;
import cn.edu.xmu.other.model.bo.Address;
import cn.edu.xmu.other.model.po.AddressPo;
import cn.edu.xmu.other.model.po.AddressPoExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class AddressDao {

    private static final Logger logger = LoggerFactory.getLogger(AddressDao.class);

    @Autowired
    private AddressPoMapper addressPoMapper;

    public ReturnObject<Address> insertAddress(Address address){
        AddressPo addressPo = address.getAddressPo();
        List<AddressPo> retObj=null;
        ReturnObject<Address> returnObject;
        AddressPoExample addressPoExample= new AddressPoExample();
        AddressPoExample.Criteria criteria=addressPoExample.createCriteria();
        criteria.andCustomerIdEqualTo(address.getCustomer_id());
        try{
            retObj=addressPoMapper.selectByExample(addressPoExample);

        }
        catch (DataAccessException e){
            logger.debug("sql exception:"+e.getMessage());
            returnObject= new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,String.format("数据库错误：%s",e.getMessage()));

        }
        catch (Exception e)
        {
            logger.error("other exception :"+e.getMessage());
            returnObject= new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,String.format("严重错误",e.getMessage()));
        }
        int length=retObj.size();
        if(length==20){
            logger.error("地址簿达到上限");
            returnObject= new ReturnObject<>(ResponseCode.ADDRESS_OUTLIMIT,String.format("地址簿达到上限"));
        }
        else {
            try{
                int ret= addressPoMapper.insertSelective(addressPo);
                if(ret==0)
                {
                    logger.debug("insertAddress: insert address fail "+addressPo.toString());
                    returnObject= new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("新增失败："+addressPo.getCustomerId()));
                }
                else {
                    logger.debug("insertAddree: insert address="+addressPo.toString());
                    returnObject= new ReturnObject<>(address);
                }
            }
            catch (DataAccessException e) {

                    logger.debug("other sql exception : " + e.getMessage());
                    returnObject= new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
            catch (Exception e) {
                // 其他Exception错误
                logger.error("other exception : " + e.getMessage());
                returnObject= new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
            }
        }
        return returnObject;


    }


}
