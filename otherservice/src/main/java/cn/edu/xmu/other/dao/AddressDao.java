package cn.edu.xmu.other.dao;


import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.mapper.AddressPoMapper;
import cn.edu.xmu.other.model.bo.Address;
import cn.edu.xmu.other.model.po.AddressPo;
import cn.edu.xmu.other.model.po.AddressPoExample;
import cn.edu.xmu.other.model.vo.AddressRetVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AddressDao {

    private static final Logger logger = LoggerFactory.getLogger(AddressDao.class);

    @Autowired
    private AddressPoMapper addressPoMapper;

    /**
     * author zrh
     * Created at 11/31 23:52
     * @param address
     * @return
     */

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
            returnObject= new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR,String.format("数据库错误：%s",e.getMessage()));

        }
        catch (Exception e)
        {
            logger.error("other exception :"+e.getMessage());
            returnObject= new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR,String.format("严重错误",e.getMessage()));
        }
        int length=retObj.size();
        if(length==20){
            logger.error("地址簿达到上限");
            returnObject= new ReturnObject(ResponseCode.ADDRESS_OUTLIMIT,String.format("地址簿达到上限"));
        }
        else {
            try{
                int ret= addressPoMapper.insertSelective(addressPo);
                if(ret==0)
                {
                    logger.debug("insertAddress: insert address fail "+addressPo.toString());
                    returnObject= new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("新增失败："+addressPo.getCustomerId()));
                }
                else {
                    logger.debug("insertAddree: insert address="+addressPo.toString());
                    returnObject= new ReturnObject(address);
                }
            }
            catch (DataAccessException e) {

                    logger.debug("other sql exception : " + e.getMessage());
                    returnObject= new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
            catch (Exception e) {
                // 其他Exception错误
                logger.error("other exception : " + e.getMessage());
                returnObject= new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
            }
        }
        return returnObject;


    }


    /**
     * @Created at 12/1 0:23
     * @author zrh
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject<PageInfo<VoObject>> selectAllAddress(Long userId, Integer page, Integer pageSize) {
        AddressPoExample example = new AddressPoExample();
        AddressPoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(userId);
        //分页查询
        PageHelper.startPage(page, pageSize);
        logger.debug("page = " + page + "pageSize = " + pageSize);
        List<AddressPo> addressPos=null;
        try {
            //不加限定条件查询所有
            addressPos = addressPoMapper.selectByExample(example);
            List<VoObject> ret = new ArrayList(addressPos.size());
            for (AddressPo po : addressPos) {
                Address address = new Address(po);
                ret.add(address);
            }
            PageInfo<VoObject> addressPage = PageInfo.of(ret);
            return new ReturnObject<>(addressPage);
        }
        catch (DataAccessException e){
            logger.error("selectAllAddress: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * @Created at 12/2 14:04
     * @author zrh
     * @param id
     * @return
     */
    public ReturnObject cancelDefaultAddress(Long id) {
        AddressPo po = addressPoMapper.selectByPrimaryKey(id);
        if (po == null) {
            logger.debug("不存在 address id:" + id);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            po.setBeDefault((byte) 1);
        }

        ReturnObject returnObject;
        int ret;
        try {
            ret = addressPoMapper.updateByPrimaryKeySelective(po);
            if (ret == 0) {
                logger.info("地址不存在或已被删除，id =" + id);
                returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);

            } else {
                logger.info("地址 id =" + id + "的默认地址被设为普通地址");
                returnObject = new ReturnObject(ResponseCode.OK);
            }
        } catch (DataAccessException e) {
            logger.error("数据库错误： " + e.getMessage());
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            logger.error("严重错误： " + e.getMessage());
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生未知错误： %s", e.getMessage()));
        }
        return returnObject;
    }
}
