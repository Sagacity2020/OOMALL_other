package cn.edu.xmu.favorite.service;

import cn.edu.xmu.favorite.dao.FavouriteGoodsDao;
import cn.edu.xmu.favorite.model.bo.FavouriteGoods;
import cn.edu.xmu.favorite.model.po.FavouriteGoodsPo;

import cn.edu.xmu.favorite.model.vo.FavouriteGoodsRetVo;
import cn.edu.xmu.goods.dto.GoodsSkuDTO;
import cn.edu.xmu.goods.service.GoodsServiceInterface;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FavouriteGoodsService {
    private Logger logger = LoggerFactory.getLogger(FavouriteGoodsService.class);
    @Autowired
    private FavouriteGoodsDao favouriteGoodsDao;

    @DubboReference(version = "0.0.1")
    GoodsServiceInterface iGoodsService;

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
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<FavouriteGoodsPo> favouriteGoodsPos = favouriteGoodsDao.getSelfFavouriteGoods(pageNum,pageSize,customerId);
        System.out.println(favouriteGoodsPos);
        List<VoObject> favouriteGoodsList= new ArrayList<>();
        for (FavouriteGoodsPo po:favouriteGoodsPos.getList()) {
            FavouriteGoods favouriteGoods=new FavouriteGoods(po);
            GoodsSkuDTO goodsSku=iGoodsService.getSkuById(po.getGoodsSkuId());
            favouriteGoods.setGoodsSku(goodsSku);
            favouriteGoodsList.add(favouriteGoods);
        }

        System.out.println("goods"+favouriteGoodsList);
        PageInfo<VoObject> returnObject= PageInfo.of(favouriteGoodsList);
        returnObject.setPages( favouriteGoodsPos.getPages());
        returnObject.setPageNum( favouriteGoodsPos.getPageNum());
        returnObject.setPageSize( favouriteGoodsPos.getPageSize());
        returnObject.setTotal( favouriteGoodsPos.getTotal());
        return new ReturnObject<>(returnObject);
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
    public ReturnObject insertFavouriteGoods(Long customerId, Long skuId)
    {
        System.out.println("service1");
        //判断skuid是否存在
        if(!iGoodsService.hasGoodsSku(skuId))
        {
            System.out.println("service2");
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("商品id不存在"));
        }

        System.out.println("service3");
        FavouriteGoods favouriteGoods = new FavouriteGoods();
        favouriteGoods.setGoodsSkuId(skuId);
        favouriteGoods.setCustomerId(customerId);
        favouriteGoods.setGmtCreate(LocalDateTime.now());
        ReturnObject<FavouriteGoods> retObj =favouriteGoodsDao.insertFavouriteGoods(favouriteGoods);
        System.out.println("service4"+retObj.toString());
        ReturnObject<VoObject> retFavouriteGoods=null;
        if(retObj.getCode().equals(ResponseCode.OK)){
            FavouriteGoodsRetVo ret= new FavouriteGoodsRetVo(retObj.getData());
            System.out.println("service5"+ret.getId()+ret.getGmtCreate());
            GoodsSkuDTO goodsSku=iGoodsService.getSkuById(skuId);
            System.out.println("service6");
            System.out.println("service6.5"+goodsSku.toString());

//            ret.setGmtCreate(retObj.getData().getGmtCreate());
//            ret.setId(retObj.getData().getId());
                ret.setGoodsSku(goodsSku);
            System.out.println("service7"+ret.toString());
            return new ReturnObject<>(ret);
            //retFavouriteGoods=new ReturnObject<>(retObj.getData());
        }else{
            retFavouriteGoods=new ReturnObject<>(retObj.getCode(),retObj.getErrmsg());
            return  retFavouriteGoods;
        }

    }

    /**
     * 取消收藏
     * @author zwl
     * @param id
     * @return
     * @Date:  2020/12/6 21:47
     */
    public ReturnObject<Object> deleteFavouriteGoods(Long customerId,Long id)
    {
        return favouriteGoodsDao.deleteFavouriteGoods(customerId,id);
    }
}
