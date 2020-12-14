package cn.edu.xmu.favorite.service;

import cn.edu.xmu.favorite.dao.FavouriteGoodsDao;
import cn.edu.xmu.favorite.model.bo.FavouriteGoods;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FavouriteGoodsService {
    private Logger logger = LoggerFactory.getLogger(FavouriteGoodsService.class);
    @Autowired
    private FavouriteGoodsDao favouriteGoodsDao;

    /**
     * 查看收藏列表
     * @author zwl
     * @param pageNum 页数
     * @param pageSize 每页大小
     * @return
     * @Date:  2020/12/6 21:42
    */
    public ReturnObject<PageInfo<VoObject>> getSelfFavouriteGoods(Integer pageNum, Integer pageSize,Long customerId)
    {
        ReturnObject<PageInfo<VoObject>> returnObject = favouriteGoodsDao.getSelfFavouriteGoods(pageNum,pageSize,customerId);
        return returnObject;
    }

    /**
     * 收藏商品
     * @author zwl
     * @param customerId
     * @param skuId
     * @return
     * @Date:  2020/12/6 21:47
    */

    @Transactional
    public ReturnObject<VoObject> insertFavouriteGoods(Long customerId, Long skuId)
    {
        FavouriteGoods favouriteGoods = new FavouriteGoods();
        favouriteGoods.setGoodsSpuId(skuId);
        favouriteGoods.setCustomerId(customerId);
        favouriteGoods.setGmtCreate(LocalDateTime.now());
        ReturnObject<FavouriteGoods> retObj =favouriteGoodsDao.insertFavouriteGoods(favouriteGoods);
        ReturnObject<VoObject> retFavouriteGoods=null;
        if(retObj.getCode().equals(ResponseCode.OK)){
            retFavouriteGoods=new ReturnObject<>(retObj.getData());
        }else{
            retFavouriteGoods=new ReturnObject<>(retObj.getCode(),retObj.getErrmsg());
        }
        return  retFavouriteGoods;
    }

    /**
     * 取消收藏
     * @author zwl
     * @param id
     * @return
     * @Date:  2020/12/6 21:47
    */
    public ReturnObject<Object> deleteFavouriteGoods(Long id)
    {
        return favouriteGoodsDao.deleteFavouriteGoods(id);
    }
}
