package cn.edu.xmu.share.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;

import cn.edu.xmu.share.mapper.BeSharePoMapper;
import cn.edu.xmu.share.model.bo.BeShare;
import cn.edu.xmu.share.model.po.BeSharePo;
import cn.edu.xmu.share.model.po.BeSharePoExample;
import cn.edu.xmu.share.model.po.SharePo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Repository
public class BeShareDao {
    @Autowired
    private BeSharePoMapper beSharePoMapper;

    @Autowired
    private ShareDao shareDao;

    @Autowired
    private ShareActivityDao shareActivityDao;

    private static final Logger logger = LoggerFactory.getLogger(BeShareDao.class);

    /**
    * 查询beShareId
    *
    * @author zxh
    * @param customerId 被分享者Id
    * @param skuId skuID
    * @return Object
    * @Date 2020/12/13 19:39
    */
    public Long getBeShareId(Long customerId, Long skuId)
    {
        BeSharePoExample example = new BeSharePoExample();
        BeSharePoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(customerId);
        criteria.andGoodsSkuIdEqualTo(skuId);
        List<BeSharePo> retBeSharePo = beSharePoMapper.selectByExample(example);
        Collections.sort(retBeSharePo, new Comparator<BeSharePo>() {
            @Override
            public int compare(BeSharePo o1, BeSharePo o2) {
                if (o1.getGmtCreate().isAfter(o2.getGmtCreate())) { //变成 < 可以变成递减排序
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        if(retBeSharePo.isEmpty() || retBeSharePo.get(0).getOrderId() != null)
            return 0L;
        else
            return retBeSharePo.get(0).getId();
    }


    /**
     * 浏览分享
     *
     * @author zxh
     * @param customerId 被分享者ID
     * @param skuId 分享商品SPU ID
     * @param sharerId 分享者Id
     * @return ReturnObject<BeShareRetVo> 新建的分享
     */

    //浏览分享
    public ReturnObject createBeShare(Long customerId, Long skuId, Long sharerId)
    {
        BeSharePoExample example = new BeSharePoExample();
        BeSharePoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(customerId);
        criteria.andGoodsSkuIdEqualTo(skuId);
        criteria.andSharerIdEqualTo(sharerId);
        try{
            List<BeSharePo> retBeSharePo = beSharePoMapper.selectByExample(example);
            if(retBeSharePo == null)
            {
                BeSharePo po = new BeSharePo();
                po.setSharerId(sharerId);
                po.setGoodsSkuId(skuId);
                po.setCustomerId(customerId);
                po.setGmtCreate(LocalDateTime.now());
                po.setGmtModified(LocalDateTime.now());
                SharePo sharePo = shareDao.getShare(sharerId,skuId);
                po.setShareId(sharePo.getId());

                /**
                 * 调用为某个商品更新分享活动Id的函数
                 * Long shareActivityId = shareActivityDao.getShareActivity(0L, skuId);
                 * po.setShareActivityId(shareActivityId);
                 */
                po.setShareActivityId(sharePo.getShareActivityId());
                int flag = beSharePoMapper.insertSelective(po);
                if(flag == 0)
                {
                    logger.debug("createBeShare: create beShare fail.");
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新建分享成功失败：" + po.getCustomerId()));
                }
                else
                {
                    logger.debug("createBeShare: create beShare success.");
                    return new ReturnObject<VoObject>(new BeShare(po));
                }
            }
            else
            {
                logger.debug("getBeShare: beShare = " + retBeSharePo.toString());
                return new ReturnObject<VoObject>(new BeShare(retBeSharePo.get(0)));
            }

        }
        catch (DataAccessException e){
            logger.error("createBeShare: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * 用户查询自己的分享成功
     *
     * @author zxh
     * @param sharerId
     * @param pageNum 页数
     * @param pageSize 每页大小
     * @return ReturnObject<List> 分享列表
     */
    public PageInfo<BeShare> getBeShared(Long sharerId, Long goodsSkuId, LocalDateTime beginTime, LocalDateTime endTime, Integer pageNum, Integer pageSize) throws Exception
    {
        BeSharePoExample example = new BeSharePoExample();
        BeSharePoExample.Criteria criteria = example.createCriteria();
        criteria.andSharerIdEqualTo(sharerId);
        if(goodsSkuId != null)
            criteria.andGoodsSkuIdEqualTo(goodsSkuId);
        if(beginTime != null)
            criteria.andGmtCreateGreaterThanOrEqualTo(beginTime);
        if(endTime != null)
            criteria.andGmtCreateLessThanOrEqualTo(endTime);
        //?
        PageHelper.startPage(pageNum, pageSize);
        logger.debug("page = " + pageNum + "pageSize = " + pageSize);
        List<BeSharePo> retBeSharePos = null;

        retBeSharePos = beSharePoMapper.selectByExample(example);
        List<BeShare> ret = new ArrayList<>(retBeSharePos.size());

        for (BeSharePo po : retBeSharePos) {
            BeShare beShare = new BeShare(po);
            ret.add(beShare);
        }
        PageInfo<BeSharePo> beSharePoPage = PageInfo.of(retBeSharePos);
        PageInfo<BeShare> beSharePage = PageInfo.of(ret);
        beSharePage.setPages(beSharePoPage.getPages());
        beSharePage.setTotal(beSharePoPage.getTotal());
        beSharePage.setPageNum(pageNum);
        beSharePage.setPageSize(pageSize);

        return beSharePage;
    }

    /**
    * 根据分享Id返回分享明细
    *
    * @author zxh
    * @param shareId 分享Id
    * @return List<BeShare>
    * @Date 2020/12/7 0:03
    */
    public List<BeShare> getBeShareByShareId(Long shareId)
    {
        BeSharePoExample example = new BeSharePoExample();
        BeSharePoExample.Criteria criteria = example.createCriteria();
        criteria.andShareIdEqualTo(shareId);
        List<BeSharePo> retBeSharePos =  beSharePoMapper.selectByExample(example);
        List<BeShare> list =  new ArrayList<>();
        if(retBeSharePos != null)
        {
            for(BeSharePo beSharePo : retBeSharePos)
            {
                list.add(new BeShare(beSharePo));
            }
        }

        return list;
    }

    /**
     * 管理员查询自己的分享成功
     *
     * @author zxh
     * @param goosSkuId 店铺的商品列表
     * @param pageNum 页数
     * @param pageSize 每页大小
     * @return ReturnObject<List> 分享列表
     */
    public PageInfo<BeShare> getBeSharedAdmin(Long goosSkuId, LocalDateTime beginTime, LocalDateTime endTime, Integer pageNum, Integer pageSize) throws Exception
    {
        BeSharePoExample example = new BeSharePoExample();
        BeSharePoExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsSkuIdEqualTo(goosSkuId);
        if(beginTime != null)
            criteria.andGmtCreateGreaterThanOrEqualTo(beginTime);
        if(endTime != null)
            criteria.andGmtCreateLessThanOrEqualTo(endTime);

        PageHelper.startPage(pageNum, pageSize);
        logger.debug("page = " + pageNum + "pageSize = " + pageSize);
        List<BeSharePo> retBeSharePos = null;

        retBeSharePos = beSharePoMapper.selectByExample(example);
        List<BeShare> ret = new ArrayList<>(retBeSharePos.size());

        for (BeSharePo po : retBeSharePos) {
            BeShare beShare = new BeShare(po);
            ret.add(beShare);
        }
        PageInfo<BeSharePo> beSharePoPage = PageInfo.of(retBeSharePos);
        PageInfo<BeShare> beSharePage = PageInfo.of(ret);
        beSharePage.setPages(beSharePoPage.getPages());
        beSharePage.setTotal(beSharePoPage.getTotal());
        beSharePage.setPageNum(pageNum);
        beSharePage.setPageSize(pageSize);

        return beSharePage;
    }

    /**
     * 根据shareId更新分享中的数量
     *
     * @author zxh
     * @param beShareId
     * @param orderId
     * @param rebate
     * @return Object
     * @Date 2020/12/7 0:14
     */
    //更新分享成功
    public Boolean updateBeShareById(Long beShareId, Long orderId, Integer rebate) throws Exception {
        try {
            BeSharePo beSharePo = beSharePoMapper.selectByPrimaryKey(beShareId);
            if (beSharePo != null) {
                beSharePo.setOrderId(orderId);
                beSharePo.setRebate(rebate);
                beSharePo.setGmtModified(LocalDateTime.now());
                int flag = beSharePoMapper.updateByPrimaryKeySelective(beSharePo);
                if (flag == 0) {
                    logger.debug("updateBeShare: fail");
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }

        } catch (DataAccessException e) {
            logger.error("updateBeShare: DataAccessException:" + e.getMessage());
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
    * @param beShareId
    * @return BeSharePo
    * @Date 2020/12/15 21:12
    */

    public BeSharePo getBeShareById(Long beShareId)
    {
        try
        {
            return beSharePoMapper.selectByPrimaryKey(beShareId);
        }
        catch (DataAccessException e){
            logger.error("getBeShare: DataAccessException:" + e.getMessage());
            return null;
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return null;
        }
    }

}
