package cn.edu.xmu.advertisement.dao;


import cn.edu.xmu.advertisement.mapper.AdvertisementPoMapper;
import cn.edu.xmu.advertisement.model.bo.Advertisement;
import cn.edu.xmu.advertisement.model.po.AdvertisementPo;
import cn.edu.xmu.advertisement.model.po.AdvertisementPoExample;
import cn.edu.xmu.advertisement.model.vo.AuditAdVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class AdvertisementDao {
    private static final Logger logger = LoggerFactory.getLogger(AdvertisementDao.class);


    @Autowired
    private AdvertisementPoMapper advertisementPoMapper;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    public ReturnObject<List<AdvertisementPo>>getCurrentAdvertisement(LocalDate localDate){
        AdvertisementPoExample example=new AdvertisementPoExample();
        AdvertisementPoExample.Criteria criteria=example.createCriteria();
        criteria.andRepeatsEqualTo((byte)1);
        //criteria.andBeDefaultNotEqualTo((byte)1);
        criteria.andBeDefaultIsNull();
        criteria.andStateEqualTo((byte)4);

        List<AdvertisementPo> advertisementPos=null;
        try{
            advertisementPos=advertisementPoMapper.selectByExample(example);
        }catch (DataAccessException e) {
            logger.error("createAvertisement: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }

        example=new AdvertisementPoExample();
        criteria=example.createCriteria();
        criteria.andBeginDateLessThanOrEqualTo(localDate);
        criteria.andEndDateGreaterThanOrEqualTo(localDate);
        criteria.andRepeatsNotEqualTo((byte)1);
        criteria.andBeDefaultIsNull();
        criteria.andStateEqualTo((byte)4);

        advertisementPos.addAll(advertisementPoMapper.selectByExample(example));

        if(advertisementPos.size()==0){
            example=new AdvertisementPoExample();
            criteria=example.createCriteria();
            criteria.andBeDefaultEqualTo((byte)1);
            criteria.andStateEqualTo((byte)4);
            advertisementPos=advertisementPoMapper.selectByExample(example);
        }

        return new ReturnObject<>(advertisementPos);
    }



    public ReturnObject<Object>createAdvertisement(Advertisement advertisement){
        int count=0;
        AdvertisementPoExample example=new AdvertisementPoExample();
        AdvertisementPoExample.Criteria criteria=example.createCriteria();

        criteria.andSegIdEqualTo(advertisement.getSegId());
        List<AdvertisementPo> advertisementPos;
        try {
            advertisementPos = advertisementPoMapper.selectByExample(example);
        } catch (DataAccessException e) {
            logger.error("createAvertisement: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }


        AdvertisementPo advertisementPo=advertisement.createInsertAdvertisement();

        int ret=advertisementPoMapper.insertSelective(advertisementPo);
        if(ret==0){
            logger.info("新增广告失败");
        }
        else{
            logger.info("新增广告成功");
        }

        return new ReturnObject<>(advertisementPo);

    }




    public ReturnObject<Object>insertAdvertisement(Long tid,Long id){
        AdvertisementPoExample example=new AdvertisementPoExample();
        AdvertisementPoExample.Criteria criteria=example.createCriteria();

        criteria.andSegIdEqualTo(tid);
        List<AdvertisementPo> advertisementPos;
        try {
            advertisementPos = advertisementPoMapper.selectByExample(example);
        } catch (DataAccessException e) {
            logger.error("createAvertisement: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }


        AdvertisementPo advertisementPo=advertisementPoMapper.selectByPrimaryKey(id);
        if(advertisementPo==null){
            logger.info("广告id="+id+"不存在");
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Advertisement advertisement=new Advertisement(advertisementPo);
        AdvertisementPo po=advertisement.createInsertAdvertisement1(tid);

        int ret=advertisementPoMapper.updateByPrimaryKeySelective(po);
        if(ret==0){
            logger.info("将广告id="+id+"添加到时间段tid="+tid+"下失败");
        }
        else{
            logger.info("将广告id="+id+"添加到时间段tid="+tid+"下成功");
        }

        po=advertisementPoMapper.selectByPrimaryKey(id);

        return new ReturnObject<>(po);

    }



    public ReturnObject<PageInfo<AdvertisementPo>>getAdvertisementBySegId(Long id,LocalDate beginDate,LocalDate endDate,Integer page,Integer pageSize){
        AdvertisementPoExample example=new AdvertisementPoExample();
        AdvertisementPoExample.Criteria criteria=example.createCriteria();
        criteria.andSegIdEqualTo(id);

        if(beginDate!=null){
            criteria.andBeginDateEqualTo(beginDate);
        }
        if(endDate!=null){
            criteria.andEndDateEqualTo(endDate);
        }

        List<AdvertisementPo>advertisementPos=null;
        try{
            advertisementPos=advertisementPoMapper.selectByExample(example);

            PageInfo<AdvertisementPo>advertisementPage=new PageInfo<>(advertisementPos);
            return new ReturnObject<>(advertisementPage);
        }catch (DataAccessException e) {
            logger.error("selectAllRole: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    public ReturnObject<Advertisement>getAdvertisementById(Long id){
        AdvertisementPo po=advertisementPoMapper.selectByPrimaryKey(id);
        if(po==null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Advertisement advertisement=new Advertisement(po);
        return new ReturnObject<>(advertisement);
    }


    public ReturnObject updateAdvertisementImage(Advertisement advertisement){
        ReturnObject returnObject=new ReturnObject();

        AdvertisementPo po=new AdvertisementPo();
        po.setId(advertisement.getId());
        po.setImageUrl(advertisement.getImageUrl());

        int ret=advertisementPoMapper.updateByPrimaryKeySelective(po);

        if (ret == 0) {
            logger.debug("updateAdvertisementImage: update fail. advertisement id: " + advertisement.getId());
            returnObject = new ReturnObject(ResponseCode.FIELD_NOTVALID);
        } else {
            logger.debug("updateAdvertisementImage: update advertisement success : " + advertisement.toString());
            returnObject = new ReturnObject();
        }
        return returnObject;
    }

    /**
     * 设置默认广告
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/9 9:19
     */
    public ReturnObject setDefaultAd(Long id)
    {
        AdvertisementPo advertisementPo =null;

        try {
            advertisementPo = advertisementPoMapper.selectByPrimaryKey(id);
            if(advertisementPo==null)
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            //待审核 下线不能设为默认 广告状态禁止
//            if(!(advertisementPo.getState()!=null&&advertisementPo.getState().equals((byte)4)))
//            {
//                return new ReturnObject<>(ResponseCode.ADVERTISEMENT_STATENOTALLOW);
//            }
            //把原来的默认广告设置成非默认
            AdvertisementPoExample example = new AdvertisementPoExample();
            AdvertisementPoExample.Criteria criteria = example.createCriteria();
            criteria.andBeDefaultEqualTo((byte)1);
            List<AdvertisementPo> preDefault =null;
            preDefault = advertisementPoMapper.selectByExample(example);
            if(!preDefault.isEmpty())
            {
                int pret=0;
                for (AdvertisementPo preDefaultAd : preDefault) {
                    preDefaultAd.setBeDefault((byte)0);
                    pret= advertisementPoMapper.updateByPrimaryKeySelective(preDefaultAd);
                }
                if(pret==0)
                {
                    return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
                }
            }
            advertisementPo.setBeDefault((byte) 1);
            int ret=advertisementPoMapper.updateByPrimaryKeySelective(advertisementPo);
            if(ret==0)
            {
                //设置失败
                logger.debug("setDefaultAd fail : " + id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("广告id不存在：" + id));
            }else{
                //成功
                logger.debug("setDefaultAd success" + id);
                return new ReturnObject<>(ResponseCode.OK);
            }
        }catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }


    /**
     * 修改广告内容
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/9 15:18
     */
    public ReturnObject updateAd(Long id, Advertisement bo){
        AdvertisementPo advertisementPo =null;

        try {
            advertisementPo = advertisementPoMapper.selectByPrimaryKey(id);
            if(advertisementPo==null)
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            //默认广告不能修改
//            if(advertisementPo.getBeDefault().equals((byte)1))
//            {
//                return new ReturnObject<>(ResponseCode.ADVERTISEMENT_STATENOTALLOW);
//            }
            if(bo.getBeginDate()!=null) {
                if (bo.getEndDate()!=null) {
                    if(bo.getBeginDate().isAfter(bo.getEndDate()))
                    {return new ReturnObject<>(ResponseCode.Log_Bigger, String.format("开始日期大于结束日期"));}
                }
                else if(bo.getEndDate()==null&&bo.getBeginDate().isAfter(advertisementPo.getEndDate()))
                { return new ReturnObject<>(ResponseCode.Log_Bigger, String.format("开始日期大于结束日期"));}
            }
            else if(bo.getBeginDate()==null)
            {
                if(bo.getEndDate()!=null)
                {
                    if(advertisementPo.getBeginDate().isAfter(bo.getEndDate()))
                    {return new ReturnObject<>(ResponseCode.Log_Bigger, String.format("开始日期大于结束日期"));}
                }
            }
            if(bo.getContent()!=null)
            {advertisementPo.setContent(bo.getContent());}

            if(bo.getBeginDate()!=null)
            {advertisementPo.setBeginDate(bo.getBeginDate());}

            if(bo.getEndDate()!=null)
            {advertisementPo.setEndDate(bo.getEndDate());}

            if(bo.getLink()!=null)
            {advertisementPo.setLink(bo.getLink());}

            if(bo.getWeight()!=null)
            {advertisementPo.setWeight(bo.getWeight());}

            if(bo.getRepeats()!=null)
            {advertisementPo.setRepeats(bo.getRepeats());}

            int ret=advertisementPoMapper.updateByPrimaryKeySelective(advertisementPo);
            if(ret==0)
            {
                //设置失败
                logger.debug("setDefaultAd fail : " + id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("广告id不存在：" + id));
            }else{
                //成功
                //修改了广告内容 变成审核状态
                if(bo.getContent()!=null)
                {
                    advertisementPo.setState((byte)0);
                    advertisementPoMapper.updateByPrimaryKeySelective(advertisementPo);
                }
                logger.debug("setDefaultAd success" + id);
                return new ReturnObject<>(ResponseCode.OK);
            }
        }catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }
    /**
     * 上架广告
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/9 11:34
     */
    public ReturnObject onshelvesAd(Long id){
        AdvertisementPo advertisementPo =null;
        try {
            advertisementPo = advertisementPoMapper.selectByPrimaryKey(id);
            if(advertisementPo==null)
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            //广告禁止--审核状态
            else if(advertisementPo.getState()!=null&&advertisementPo.getState().equals((byte)0))
            {
                return new ReturnObject<>(ResponseCode.ADVERTISEMENT_STATENOTALLOW);
            }
            else{
                advertisementPo.setState((byte)4);
                int ret=advertisementPoMapper.updateByPrimaryKeySelective(advertisementPo);
                //失败
                if(ret==0) {
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("广告id不存在：" + id));
                }
                //成功
                else{
                    if((advertisementPo.getBeginDate().equals(LocalDate.now())||advertisementPo.getBeginDate().isBefore(LocalDate.now()))&&(advertisementPo.getEndDate().equals(LocalDate.now())||advertisementPo.getEndDate().isAfter(LocalDate.now())))
                    {
                        Advertisement adbo=new Advertisement(advertisementPo);
                        String key="ad_"+advertisementPo.getId();
                        String adboJson= JacksonUtil.toJson(adbo);
                        redisTemplate.opsForValue().set(key,adboJson);
                        redisTemplate.expire(key,1, TimeUnit.DAYS);
                    }
                    return new ReturnObject<>(ResponseCode.OK);
                }
            }
        }catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }
    /**
     * 下架广告
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/9 19:03
     */
    public ReturnObject offshelvesAd(Long id){
        AdvertisementPo advertisementPo =null;
        try {
            advertisementPo = advertisementPoMapper.selectByPrimaryKey(id);
            if(advertisementPo==null)
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            //广告禁止--默认广告
//            if(advertisementPo.getBeDefault()!=null&&advertisementPo.getBeDefault().equals((byte)1))
//            {
//                return new ReturnObject<>(ResponseCode.ADVERTISEMENT_STATENOTALLOW);
//            }
            else{
                advertisementPo.setState((byte)6);
                int ret=advertisementPoMapper.updateByPrimaryKeySelective(advertisementPo);
                //失败
                if(ret==0) {
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("广告id不存在：" + id));
                }
                //成功
                else{
                    if((advertisementPo.getBeginDate().equals(LocalDate.now())||advertisementPo.getBeginDate().isBefore(LocalDate.now()))&&(advertisementPo.getEndDate().equals(LocalDate.now())||advertisementPo.getEndDate().isAfter(LocalDate.now())))
                    {
                        //String key="ad_"+advertisementPo.getId();
                        redisTemplate.delete("ad_"+advertisementPo.getId());
                    }
                    return new ReturnObject<>(ResponseCode.OK);
                }
            }
        }catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }
    /**
     * 审核广告
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/10 19:54
     */
    public ReturnObject auditAd(Long id, AuditAdVo auditAdVo){
        AdvertisementPo advertisementPo =null;
        try {
            advertisementPo = advertisementPoMapper.selectByPrimaryKey(id);
            if(advertisementPo==null)
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            //广告状态禁止
            if(advertisementPo.getState()!=null&&advertisementPo.getState().equals((byte)4)||(advertisementPo.getState()!=null&&advertisementPo.getState().equals((byte)6)))
            {
                return new ReturnObject<>(ResponseCode.ADVERTISEMENT_STATENOTALLOW);
            }
            else{
                if(auditAdVo.isConclusion()==true)
                {
                    advertisementPo.setMessage(auditAdVo.getMessage());
                    int ret=advertisementPoMapper.updateByPrimaryKeySelective(advertisementPo);
                    if(ret==0)
                    {
                        return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("广告id不存在：" + id));
                    }
                    else{
                        advertisementPo.setState((byte)6);
                        advertisementPoMapper.updateByPrimaryKeySelective(advertisementPo);
                        return new ReturnObject<>(ResponseCode.OK);
                    }
                }
                return new ReturnObject<>(ResponseCode.OK);
            }
        }catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }

    }

    /**
     * 删除广告
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/9 15:57
     */
    public ReturnObject deleteAd(Long id){
        AdvertisementPo advertisementPo;
        try {
            advertisementPo = advertisementPoMapper.selectByPrimaryKey(id);
            if(advertisementPo==null)
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            //广告禁止--默认广告/上线的广告
            if((advertisementPo.getBeDefault()!=null&&advertisementPo.getBeDefault().equals((byte)1))|| (advertisementPo.getState()!=null&&advertisementPo.getState().equals((byte)4)))
            {
                return new ReturnObject<>(ResponseCode.ADVERTISEMENT_STATENOTALLOW);
            }
            int ret = advertisementPoMapper.deleteByPrimaryKey(id);
            if(ret==0)
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            else{

                return new ReturnObject<>(ResponseCode.OK);
            }
        }catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * 删除时间段 把对应的广告的segId设为0
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/13 17:25
     */
    public ReturnObject updateAdSegId(Long segId){
        AdvertisementPoExample example = new AdvertisementPoExample();
        AdvertisementPoExample.Criteria criteria = example.createCriteria();
        criteria.andSegIdEqualTo(segId);
        List<AdvertisementPo> advertisementPoList =null;
        try {
            advertisementPoList = advertisementPoMapper.selectByExample(example);
            if(!advertisementPoList.isEmpty()) {
                for (AdvertisementPo po : advertisementPoList) {
                    po.setSegId((long) 0);
                    advertisementPoMapper.updateByPrimaryKeySelective(po);
                }
            }
        }catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return new ReturnObject<>(ResponseCode.OK);
    }
}
