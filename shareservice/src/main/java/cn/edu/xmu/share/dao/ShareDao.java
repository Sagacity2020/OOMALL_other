package cn.edu.xmu.share.dao;

import cn.edu.xmu.goods.dto.GoodsSkuDTO;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;

import cn.edu.xmu.share.mapper.SharePoMapper;
import cn.edu.xmu.share.model.bo.Share;
import cn.edu.xmu.share.model.po.SharePo;
import cn.edu.xmu.share.model.po.SharePoExample;
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

@Repository
public class ShareDao {
    @Autowired
    private SharePoMapper sharePoMapper;

    private static final Logger logger = LoggerFactory.getLogger(ShareDao.class);

    public SharePo getShare(Long sharerId, Long goodsSkuId)
    {
        SharePoExample example = new SharePoExample();
        SharePoExample.Criteria criteria = example.createCriteria();
        criteria.andSharerIdEqualTo(sharerId);
        criteria.andGoodsSkuIdEqualTo(goodsSkuId);
        try{
            List<SharePo> sharePoList = sharePoMapper.selectByExample(example);
            return sharePoList.get(0);
        }
        catch (DataAccessException e){
            logger.error("getShares: DataAccessException:" + e.getMessage());
            return null;
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return null;
        }


    }



    //根据分享活动id查询分享
    public List<Share> getShareIdByShareActivityId(Long shareActivityId) throws Exception
    {
        SharePoExample example = new SharePoExample();
        SharePoExample.Criteria criteria = example.createCriteria();
        criteria.andShareActivityIdEqualTo(shareActivityId);
        List<SharePo> sharePoList = new ArrayList<>();
        try
        {
            sharePoList = sharePoMapper.selectByExample(example);
        }
        catch (DataAccessException e){
            logger.error("getShares: DataAccessException:" + e.getMessage());
            throw e;
        }
        catch (Exception e) {
            logger.error("other exception : " + e.getMessage());
            throw e;
        }
        logger.info("getShareIdByShareActivityId: shareActivityId = "+ shareActivityId);
        List<Share> list = new ArrayList<>();
        if(!sharePoList.isEmpty())
        {
            for (SharePo po : sharePoList) {
                list.add(new Share(po));
            }
        }

        return list;
    }

    /**
     * 用户查询自己的分享
     *
     * @author zxh
     * @param sharerId
     * @param pageNum 页数
     * @param pageSize 每页大小
     * @return ReturnObject<List> 分享列表
     */
    public PageInfo<Share> getShares(Long sharerId, Long goodsSpuId, LocalDateTime beginTime, LocalDateTime endTime, Integer pageNum, Integer pageSize) throws Exception
    {
        SharePoExample example = new SharePoExample();
        SharePoExample.Criteria criteria = example.createCriteria();
        criteria.andSharerIdEqualTo(sharerId);
        if(goodsSpuId != null)
            criteria.andGoodsSkuIdEqualTo(goodsSpuId);
        if(beginTime != null)
            criteria.andGmtCreateGreaterThanOrEqualTo(beginTime);
        if(endTime != null)
            criteria.andGmtCreateLessThanOrEqualTo(endTime);
        PageHelper.startPage(pageNum, pageSize);
        logger.debug("page = " + pageNum + "pageSize = " + pageSize);

        List<SharePo> retSharePos = sharePoMapper.selectByExample(example);
        List<Share> ret = new ArrayList<>(retSharePos.size());

        for (SharePo po : retSharePos) {
            Share share = new Share(po);
            ret.add(share);
        }
        PageInfo<SharePo> sharePoPage = PageInfo.of(retSharePos);
        PageInfo<Share> sharePage = PageInfo.of(ret);
        sharePage.setPages(sharePoPage.getPages());
        sharePage.setTotal(sharePoPage.getTotal());
        sharePage.setPageNum(sharePoPage.getPageNum());
        sharePage.setPageSize(sharePoPage.getPageSize());

        return sharePage;
    }



    /**
     * 管理员查询分享记录
     *
     * @author zxh
     * @param goodsSkuId skuId
     * @param pageNum 页数
     * @param pageSize 每页大小
     * @return ReturnObject<List> 分享列表
     */
    public PageInfo<Share> getSharesAdmin(Long goodsSkuId, Integer pageNum, Integer pageSize) throws Exception
    {
        SharePoExample example = new SharePoExample();
        SharePoExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsSkuIdEqualTo(goodsSkuId);
        PageHelper.startPage(pageNum, pageSize);
        logger.debug("page = " + pageNum + "pageSize = " + pageSize);

        List<SharePo> retSharePos = null;
        retSharePos = sharePoMapper.selectByExample(example);
        List<Share> ret = new ArrayList<>(retSharePos.size());

        for (SharePo po : retSharePos) {
            Share share = new Share(po);
            ret.add(share);
        }
        PageInfo<SharePo> sharePoPage = PageInfo.of(retSharePos);
        PageInfo<Share> sharePage = PageInfo.of(ret);
        sharePage.setPages(sharePoPage.getPages());
        sharePage.setTotal(sharePoPage.getTotal());
        sharePage.setPageNum(sharePoPage.getPageNum());
        sharePage.setPageSize(sharePoPage.getPageSize());

        return sharePage;
    }


    /**
     * 新建分享
     *
     * @author zxh
     * @param sharerId 分享者Id
     * @param skuId 商品Id
     * @param shareActivityId 分享活动Id
     * @return ReturnObject<> 新建的分享
     */
    //新建分享
    public ReturnObject<VoObject> createShare(Long sharerId, Long skuId, Long shareActivityId, GoodsSkuDTO vo)
    {
        SharePoExample example = new SharePoExample();
        SharePoExample.Criteria criteria = example.createCriteria();
        criteria.andSharerIdEqualTo(sharerId);
        criteria.andGoodsSkuIdEqualTo(skuId);
        try{
            List<SharePo> retSharePo = sharePoMapper.selectByExample(example);
            if(retSharePo == null || retSharePo.isEmpty())
            {
                SharePo po = new SharePo();
                po.setSharerId(sharerId);
                po.setGoodsSkuId(skuId);
                po.setShareActivityId(shareActivityId);
                po.setQuantity(0);
                po.setGmtCreate(LocalDateTime.now());
                po.setGmtModified(LocalDateTime.now());
                int flag = sharePoMapper.insertSelective(po);;
                if(flag == 0)
                {
                    logger.debug("createShare: create share fail  sharerId= " + po.getSharerId() +"goodsSpuId= " + po.getGoodsSkuId());
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新建分享失败：" + po.getSharerId()));
                }
                else
                {
                    logger.debug("createShare: create share success  sharerId= " + po.getSharerId() +"goodsSpuId= " + po.getGoodsSkuId());
                    Share share= new Share(po);
                    share.setGoodSkuVo(vo);
                    System.out.println(share);

                    return new ReturnObject<VoObject>(share);
                }
            }
            else
            {
                logger.debug("getShare: share = " + sharerId +"goodsSpuId= " + skuId);
                Share share= new Share(retSharePo.get(0));
                share.setGoodSkuVo(vo);
                System.out.println(share);
                return new ReturnObject<VoObject>(share);
            }

        }
        catch (DataAccessException e){
            logger.error("createShare: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    /**
    * 根据shareId更新分享中的数量
    *
    * @author zxh
    * @param shareId
    * @param quantity
    * @return Object
    * @Date 2020/12/7 0:14
    */
    //新建分享
    public Boolean updateShareById(Long shareId, int quantity) throws Exception {
        try {
            SharePo sharePo = sharePoMapper.selectByPrimaryKey(shareId);
            if (sharePo != null) {
                sharePo.setQuantity(quantity);
                sharePo.setGmtModified(LocalDateTime.now());
                int flag = sharePoMapper.updateByPrimaryKeySelective(sharePo);
                if (flag == 0) {
                    logger.debug("updateShare: fail");
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }

        } catch (DataAccessException e) {
            logger.error("updateShare: DataAccessException:" + e.getMessage());
            throw e;

        } catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            throw e;
        }
    }

    /**
     * 通过beShareId查询beShare
     *
     * @author zxh
     * @param shareId
     * @return SharePo
     * @Date 2020/12/15 21:12
     */

    public SharePo getShareById(Long shareId)
    {
        try
        {
            return sharePoMapper.selectByPrimaryKey(shareId);
        }
        catch (DataAccessException e){
            logger.error("getShare: DataAccessException:" + e.getMessage());
            return null;
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return null;
        }
    }
}
