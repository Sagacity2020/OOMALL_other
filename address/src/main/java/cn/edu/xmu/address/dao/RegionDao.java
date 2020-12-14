package cn.edu.xmu.address.dao;


import cn.edu.xmu.address.model.po.RegionPoExample;
import cn.edu.xmu.address.service.RegionService;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.address.mapper.RegionPoMapper;
import cn.edu.xmu.address.model.bo.Region;
import cn.edu.xmu.address.model.po.RegionPo;
import com.fasterxml.jackson.core.PrettyPrinter;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import javax.naming.CompositeName;
import java.util.List;


/**
 * @author zrh
 * @Created at 12/7 0:44
 */
@Repository
public class RegionDao {

    private static final Logger logger= LoggerFactory.getLogger(RegionDao.class);

    @Autowired
    RegionPoMapper regionPoMapper;


    /**
     * @Created at 12/7 1:03
     * @author zrh
     * @param id
     * @return
     */
    public ReturnObject<Region> queryPreRegion(Long id) {
        ReturnObject<Region> regionReturnObject;
        RegionPo regionPo=new RegionPo();
        try{
            regionPo=regionPoMapper.selectByPrimaryKey(id);
            //logger.debug(regionPo.toString());
            if(regionPo==null){
                logger.debug("地区id不存在："+id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("地区id不存在"));

            }else if(regionPo.getState().equals(0))
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
            if(retRegion ==null){
                logger.debug("无上级地区");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("上级地区不存在"));
            }
            else if(retRegion.getState()==0){
                return new ReturnObject<>(ResponseCode.REGION_OBSOLETE);
            }
            else {
                regionReturnObject=new ReturnObject<>(new Region(retRegion));
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

        return regionReturnObject;
    }

    /**
     * @Created at 12/8 22:42
     * @author zrh
     * @param region
     * @return
     */
    public ReturnObject newSubRegion(Region region) {
        ReturnObject returnObject;
        RegionPo regionPo=new RegionPo();
        try{
            regionPo=regionPoMapper.selectByPrimaryKey(region.getId());

            if(regionPo==null){
                logger.debug("地区id不存在："+region.getId());
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("地区id不存在"));

            }else if(regionPo.getState()==0)
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
        RegionPo insertRegion=new RegionPo();
        insertRegion.setName(region.getName());
        insertRegion.setPostalCode(region.getPostalCode());
        insertRegion.setPid(region.getId());
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

            }else if(regionPo.getState()==0)
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
        return  new ReturnObject<Integer>(1);
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

            }else if(Po.getState()==0)
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

            }else if(Po.getState()==0)
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
        ReturnObject returnObject;
        int ret;
        Po.setState((byte)0);
        try {
            ret = regionPoMapper.updateByPrimaryKeySelective(Po);
            if (ret == 0) {
                logger.info("地区不存在或已被删除，id =" + id);
                returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);

            } else {

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
