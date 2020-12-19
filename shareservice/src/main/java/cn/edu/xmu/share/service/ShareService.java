package cn.edu.xmu.share.service;

import cn.edu.xmu.goods.dto.GoodsSkuDTO;
import cn.edu.xmu.goods.service.GoodsServiceInterface;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;

import cn.edu.xmu.share.dao.BeShareDao;
import cn.edu.xmu.share.dao.ShareActivityDao;
import cn.edu.xmu.share.dao.ShareDao;
import cn.edu.xmu.share.model.bo.*;
import cn.edu.xmu.share.model.vo.ShareActivityVo;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service()
public class ShareService {
    private Logger logger = LoggerFactory.getLogger(ShareService.class);

    @Autowired
    private ShareDao shareDao;

    @Autowired
    private BeShareDao beShareDao;

    @Autowired
    private ShareActivityDao shareActivityDao;

    @DubboReference(version = "0.0.1")
    GoodsServiceInterface goodsServiceInterface;
    /**
    * 查询分享明细Id
    *
    * @author zxh
    * @param
    * @return Object
    * @Date 2020/12/13 19:36
    */
    public Long getBeShareId(Long customerId, Long skuId)
    {
        return beShareDao.getBeShareId(customerId, skuId);
    }


    /**
     * 用户查询自己的分享
     *
     * @author zxh
     * @param pageNum  页数
     * @param pageSize 每页大小
     * @return ReturnObject<PageInfo < VoObject>> 分页返回分享信息
     */
    public ReturnObject<PageInfo<VoObject>> getShares(Long sharerId, Long goodsSpuId, LocalDateTime beginTime, LocalDateTime endTime, Integer pageNum, Integer pageSize) {
        try
        {
            PageInfo<Share> sharePage = shareDao.getShares(sharerId, goodsSpuId, beginTime, endTime, pageNum, pageSize);
            List<Share> retShare = sharePage.getList();
            List<VoObject> ret = new ArrayList<>(retShare.size());
            for(Share share : retShare)
            {
                share.setGoodSkuVo(goodsServiceInterface.getSkuById(share.getGoodsSkuId()));
                ret.add(share);
            }
            PageInfo<VoObject> retPage = PageInfo.of(ret);
            retPage.setPages(sharePage.getPages());
            retPage.setTotal(sharePage.getTotal());
            retPage.setPageNum(sharePage.getPageNum());
            retPage.setPageSize(sharePage.getPageSize());

            return new ReturnObject<>(retPage);
        }
        catch (DataAccessException e){
            logger.error("getShares: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * 管理员查询分享记录
     *
     * @author zxh
     * @param pageNum  页数
     * @param pageSize 每页大小
     * @return ReturnObject<PageInfo < VoObject>> 分页返回分享信息
     */
    public ReturnObject<PageInfo<VoObject>> getSharesAdmin(Long shopId, Long goodsSkuId, Integer pageNum, Integer pageSize) {
        try
        {
            if(!goodsServiceInterface.hasGoodsSku(goodsSkuId))
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "skuId不存在");
            if(shopId != 0L)
            {
                Long temp = goodsServiceInterface.getShopIdBySkuId(goodsSkuId);
                if(temp.longValue() != shopId.longValue())
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, "该商品不属于该商店");
            }
            PageInfo<Share> sharePage = shareDao.getSharesAdmin(goodsSkuId,  pageNum, pageSize);
            List<Share> retShare = sharePage.getList();
            List<VoObject> ret = new ArrayList<>(retShare.size());
            for(Share share : retShare)
            {
                share.setGoodSkuVo(goodsServiceInterface.getSkuById(share.getGoodsSkuId()));
                ret.add(share);
            }
            PageInfo<VoObject> retPage = PageInfo.of(ret);
            retPage.setPages(sharePage.getPages());
            retPage.setTotal(sharePage.getTotal());
            retPage.setPageNum(sharePage.getPageNum());
            retPage.setPageSize(sharePage.getPageSize());

            return new ReturnObject<>(retPage);
        }
        catch (DataAccessException e){
            logger.error("getShares: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * 新建分享
     *
     * @param sharerId 分享者Id
     * @param skuId 商品Id
     * @return ReturnObject
     * @author zxh
     */
    @Transactional
    public ReturnObject<VoObject> createShare(Long sharerId, Long skuId)
    {
        if(!goodsServiceInterface.hasGoodsSku(skuId))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "skuId不存在");
        //调用商品模块的查找商品的api 查询商品简略信息
        //调用ShareActivityDao查询商品的分享活动Id  shopId
        GoodsSkuDTO vo = goodsServiceInterface.getSkuById(skuId);
        if(vo == null)
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新建分享失败：商品Id不存在" + skuId));
        Long shopId = goodsServiceInterface.getShopIdBySkuId(skuId);
        Long shareActivityId = shareActivityDao.getShareActivity(shopId, skuId);
        //Long shareActivityId = 0L;
        return shareDao.createShare(sharerId, skuId, shareActivityId, vo);
    }

    /**
     * 浏览分享
     *
     * @param sharerId 分享者Id
     * @return ReturnObject
     * @author zxh
     */
//    @Transactional
//    public ReturnObject createBeShare(Long customer, Long spuId, Long sharerId)
//    {
//        return beShareDao.createBeShare(customer, spuId, sharerId);
//    }

    /**
     * 管理员查询自己店铺的分享成功
     *
     * @author zxh
     * @param pageNum  页数
     * @param pageSize 每页大小
     * @return ReturnObject<PageInfo < VoObject>> 分页返回分享信息
     */
    public ReturnObject<PageInfo<VoObject>> getBeSharedAdmin(Long shopId, Long goodsSkuId, LocalDateTime beginTime, LocalDateTime endTime, Integer pageNum, Integer pageSize) {
        try
        {
            if(!goodsServiceInterface.hasGoodsSku(goodsSkuId))
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "skuId不存在");
            if(shopId != 0L)
            {
                Long temp = goodsServiceInterface.getShopIdBySkuId(goodsSkuId);
                if(temp.longValue() != shopId.longValue())
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, "该商品不属于该商店");
            }
            PageInfo<BeShare> beSharePage = beShareDao.getBeSharedAdmin( goodsSkuId, beginTime, endTime, pageNum, pageSize);
            List<BeShare> retBeShare = beSharePage.getList();
            List<VoObject> ret = new ArrayList<>(retBeShare.size());
            for(BeShare beShare : retBeShare)
            {
                //调用商品模块
                beShare.setGoodSkuVo(goodsServiceInterface.getSkuById(beShare.getGoodsSkuId()));
                ret.add(beShare);
            }
            PageInfo<VoObject> retPage = PageInfo.of(ret);
            retPage.setPages(beSharePage.getPages());
            retPage.setTotal(beSharePage.getTotal());
            retPage.setPageNum(beSharePage.getPageNum());
            retPage.setPageSize(beSharePage.getPageSize());

            return new ReturnObject<>(retPage);
        }
        catch (DataAccessException e){
            logger.error("getShares: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * 用户查询自己的的分享成功
     *
     * @author zxh
     * @param pageNum  页数
     * @param pageSize 每页大小
     * @return ReturnObject<PageInfo < VoObject>> 分页返回分享信息
     */
    public ReturnObject<PageInfo<VoObject>> getBeShared(Long sharerId, Long goodsSkuId, LocalDateTime beginTime, LocalDateTime endTime, Integer pageNum, Integer pageSize) {
        try
        {
            PageInfo<BeShare> beSharePage = beShareDao.getBeShared(sharerId, goodsSkuId, beginTime, endTime, pageNum, pageSize);
            List<BeShare> retBeShare = beSharePage.getList();
            List<VoObject> ret = new ArrayList<>(retBeShare.size());
            for(BeShare beShare : retBeShare)
            {
                //调用商品模块
                beShare.setGoodSkuVo(goodsServiceInterface.getSkuById(beShare.getGoodsSkuId()));
                ret.add(beShare);
            }
            PageInfo<VoObject> retPage = PageInfo.of(ret);
            retPage.setPages(beSharePage.getPages());
            retPage.setTotal(beSharePage.getTotal());
            retPage.setPageNum(beSharePage.getPageNum());
            retPage.setPageSize(beSharePage.getPageSize());

            return new ReturnObject<>(retPage);
        }
        catch (DataAccessException e){
            logger.error("getShares: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }


    /**
     * 查询分享活动
     *
     * @param pageNum  页数
     * @param pageSize 每页大小
     * @return ReturnObject
     * @author zxh
     */
    @Transactional
    public ReturnObject<PageInfo<VoObject>> getShareActivities(Long skuId, Long shopId, Integer pageNum, Integer pageSize)
    {
        return shareActivityDao.getShareActivities(skuId, shopId, pageNum, pageSize);
    }

    /**
     * 新建分享活动
     * @param shopId 商铺Id
     * @param skuId 商品skuId
     * @param vo:vo对象
     * @return ReturnObject
     * @author zxh
     */
    @Transactional
    public ReturnObject createShareActivity(Long shopId, Long skuId, ShareActivityVo vo)
    {
        if(!goodsServiceInterface.hasGoodsSku(skuId))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "skuId不存在");
        if(shopId != 0L)
        {
            Long temp = goodsServiceInterface.getShopIdBySkuId(skuId);
            if(temp.longValue() != shopId.longValue())
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, "该商品不属于该商店");
        }
        ShareActivity bo = new ShareActivity(vo);
        return shareActivityDao.createShareActivity(shopId, skuId, bo);
    }

    /**
     * 修改分享活动
     * @param shopId 商铺Id
     * @param shareActivityId 分享活动Id
     * @param vo:vo对象
     * @return ReturnObject
     * @author zxh
     */
    @Transactional
    public ReturnObject putShareActivity(Long shopId, Long shareActivityId, ShareActivityVo vo)
    {
        ShareActivity bo = new ShareActivity(vo);
        return shareActivityDao.putShareActivity(shopId, shareActivityId, bo);
    }

    /**
     * 删除分享活动
     * @param shopId 商铺Id
     * @param shareActivityId 分享活动Id
     * @return ReturnObject
     * @author zxh
     */
    /**
    @Transactional
    public ReturnObject<ShareActivityRetVo> deleteShareActivity(Long shopId, Long shareActivityId)
    {
        ReturnObject<ShareActivityRetVo> ret = shareActivityDao.deleteShareActivity(shopId, shareActivityId);
        ShareActivityRetVo vo;
        if(ret.getData() != null)
            vo = ret.getData();
        else
            return ret;
        if(vo.getState() == 0)
            return ret;
        /**调用商品模块的api,查询分享id为shareActivityId的商品 goodsSpuId shopId
        //直接返回
        List<VoObject> goodsId = new ArrayList<>();

        //对每一个商品Id，为其填写新的分享活动Id
        for(GoodSkuVo id : goodsId)
        {
            //查找现在可用的分享活动
            Long actId = shareActivityDao.getShareActivity(shopId, goodSkuId);

            //调用商品模块的Api，修改商品的分享活动Id


        }

        try{
            //调用自己模块的Api,找出分享中分享活动Id为shareActivityId的分享
            //根据分享活动Id查找分享
            List<Share> share = shareDao.getShareIdByShareActivityId(shareActivityId);
            if(share != null)
            {
                for(Share i : share)
                {
                    //调用商品模块 用share中的skuId，查询所对应的店铺Id
                    Long shopid = 0L;
                    //查找该商品所对应的分享活动
                    Long actId = shareActivityDao.getShareActivity(shopid, i.getGoodsSpuId());
                    //x修改数据库
                    //修改分享的Id
                    i.setShareActivityId(actId);
                    //将i更新数据库
                    //查找分享明细并更新分享活动的Id
                    List<BeShare> beshare = beShareDao.getBeShareByShareId(i.getId());
                    if(beshare != null)
                    {
                        for(BeShare j : beshare)
                        {
                            j.setShareActivityId(shareActivityId);
                            //修改数据库
                        }
                    }

                }
            }
        }
        catch (DataAccessException e){
            logger.error("getShares: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }


        return ret;
    }
    */


    /**
     * 删除分享活动
     * @param shopId 商铺Id
     * @param shareActivityId 分享活动Id
     * @return ReturnObject
     * @author zxh
     */
    @Transactional
    public ReturnObject deleteShareActivity(Long shopId, Long shareActivityId)
    {

        return shareActivityDao.deleteShareActivity(shopId, shareActivityId);
    }


    /**
     * 管理员 上线分享活动
     * @param shopId 商铺Id
     * @param shareActivityId 分享活动Id
     * @return ReturnObject
     * @author zxh
     */
    @Transactional
    public ReturnObject putShareActivityState(Long shopId, Long shareActivityId)
    {
        return shareActivityDao.putShareActivityState(shopId, shareActivityId);
    }

}
