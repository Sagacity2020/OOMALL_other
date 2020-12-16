package cn.edu.xmu.share.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;

import cn.edu.xmu.share.mapper.ShareActivityPoMapper;
import cn.edu.xmu.share.model.bo.ShareActivity;
import cn.edu.xmu.share.model.po.BeSharePo;
import cn.edu.xmu.share.model.po.ShareActivityPo;
import cn.edu.xmu.share.model.po.ShareActivityPoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class ShareActivityDao {

    @Autowired
    private ShareActivityPoMapper shareActivityPoMapper;

    private static final Logger logger = LoggerFactory.getLogger(ShareActivityDao.class);

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
    * 判断一个商品是否可以分享
    *
    * @author zxh
    * @param goodsSkuId
    * @return Boolean
    * @Date 2020/12/14 22:30
    */
    public Boolean isShared(Long shopId, Long goodsSkuId)
    {
        ShareActivity shareActivity = null;
        //平台默认分享活动
        shareActivity = (ShareActivity) redisTemplate.opsForValue().get("p");
        if(shareActivity == null || shareActivity.getBeginTime().isAfter(LocalDateTime.now()) || shareActivity.getEndTime().isBefore(LocalDateTime.now()))
        {
            if(shareActivity != null && shareActivity.getEndTime().isBefore(LocalDateTime.now()))
            {
                //过期删除
                redisTemplate.delete("p");
                ShareActivityPo po = getRecentShareActivity(0L, shareActivity.getGoodsSkuId());
                if(po != null) {
                    redisTemplate.opsForValue().set("p", new ShareActivity(po));
                    if(po.getBeginTime().isBefore(LocalDateTime.now()))
                        return true;
                }
            }
            shareActivity = null;
            //查找店铺的
            shareActivity = (ShareActivity) redisTemplate.opsForValue().get("d" + shopId);
            if(shareActivity == null || shareActivity.getBeginTime().isAfter(LocalDateTime.now()) || shareActivity.getEndTime().isBefore(LocalDateTime.now()))
            {
                if(shareActivity != null && shareActivity.getEndTime().isBefore(LocalDateTime.now()))
                {
                    redisTemplate.delete("d" + shopId);
                    ShareActivityPo po = getRecentShareActivity(shareActivity.getShopId(), 0L);
                    if(po != null) {
                        redisTemplate.opsForValue().set("d" + shopId, new ShareActivity(po));
                        if(po.getBeginTime().isBefore(LocalDateTime.now()))
                            return true;
                    }
                }
                shareActivity = null;
                //查找特定的
                shareActivity = (ShareActivity) redisTemplate.opsForValue().get("t" + goodsSkuId);
                if(shareActivity == null || shareActivity.getBeginTime().isAfter(LocalDateTime.now()) || shareActivity.getEndTime().isBefore(LocalDateTime.now()))
                {
                    if(shareActivity != null && shareActivity.getEndTime().isBefore(LocalDateTime.now()))
                    {
                        redisTemplate.delete("t" + goodsSkuId);
                        ShareActivityPo po = getRecentShareActivity(shareActivity.getShopId(), shareActivity.getGoodsSkuId());
                        if(po != null) {
                            redisTemplate.opsForValue().set("t" + goodsSkuId, new ShareActivity(po));
                            if(po.getBeginTime().isBefore(LocalDateTime.now()))
                                return true;
                        }
                    }
                    return false;
                }
                else
                    return true;
            }
            else
                return true;
        }
        else
            return true;
    }


    /**
     * 用户查询分享活动
     *
     * @author zxh
     * @param pageNum 页数
     * @param pageSize 每页大小
     * @return ReturnObject<List> 分享列表
     */
    public ReturnObject<PageInfo<VoObject>> getShareActivities(Long skuId, Long shopId, Integer pageNum, Integer pageSize)
    {
        ShareActivityPoExample example = new ShareActivityPoExample();
        ShareActivityPoExample.Criteria criteria = example.createCriteria();
        if(shopId != null)
            criteria.andShopIdEqualTo(shopId);
        if(shopId == null)
            criteria.andGoodsSkuIdEqualTo(skuId);
        else if(shopId != null && shopId != 0L && skuId!= null)
            criteria.andGoodsSkuIdEqualTo(skuId);
//        criteria.andEndTimeGreaterThan(LocalDateTime.now());
//        criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());

        PageHelper.startPage(pageNum, pageSize);
        logger.debug("page = " + pageNum + "pageSize = " + pageSize);

        List<ShareActivityPo> retShareActivityPos = null;
        try
        {
            retShareActivityPos = shareActivityPoMapper.selectByExample(example);
            List<VoObject> ret = new ArrayList<>(retShareActivityPos.size());
            if(ret != null && !ret.isEmpty()) {
                for (ShareActivityPo po : retShareActivityPos) {
                    ShareActivity shareActivity = new ShareActivity(po);
                    ret.add(shareActivity);
                }
            }

            PageInfo<ShareActivityPo> shareActivityPoPage = PageInfo.of(retShareActivityPos);
            PageInfo<VoObject> shareActivityPage = PageInfo.of(ret);
            shareActivityPage.setPages(shareActivityPoPage.getPages());
            shareActivityPage.setTotal(shareActivityPoPage.getTotal());
            shareActivityPage.setPageNum(pageNum);
            shareActivityPage.setPageSize(pageSize);

            return new ReturnObject<>(shareActivityPage);
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
     * 新建分享活动
     *
     * @author zxh
     * @param shopId 商铺Id
     * @param skuId 商品spuId
     * @param bo:bo对象
     * @return ReturnObject 新建的分享
     */
    //新建分享活动
    public ReturnObject<VoObject> createShareActivity(Long shopId, Long skuId, ShareActivity bo)
    {
        if(bo.getBeginTime() == null)
            return new ReturnObject<>(ResponseCode.Log_BEGIN_NULL, String.format("开始时间不能为空"));
        if(bo.getEndTime() == null)
            return new ReturnObject<>(ResponseCode.Log_END_NULL, String.format("结束时间不能为空"));
        if(bo.getBeginTime().isAfter(bo.getEndTime()))
            return new ReturnObject<>(ResponseCode.Log_Bigger, String.format("开始时间大于结束时间"));
        try{
            ShareActivityPo po = new ShareActivityPo();
            po.setShopId(shopId);
            po.setGoodsSkuId(skuId);
            po.setBeginTime(bo.getBeginTime());
            po.setEndTime(bo.getEndTime());
            po.setState((byte) 0);
            po.setStrategy(bo.getStrategy());
            po.setGmtCreate(LocalDateTime.now());
            po.setGmtModified(LocalDateTime.now());
            int flag = shareActivityPoMapper.insert(po);
            if(flag == 0)
            {
                logger.debug("createShareActivity: create shareActivity fail  shopId= " + shopId +"goodsSkuId= " + skuId);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新建分享活动失败"));
            }
            else
            {
                logger.debug("createShareActivity: create shareActivity success  shopId= " + shopId +"goodsSkuId= " + skuId);
                return new ReturnObject<VoObject>(new ShareActivity(po));
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
     * 修改分享活动
     *
     * @author zxh
     * @param shopId 商铺Id
     * @param shareActivityId 分享活动Id
     * @param bo:bo对象
     * @return ReturnObject
     */
    //修改分享活动
    public ReturnObject putShareActivity(Long shopId, Long shareActivityId, ShareActivity bo)
    {
        try{

            ShareActivityPo po = shareActivityPoMapper.selectByPrimaryKey(shareActivityId);
            if(po == null)
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            if(po.getShopId() != shopId)
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该分享活动不属于该商铺");
            //logger.debug("hhhhhhh "+shareActivityId);
            //分享活动已生效或者未设置
            if(po.getState()!=null && po.getState() == (byte)1)
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            if(bo.getBeginTime() != null)
                po.setBeginTime(bo.getBeginTime());
            if(bo.getEndTime() != null)
                po.setEndTime(bo.getEndTime());
            if(bo.getStrategy() != null)
                po.setStrategy(bo.getStrategy());
            if(po.getBeginTime().isAfter(po.getEndTime()))
                return new ReturnObject<>(ResponseCode.Log_Bigger, String.format("开始时间大于结束时间"));
            po.setGmtModified(LocalDateTime.now());
            int flag = shareActivityPoMapper.updateByPrimaryKeySelective(po);
            if(flag == 0)
            {
                logger.info("putShareActivity: put shareActivity fail  shopId= " + shopId +" shareActivityId = " + shareActivityId);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("修改分享活动失败"));
            }
            else
            {
                logger.info("putShareActivity: put shareActivity success  shopId= " + shopId +" shareActivityId = " + shareActivityId);
                return new ReturnObject<>(ResponseCode.OK);
            }
        }
        catch (DataAccessException e){
            logger.error("putShareActivity: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    /**
    *查询一个商品最近的分享活动
    *
    * @author zxh
    * @param shopId
    * @param skuId
    * @return ShareActivityPo
    * @Date 2020/12/14 23:20
    */
    public ShareActivityPo getRecentShareActivity(Long shopId , Long skuId)
    {
        //查询最近的分享活动
        ShareActivityPoExample example = new ShareActivityPoExample();
        ShareActivityPoExample.Criteria criteria = example.createCriteria();
        criteria.andStateEqualTo((byte) 1);
        criteria.andEndTimeGreaterThan(LocalDateTime.now());
        criteria.andShopIdEqualTo(shopId);
        if(shopId != 0L){
            criteria.andGoodsSkuIdEqualTo(skuId);
        }
        List<ShareActivityPo> retShareActivityPos = shareActivityPoMapper.selectByExample(example);
        Collections.sort(retShareActivityPos, new Comparator<ShareActivityPo>() {
            @Override
            public int compare(ShareActivityPo o1, ShareActivityPo o2) {
                if (o1.getBeginTime().isAfter(o2.getBeginTime())) { //变成 < 可以变成递减排序
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        if(retShareActivityPos != null && !retShareActivityPos.isEmpty())
            return retShareActivityPos.get(0);
        else
            return null;
    }

    /**
     * 下线分享活动
     *
     * @author zxh
     * @param shopId 商铺Id
     * @param shareActivityId 分享活动Id
     * @return ReturnObject
     */
    //下线分享活动
    public ReturnObject<VoObject> deleteShareActivity(Long shopId, Long shareActivityId)
    {

        try{
            ShareActivityPo po = shareActivityPoMapper.selectByPrimaryKey(shareActivityId);
            if(po == null)
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"分享活动Id不存在");
            if(po.getShopId() != shopId)
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该分享活动不属于该商铺");
//            //改错误码
//            if(po.getState() != null && po.getState() == (byte) 0)
//                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"该分享活动已经下线");
            po.setState((byte) 0);
            po.setGmtModified(LocalDateTime.now());
            //在数据库删除
            int flag = shareActivityPoMapper.updateByPrimaryKeySelective(po);
            if(flag == 0)
            {
                logger.info("deleteShareActivity: delete shareActivity fail  shopId= " + shopId +" shareActivityId = " + shareActivityId);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("下线分享活动失败"));
            }
            else
            {
                if(po.getEndTime().isAfter(LocalDateTime.now()))
                {
                    //特定分享活动
                    if(po.getShopId() != 0L && po.getGoodsSkuId() != 0L) {
                        ShareActivityPo temp = getRecentShareActivity(shopId, po.getGoodsSkuId());
                        redisTemplate.delete("t"+po.getGoodsSkuId());
                        if(temp != null)
                        redisTemplate.opsForValue().set("t"+po.getGoodsSkuId(), new ShareActivity(temp));
                    }
                    //店铺默认分享活动
                    else if(po.getShopId() != 0L && po.getGoodsSkuId() == 0L) {
                        ShareActivityPo temp = getRecentShareActivity(shopId, 0L);
                        redisTemplate.delete("d"+po.getShopId());
                        if(temp != null)
                            redisTemplate.opsForValue().set("d"+po.getShopId(), new ShareActivity(temp));
                    } else {
                        //平台默认分享活动
                        ShareActivityPo temp = getRecentShareActivity(0L, po.getGoodsSkuId());
                        redisTemplate.delete("p");
                        if(temp != null)
                            redisTemplate.opsForValue().set("p", new ShareActivity(temp));
                    }
                }
                logger.info("deleteShareActivity: delete shareActivity success  shopId= " + shopId +" shareActivityId = " + shareActivityId);
                return new ReturnObject<>();
            }
        }
        catch (DataAccessException e){
            logger.error("deleteShareActivity: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    /**
    * 判断是否有冲突的分享活动
    *
    * @author zxh
    * @param po
    * @return Boolean
    * @Date 2020/12/14 22:34
    */
    public Boolean isConflict(ShareActivityPo po)
    {
        ShareActivityPoExample example = new ShareActivityPoExample();
        ShareActivityPoExample.Criteria criteria = example.createCriteria();
        criteria.andStateEqualTo((byte) 1);
        criteria.andShopIdEqualTo(po.getShopId());
        if(po.getShopId() != 0L)
            criteria.andGoodsSkuIdEqualTo(po.getGoodsSkuId());
        List<ShareActivityPo> retShareActivityPos = shareActivityPoMapper.selectByExample(example);
        if(retShareActivityPos != null && !retShareActivityPos.isEmpty())
        {
            for(ShareActivityPo retpo : retShareActivityPos)
            {
                if(retpo.getEndTime().isBefore(po.getBeginTime()) || retpo.getBeginTime().isAfter(po.getEndTime()))
                    continue;
                else
                    return true;
            }
        }
        return false;
    }

    /**
     * 上线分享活动
     *
     * @author zxh
     * @param shopId 商铺Id
     * @param shareActivityId 分享活动Id
     * @return ReturnObject
     */
    //上线分享活动
    public ReturnObject<VoObject> putShareActivityState(Long shopId, Long shareActivityId)
    {

        try{
            ShareActivityPo po = shareActivityPoMapper.selectByPrimaryKey(shareActivityId);
            if(po == null)
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"分享活动Id不存在");
            if(po.getShopId() != shopId)
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"该分享活动不属于该商铺");
//            //改错误码
//            if(po.getState() != null && po.getState() == (byte) 1)
//                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"该分享活动已经上线");
            //查询是否有冲突的分享活动
            if(isConflict(po))
                return new ReturnObject<>(ResponseCode.SHAREACT_CONFLICT,"分享活动时段冲突");
            po.setState((byte) 1);
            po.setGmtModified(LocalDateTime.now());
            int flag = shareActivityPoMapper.updateByPrimaryKeySelective(po);
            if(flag == 0)
            {
                logger.info("deleteShareActivity: delete shareActivity fail  shopId= " + shopId +" shareActivityId = " + shareActivityId);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("删除分享活动失败"));
            }
            else
            {
                ShareActivityPo temp = getRecentShareActivity(po.getShopId(), po.getGoodsSkuId());
                //查找是否存在冲突并更新redis
                if(po.getGoodsSkuId() == 0L && po.getShopId() != 0L) {
                    if(temp != null)
                        redisTemplate.opsForValue().set("d"+temp.getShopId(), new ShareActivity(temp));
                } else if(shopId == 0) {
                    if(temp != null)
                    {
                        redisTemplate.opsForValue().set("p", new ShareActivity(temp));
                    }
                } else {
                    if(temp != null)
                    {
                        redisTemplate.opsForValue().set("t"+temp.getGoodsSkuId(), new ShareActivity(temp));
                    }
                }
                logger.info("deleteShareActivity: delete shareActivity success  shopId= " + shopId +" shareActivityId = " + shareActivityId);
                return new ReturnObject<>();
            }
        }
        catch (DataAccessException e){
            logger.error("deleteShareActivity: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }


    /**
     * 使用主键查询分享活动
     *
     * @author zxh
     * @param shareActivityId 分享活动Id
     * @return ReturnObject
     */
    //查询分享活动
    public ShareActivity getShareActivityById(Long shareActivityId) throws Exception
    {

        try{
            ShareActivityPo po = shareActivityPoMapper.selectByPrimaryKey(shareActivityId);
            return new ShareActivity(po);
        }
        catch (DataAccessException e){
            logger.error("getShareActivityById: DataAccessException:" + e.getMessage());
            throw e;
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            throw e;
        }
    }

    /**
     * 使用商品Id获得分享活动Id
     * @Author zxh
     * @param goodId 商品Id
     * @return actId 分享活动Id
     */
    public Long getShareActivity(Long shopId, Long goodId)
    {
        //1:特定 2：商铺 3：平台
        ShareActivityPo shareActivityPo = null;
        //特定分享活动
        shareActivityPo = getRecentShareActivity(shopId, goodId);
        if(shareActivityPo != null && shareActivityPo.getBeginTime().isBefore(LocalDateTime.now()))
            return shareActivityPo.getId();
        else
        {
            //店铺的分享活动
            shareActivityPo = getRecentShareActivity(shopId, 0L);
            if(shareActivityPo != null && shareActivityPo.getBeginTime().isBefore(LocalDateTime.now()))
                return shareActivityPo.getId();
            else
            {
                shareActivityPo = getRecentShareActivity(0L, goodId);
                if(shareActivityPo != null && shareActivityPo.getBeginTime().isBefore(LocalDateTime.now()))
                    return shareActivityPo.getId();
                else
                    return 0L;
            }
        }
    }


    /**
     * 删除
     * 使用商品Id获得分享活动Id  特定分享活动
     * @Author zxh
     *
     * @return 分享活动Id
     */
    public Long getShareActivity1(int flag, Long shopId, Long goodId)
    {
        ShareActivityPoExample example = new ShareActivityPoExample();
        ShareActivityPoExample.Criteria criteria = example.createCriteria();
        if(flag != 2) //不是查商铺默认分享活动
            criteria.andGoodsSkuIdEqualTo(goodId);
        else
            criteria.andGoodsSkuIdEqualTo(0L);
        //criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
        //criteria.andEndTimeGreaterThanOrEqualTo(LocalDateTime.now());
        criteria.andStateEqualTo((byte) 1);
        if(flag == 1)
            criteria.andShopIdNotEqualTo(0L);
        else if(flag == 2)
            criteria.andShopIdEqualTo(shopId);
        else
            criteria.andShopIdEqualTo(0L);
        List<ShareActivityPo> retShareActivityPos = null;

        retShareActivityPos = shareActivityPoMapper.selectByExample(example);
        if(retShareActivityPos != null && !retShareActivityPos.isEmpty())
        {
            return retShareActivityPos.get(0).getId();
        }
        else
            return 0L;
    }

    /**
    * 初始化、将分享活动全部放入Redis
    *
    * @author zxh
    * @return Object
    * @Date 2020/12/14 20:34
    */
    public void initialize() throws Exception {
        Boolean a = redisTemplate.delete("e");
        System.out.println(a+ "*****************");
        //将上线的分享活动放入redis
        ShareActivityPoExample example = new ShareActivityPoExample();
        ShareActivityPoExample.Criteria criteria = example.createCriteria();
        criteria.andStateEqualTo((byte) 1);
        criteria.andEndTimeGreaterThan(LocalDateTime.now());
        List<ShareActivityPo> retShareActivityPos = shareActivityPoMapper.selectByExample(example);
        Collections.sort(retShareActivityPos, new Comparator<ShareActivityPo>() {
            @Override
            public int compare(ShareActivityPo o1, ShareActivityPo o2) {
                if (o1.getBeginTime().isAfter(o2.getBeginTime())) { //变成 < 可以变成递减排序
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        if(retShareActivityPos != null && !retShareActivityPos.isEmpty())
        {
            for(ShareActivityPo po : retShareActivityPos)
            {
                //特定分享活动
                if(po.getShopId() != 0L && po.getGoodsSkuId() != 0L) {
                    ShareActivity shareActivity = (ShareActivity) redisTemplate.opsForValue().get("t"+po.getGoodsSkuId());
                    if(shareActivity == null)
                        redisTemplate.opsForValue().set("t"+po.getGoodsSkuId(), new ShareActivity(po));
                }
                //店铺默认分享活动
                else if(po.getShopId() != 0L && po.getGoodsSkuId() == 0L) {
                    ShareActivity shareActivity = (ShareActivity) redisTemplate.opsForValue().get("d"+po.getShopId());
                    if(shareActivity == null)
                        redisTemplate.opsForValue().set("d"+po.getShopId(), new ShareActivity(po));
                } else {
                    //平台默认分享活动
                    ShareActivity shareActivity = (ShareActivity) redisTemplate.opsForValue().get("p");
                    if(shareActivity == null)
                        redisTemplate.opsForValue().set("p", new ShareActivity(po));
                }
            }
        }
    }

}
