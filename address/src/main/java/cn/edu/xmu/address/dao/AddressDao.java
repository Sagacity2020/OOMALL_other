package cn.edu.xmu.address.dao;


import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.address.mapper.AddressPoMapper;
import cn.edu.xmu.address.model.bo.Address;
import cn.edu.xmu.address.model.bo.AddressPage;
import cn.edu.xmu.address.model.po.AddressPo;
import cn.edu.xmu.address.model.po.AddressPoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zrh
 * @Created at 12/6 19:49
 *
 */
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
        int maxAddressNum=20;
        if(length==maxAddressNum){
            logger.error("地址簿达到上限");
            returnObject= new ReturnObject(ResponseCode.ADDRESS_OUTLIMIT,String.format("地址簿达到上限"));
        }
        if(retObj==null){
            addressPo.setBeDefault((byte)1);
        }

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
                AddressPage addressPage = new AddressPage(po);
                ret.add(addressPage);
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
     * @param userId
     * @return
     */
    public ReturnObject cancelDefaultAddress(Long userId) {
        AddressPoExample example=new AddressPoExample();
        AddressPoExample.Criteria criteria=example.createCriteria();
        criteria.andBeDefaultEqualTo((byte)1);
        criteria.andCustomerIdEqualTo(userId);
        List<AddressPo> addressPos=null;
        try {
            addressPos=addressPoMapper.selectByExample(example);
            if(addressPos==null){
                return new ReturnObject(true);
            }
        }catch (DataAccessException e) {

            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        for(int i=0;i<addressPos.size();i++){
            addressPos.get(i).setBeDefault((byte)0);
            try{
                int ret = addressPoMapper.updateByPrimaryKeySelective(addressPos.get(i));
                if(ret==0){
                    return new ReturnObject(false);
                }

            }catch (DataAccessException e) {

                logger.debug("other sql exception : " + e.getMessage());
                return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
            catch (Exception e) {
                // 其他Exception错误
                logger.error("other exception : " + e.getMessage());
                return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
            }
        }
        return new ReturnObject(true);



    }

    /**
     * @Created at 12/3 15:28
     * @author zrh
     * @param address
     * @return
     */
    public ReturnObject updateAddressInfo(Address address) {
        AddressPo po=null;
        try{
            po=addressPoMapper.selectByPrimaryKey(address.getId());
            if(po==null){
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            }else if (!po.getCustomerId().equals(address.getCustomer_id())){
                return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }
        }catch (DataAccessException e){
            logger.debug("sql exception : "+e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR,String.format("数据库错误, %s",e.getMessage()));

        }catch (Exception e){
            logger.error("other exception : "+e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR,String.format("其他错误：%s",e.getMessage()));
        }
        AddressPo addressPo = address.getAddressPo();
        ReturnObject returnObject=null;
        AddressPoExample addressPoExample=new AddressPoExample();
        AddressPoExample.Criteria criteria= addressPoExample.createCriteria();
        criteria.andIdEqualTo(address.getId());
        criteria.andCustomerIdEqualTo(address.getCustomer_id());
        try{
            int ret =addressPoMapper.updateByExampleSelective(addressPo,addressPoExample);
            if(ret == 0){
                logger.debug("updateAddress: update address fail: "+addressPo.toString());
                returnObject=new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("地址id不存在："+address.getId()));

            }
            else {
                logger.debug("update address = "+addressPo.toString());
                returnObject =new ReturnObject(ResponseCode.OK);
            }
        }catch (DataAccessException e){
            logger.debug("sql exception : "+e.getMessage());
            returnObject =new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR,String.format("数据库错误, %s",e.getMessage()));

        }catch (Exception e){
            logger.error("other exception : "+e.getMessage());
            returnObject =new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR,String.format("其他错误：%s",e.getMessage()));
        }
        return returnObject;
    }

    /**
     * @Created at 12/3 19:26
     * @author zrh
     * @param id
     * @return
     */
    public ReturnObject deleteAddress(Long id) {

        try{
            int ret=addressPoMapper.deleteByPrimaryKey(id);
            if(ret==0)
            {
                logger.debug("delete address :id is not exist ="+id);
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("地址id不存在："+id));

            }
            else {
                logger.debug("delete address id= " + id);
                return new ReturnObject(ResponseCode.OK);
            }
        }
        catch (DataAccessException e){
            logger.error("delete Address DataAccessException:"+e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR,String.format("数据库错误："+e.getMessage()));

        }
        catch (Exception e)
        {
            logger.error("other exception:"+e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR,String.format("其他错误："+e.getMessage()));
        }


    }


    /**
     * @Created at 12/16 23:30
     * @author zrh
     * @param userId
     * @param id
     * @return
     */
    public ReturnObject setDefaultAddress(Long userId, Long id) {
        AddressPo po=null;
        try{
            po=addressPoMapper.selectByPrimaryKey(id);
            if(po==null){
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);

            }
            if(!userId.equals(po.getCustomerId())){
                return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }
        }catch (DataAccessException e){
            logger.error("delete Address DataAccessException:"+e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR,String.format("数据库错误："+e.getMessage()));

        }
        catch (Exception e)
        {
            logger.error("other exception:"+e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR,String.format("其他错误："+e.getMessage()));
        }
        po.setBeDefault((byte)1);
        try {
            int ret=addressPoMapper.updateByPrimaryKeySelective(po);
            if(ret==0){
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            }

        }catch (DataAccessException e){
            logger.error("delete Address DataAccessException:"+e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR,String.format("数据库错误："+e.getMessage()));

        }
        catch (Exception e)
        {
            logger.error("other exception:"+e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR,String.format("其他错误："+e.getMessage()));
        }
        return new ReturnObject(ResponseCode.OK);
    }
}
