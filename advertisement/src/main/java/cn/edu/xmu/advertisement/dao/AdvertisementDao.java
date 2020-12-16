package cn.edu.xmu.advertisement.dao;


import cn.edu.xmu.advertisement.mapper.AdvertisementPoMapper;
import cn.edu.xmu.advertisement.model.bo.Advertisement;
import cn.edu.xmu.advertisement.model.po.AdvertisementPo;
import cn.edu.xmu.advertisement.model.po.AdvertisementPoExample;
import cn.edu.xmu.ooad.model.VoObject;
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

@Repository
public class AdvertisementDao {
    private static final Logger logger = LoggerFactory.getLogger(AdvertisementDao.class);


    @Autowired
    private AdvertisementPoMapper advertisementPoMapper;


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



    public ReturnObject<PageInfo<AdvertisementPo>>getAdvertisementBySegId(Long id,Integer page,Integer pageSize){
        AdvertisementPoExample example=new AdvertisementPoExample();
        AdvertisementPoExample.Criteria criteria=example.createCriteria();
        criteria.andSegIdEqualTo(id);

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
}
