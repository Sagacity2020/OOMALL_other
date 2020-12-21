package cn.edu.xmu.address.dao;


import cn.edu.xmu.address.mapper.RegionPoMapper;
import cn.edu.xmu.address.model.bo.Region;
import cn.edu.xmu.address.model.po.RegionPo;
import cn.edu.xmu.address.model.po.RegionPoExample;
import cn.edu.xmu.address.model.vo.RegionVo;
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

    @Autowired
    RegionPoMapper regionPoMapper;

    /**
     * author zrh
     * Created at 11/31 23:52
     * @param address
     * @return
     */

    public ReturnObject<Address> insertAddress(Address address){
        AddressPo addressPo = address.getAddressPo();
        List<AddressPo> retObj;
        AddressPoExample addressPoExample= new AddressPoExample();
        AddressPoExample.Criteria criteria=addressPoExample.createCriteria();
        criteria.andCustomerIdEqualTo(address.getCustomer_id());
        try{
            retObj=addressPoMapper.selectByExample(addressPoExample);

        }
        catch (DataAccessException e){
            logger.debug("sql exception:"+e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR,String.format("数据库错误：%s",e.getMessage()));

        }
        catch (Exception e)
        {
            logger.error("other exception :"+e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR,String.format("严重错误",e.getMessage()));
        }
        int length=retObj.size();
        int maxAddressNum=20;
        if(length==maxAddressNum){
            logger.error("地址簿达到上限");
            return new ReturnObject(ResponseCode.ADDRESS_OUTLIMIT,String.format("地址簿达到上限"));
        }
        if(retObj==null){
            addressPo.setBeDefault((byte)1);
        }

        try{
            int ret= addressPoMapper.insertSelective(addressPo);
            if(ret==0)
            {
                logger.debug("insertAddress: insert address fail "+addressPo.toString());
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("新增失败："+addressPo.getCustomerId()));
            }
            else {
                logger.debug("insertAddree: insert address="+addressPo.toString());
                return new ReturnObject(address);
            }
        }
        catch (DataAccessException e) {

            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }



    }


    /**
     * @Created at 12/1 0:23
     * @author zrh
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject<PageInfo<AddressPo>> selectAllAddress(Long userId, Integer page, Integer pageSize) {
        AddressPoExample example = new AddressPoExample();
        AddressPoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(userId);
        //分页查询

        logger.debug("page = " + page + "pageSize = " + pageSize);
        List<AddressPo> addressPos=null;
        try {
            //不加限定条件查询所有
            addressPos = addressPoMapper.selectByExample(example);
            PageInfo<AddressPo> addressPage=new PageInfo<>(addressPos);
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

    public ReturnObject<Region> queryPreRegion(Long id) {

        RegionPo regionPo=new RegionPo();
        try{
            regionPo=regionPoMapper.selectByPrimaryKey(id);
            logger.debug(regionPo.toString());
            if(regionPo==null){
                logger.debug("地区id不存在："+id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("地区id不存在"));

            }
            if(regionPo.getState()==1)
            {
                logger.debug("该地区无效: "+id);
                return new ReturnObject<>(ResponseCode.REGION_OBSOLETE);
            }
        }catch (DataAccessException e)
        {
            logger.debug("数据库错误："+e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,String.format("数据库错误："+e.getMessage()));
        }
        catch (Exception e)
        {
            logger.debug("服务器其他错误："+e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,String.format("服务器其他错误："+e.getMessage()));
        }
        logger.debug("查询上级地区的地区id："+id);
        RegionPo retRegion=new RegionPo();
        try{
            retRegion=regionPoMapper.selectByPrimaryKey(regionPo.getPid());
            if(regionPo==null){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            if(retRegion.getState()==1){
                return new ReturnObject<>(ResponseCode.REGION_OBSOLETE);
            }

            return new ReturnObject<>(new Region(retRegion));


        }catch (DataAccessException e)
        {
            logger.debug("数据库错误："+e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,String.format("数据库错误："+e.getMessage()));
        }
        catch (Exception e)
        {
            logger.debug("服务器其他错误："+e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,String.format("服务器其他错误："+e.getMessage()));
        }
    }

    /**
     * @Created at 12/8 22:42
     * @author zrh
     * @param id
     * @param vo
     * @return
     */
    public ReturnObject newSubRegion(Long id, RegionVo vo) {
        ReturnObject returnObject;
        logger.debug("id is "+id);
        RegionPo regionPo=null;
        try{
            regionPo=regionPoMapper.selectByPrimaryKey(id);
            logger.debug(regionPo.toString());
            if(regionPo==null){
                logger.debug("地区id不存在："+id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("地区id不存在"));

            }
            if(regionPo.getState()==1)
            {
                logger.debug("该地区无效: "+id);
                return new ReturnObject<>(ResponseCode.REGION_OBSOLETE);
            }
        }catch (DataAccessException e)
        {
            logger.debug("数据库错误："+e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,String.format("数据库错误："+e.getMessage()));
        }
        catch (Exception e)
        {
            logger.debug("服务器其他错误："+e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,String.format("服务器其他错误："+e.getMessage()));
        }
        RegionPo insertRegion=new RegionPo();
        insertRegion.setName(vo.getName());
        insertRegion.setPostalCode(vo.getPostalCode());
        insertRegion.setPid(id);
        insertRegion.setState((byte)0);
        try{
            int ret= regionPoMapper.insertSelective(insertRegion);
            if(ret ==0){
                logger.debug("insertRegion: insert region fail "+insertRegion.toString());
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("新增失败："+insertRegion.getName()));
            }
            else{
                logger.debug("insertRegion: insert region="+insertRegion.toString());
                returnObject= new ReturnObject(ResponseCode.OK);
            }
        }catch (DataAccessException e) {

            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return  new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return returnObject;

    }

    /**
     * @Created at 12/10 16:00
     * @author zrh
     * @param id
     * @return
     */

    public ReturnObject isRegion(Long id) {
        RegionPo regionPo=new RegionPo();

        try{
            regionPo=regionPoMapper.selectByPrimaryKey(id);

            if(regionPo==null){
                logger.debug("地区id不存在："+id);
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("地区id不存在"));

            }else if(regionPo.getState()==1)
            {
                logger.debug("该地区无效: "+id);
                return new ReturnObject<>(ResponseCode.REGION_OBSOLETE);
            }
        }catch (DataAccessException e)
        {
            logger.debug("数据库错误："+e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,String.format("数据库错误："+e.getMessage()));
        }
        catch (Exception e)
        {
            logger.debug("服务器其他错误："+e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,String.format("服务器其他错误："+e.getMessage()));
        }
        return  new ReturnObject<Boolean>(true);
    }

    /**
     * @Created at 12/10 16:00
     * @author zrh
     * @param region
     * @return
     */
    public ReturnObject updateRegion(Region region) {
        logger.error(region.toString());
        RegionPo regionPo=region.getRegionPo();
        logger.error(regionPo.toString());
        RegionPo Po=new RegionPo();

        try{
            Po=regionPoMapper.selectByPrimaryKey(region.getId());

            if(Po==null){
                logger.debug("地区id不存在："+region.getId());
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("地区id不存在"));

            }else if(Po.getState()==1)
            {
                logger.debug("该地区无效: "+region.getId());
                return new ReturnObject<>(ResponseCode.REGION_OBSOLETE);
            }
        }catch (DataAccessException e)
        {
            logger.debug("数据库错误："+e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,String.format("数据库错误："+e.getMessage()));
        }
        catch (Exception e)
        {
            logger.debug("服务器其他错误："+e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,String.format("服务器其他错误："+e.getMessage()));
        }
        ReturnObject returnObject=null;
        try{
            int ret=regionPoMapper.updateByPrimaryKeySelective(regionPo);
            if(ret==0){
                logger.debug("updateRegion: update region fail: "+region.getId());
                returnObject=new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("地区id不存在"));
            }
            else {
                returnObject = new ReturnObject(ResponseCode.OK,String.format("成功"));
            }
        }catch (DataAccessException e){
            logger.debug("sql exception :"+e.getMessage());
            returnObject= new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR,String.format("数据库错误:%s",e.getMessage()));
        }catch (Exception e){
            logger.error("other exception : "+e.getMessage());
            returnObject =new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR,String.format("其他错误：%s",e.getMessage()));
        }
        return returnObject;

    }

    /**
     * @Created at 12/10 21:29
     * @Modified at 12/17 20:56
     * @author zrh
     * @param id
     * @return
     */
    public ReturnObject deleteRegion(Long id) {
        RegionPo Po=new RegionPo();
        try{
            Po=regionPoMapper.selectByPrimaryKey(id);

            if(Po==null){
                logger.debug("地区id不存在："+id);
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("地区id不存在"));

            }else if(Po.getState()==1)
            {
                logger.debug("该地区无效: "+id);
                return new ReturnObject<>(ResponseCode.REGION_OBSOLETE);
            }
        }catch (DataAccessException e)
        {
            logger.debug("数据库错误："+e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,String.format("数据库错误："+e.getMessage()));
        }
        catch (Exception e)
        {
            logger.debug("服务器其他错误："+e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,String.format("服务器其他错误："+e.getMessage()));
        }

        int ret;
        Po.setState((byte)1);
        try {
            ret = regionPoMapper.updateByPrimaryKeySelective(Po);
            if (ret == 0) {
                logger.info("地区不存在或已被删除，id =" + id);
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);

            }
        } catch (DataAccessException e) {
            logger.error("数据库错误： " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            logger.error("严重错误： " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生未知错误： %s", e.getMessage()));
        }
        return new ReturnObject(ResponseCode.OK);
    }

    /**
     * @Create at 12/17 20:56
     * @author zrh
     * @param id
     * @return
     */
    public List<Long> getChildRegion(Long id) {
        RegionPoExample example=new RegionPoExample();
        RegionPoExample.Criteria criteria=example.createCriteria();
        criteria.andPidEqualTo(id);
        List<Long> ids=null;
        List<RegionPo> regionPos=null;
        try {
            regionPos=regionPoMapper.selectByExample(example);
            if(regionPos==null) {
                return null;

            }
            for(int i=0;i<regionPos.size();i++){
                ids.add(regionPos.get(i).getId());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ids;
    }

    /**
     * @Created at 12/17 20:57
     * @author zrh
     * @param childId
     * @return
     */
    public List<Long> deleteChildRegion(Long childId) {
        RegionPo po=regionPoMapper.selectByPrimaryKey(childId);
        List<Long> ids=getChildRegion(childId);
        po.setState((byte)0);
        try{
            int ret = regionPoMapper.updateByPrimaryKeySelective(po);
        }catch (DataAccessException e) {
            logger.error("数据库错误： " + e.getMessage());

        } catch (Exception e) {
            logger.error("严重错误： " + e.getMessage());

        }
        return ids;
    }

    public void deleteChildRegionOnly(Long aLong) {
        RegionPo po=new RegionPo();
        try{
            po=regionPoMapper.selectByPrimaryKey(aLong);
            if(po==null) {
                return;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        po.setState((byte)0);
        try{
            int ret=regionPoMapper.updateByPrimaryKeySelective(po);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ReturnObject<List<Region>> queryParentRegionAll(Long id) {
        RegionPo regionPo=regionPoMapper.selectByPrimaryKey(id);
        List<Region> regions=new ArrayList<>();
        Region region=new Region(regionPo);
        Long ret=region.getPid();
        while(ret!=0){
            RegionPo regionPo1=regionPoMapper.selectByPrimaryKey(ret);
            Region region1=new Region(regionPo1);
            regions.add(region1);
            ret=region1.getPid();
        }
        return new ReturnObject<>(regions);
    }
    public Long getParentRegionIdByChildId(Long regionId){
        try{
            if(regionId != null){
                RegionPo regionPo = regionPoMapper.selectByPrimaryKey(regionId);
                if(regionPo != null && regionPo.getState() == (byte)0){
                    return regionPo.getPid() == null ? 0L : regionPo.getPid();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
