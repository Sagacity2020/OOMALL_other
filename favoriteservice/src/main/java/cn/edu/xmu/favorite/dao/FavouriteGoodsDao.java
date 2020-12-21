package cn.edu.xmu.favorite.dao;
import cn.edu.xmu.favorite.mapper.FavouriteGoodsPoMapper;
import cn.edu.xmu.favorite.model.bo.FavouriteGoods;
import cn.edu.xmu.favorite.model.po.FavouriteGoodsPo;
import cn.edu.xmu.favorite.model.po.FavouriteGoodsPoExample;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
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
public class FavouriteGoodsDao {

    private static final Logger logger = LoggerFactory.getLogger(FavouriteGoodsDao.class);

    @Autowired
    private FavouriteGoodsPoMapper favouriteGoodsPoMapper;

    /**
     * 查看收藏列表
     * @author zwl
     * @param pageNum 页数
     * @param pageSize 每页大小
     * @return
     * @Date:  2020/12/6 21:48
     */
    public PageInfo<FavouriteGoodsPo> getSelfFavouriteGoods(Integer pageNum, Integer pageSize,Long customerId)
    {
        FavouriteGoodsPoExample example = new FavouriteGoodsPoExample();
        FavouriteGoodsPoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(customerId);
        //分页查询
        //PageHelper.startPage(pageNum, pageSize);
        //logger.debug("page = " + pageNum + "pageSize = " + pageSize);
        List<FavouriteGoodsPo> favouriteGoodsPoList =null;
        favouriteGoodsPoList = favouriteGoodsPoMapper.selectByExample(example);
        return new PageInfo<FavouriteGoodsPo>(favouriteGoodsPoList);
//        try {
//            favouriteGoodsPoList = favouriteGoodsPoMapper.selectByExample(example);
//            //logger.info("getUserRoles: userId = "+ id + "roleNum = "+ userRolePoList.size());
//        }catch (DataAccessException e){
//            logger.error("selectAllRole: DataAccessException:" + e.getMessage());
//            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
//        }
//
//        if(favouriteGoodsPoList.isEmpty())
//        {
//            logger.info("收藏列表为空");
//            return new ReturnObject<>(ResponseCode.OK);
//        }

//        List<VoObject> ret = new ArrayList<>(favouriteGoodsPoList.size());
//        for (FavouriteGoodsPo po : favouriteGoodsPoList) {
//            FavouriteGoods favouriteGoods = new FavouriteGoods(po);
//            ret.add(favouriteGoods);
//        }
//        PageInfo<VoObject> favouriteGoodsPage = PageInfo.of(ret);
//        return new ReturnObject<>(favouriteGoodsPage);
    }

    /**
     * 新增收藏
     * @author zwl
     * @param favouriteGoods
     * @return
     * @Date:  2020/12/6 21:49
     */

    public ReturnObject<FavouriteGoods> insertFavouriteGoods(Long customerId,Long goodsSkuId,FavouriteGoods favouriteGoods)
    {
        FavouriteGoodsPo fPo=favouriteGoodsPoMapper.selectByPrimaryKey(favouriteGoods.getGoodsSkuId());

        FavouriteGoodsPoExample example = new FavouriteGoodsPoExample();
        FavouriteGoodsPoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(customerId);
        criteria.andGoodsSkuIdEqualTo(goodsSkuId);
        List<FavouriteGoodsPo> selectList= favouriteGoodsPoMapper.selectByExample(example);
        if(selectList!=null)
        {
            FavouriteGoodsPo retPo=selectList.get(0);
            FavouriteGoods retbo=new FavouriteGoods(retPo);
            return new ReturnObject<>(retbo);
        }
            FavouriteGoodsPo favouriteGoodsPo=favouriteGoods.gotFavouriteGoodsPo();
        ReturnObject<FavouriteGoods> retObj =null;
        try{
            int ret = favouriteGoodsPoMapper.insertSelective(favouriteGoodsPo);
            if(ret==0){
                logger.debug("insert fail");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("收藏商品失败：" + favouriteGoodsPo.getGoodsSkuId()));
            }else{
                logger.info(" insert favouriteGoods = " + favouriteGoodsPo.toString());
                System.out.println(" insert favouriteGoods = " + favouriteGoodsPo.toString());
                favouriteGoods.setId(favouriteGoodsPo.getId());
               return new ReturnObject<>(favouriteGoods);
            }
        }
        catch(DataAccessException e){
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }

        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

//    public ReturnObject<VoObject> insertFavouriteGoods(FavouriteGoods favouriteGoods)
//    {
//        FavouriteGoodsPo favouriteGoodsPo=favouriteGoods.gotFavouriteGoodsPo();
//        ReturnObject<VoObject> retObj =null;
//        try{
//            int ret = favouriteGoodsPoMapper.insertSelective(favouriteGoodsPo);
//            if(ret==0){
//                logger.debug("insert fail");
//                retObj=new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("收藏商品失败：" + favouriteGoodsPo.getGoodsSkuId()));
//            }else{
//                logger.info(" insert favouriteGoods = " + favouriteGoodsPo.toString());
//                System.out.println(" insert favouriteGoods = " + favouriteGoodsPo.toString());
//                favouriteGoods.setId(favouriteGoodsPo.getId());
//                retObj=new ReturnObject<VoObject>(favouriteGoods);
//            }
//        }
//        catch(DataAccessException e){
//            logger.debug("other sql exception : " + e.getMessage());
//            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
//        }
//
//        catch (Exception e) {
//            // 其他Exception错误
//            logger.error("other exception : " + e.getMessage());
//            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
//        }
//        return retObj;
//    }

    /**
     * 取消收藏
     * @author zwl
     * @param id
     * @return
     * @Date:  2020/12/6 21:49
     */
    public ReturnObject<Object> deleteFavouriteGoods(Long customerId,Long id)
    {
        ReturnObject<Object> retObj = null;
        try {
            FavouriteGoodsPo po =favouriteGoodsPoMapper.selectByPrimaryKey(id);
            if(po==null)
            {
                System.out.println("null"+id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("收藏id不存在：" + id));
            }
            if(!(po.getCustomerId().longValue() == customerId.longValue()))
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            int ret = favouriteGoodsPoMapper.deleteByPrimaryKey(id);
            if (ret == 0) {
                logger.debug("deleteFavouriteGoods: id not exist = " + id);
                System.out.println("deleteFavouriteGoods: id not exist = " + id);
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("收藏id不存在：" + id));
            } else {
                retObj= new ReturnObject(ResponseCode.OK);
            }
        }catch (DataAccessException e){
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }

        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return  retObj;
    }
}
