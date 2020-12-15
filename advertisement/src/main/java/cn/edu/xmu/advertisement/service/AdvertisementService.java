package cn.edu.xmu.advertisement.service;

import cn.edu.xmu.advertisement.dao.AdvertisementDao;
import cn.edu.xmu.advertisement.model.bo.Advertisement;
import cn.edu.xmu.advertisement.model.po.AdvertisementPo;
import cn.edu.xmu.advertisement.model.vo.AdvertisementCreateVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ImgHelper;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.dto.TimeSegmentDTO;
import cn.edu.xmu.other.service.TimeServiceInterface;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class AdvertisementService{
    private Logger logger = LoggerFactory.getLogger(AdvertisementService.class);

    @DubboReference(version = "0.0.1-SNAPSHOT")
    TimeServiceInterface timeServiceInterface;

    @Autowired
    AdvertisementDao advertisementDao;

    @Value("${privilegeservice.dav.username}")
    private String davUsername;

    @Value("${privilegeservice.dav.password}")
    private String davPassword;

    @Value("${privilegeservice.dav.baseUrl}")
    private String baseUrl;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;



    @Transactional
    public ReturnObject<Object>getCurrentAdvertisement(LocalDate localDate, LocalTime localTime){
       /* ReturnObject<List<AdvertisementPo>> returnObject=advertisementDao.getCurrentAdvertisement(localDate);
        List<AdvertisementPo> pos=returnObject.getData();
        */
        int count=0;
        loadAdvertisement();
        List<Advertisement>advertisements=new ArrayList<>();


        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = localDate.format(fmt);

        List<Serializable> lists = redisTemplate.opsForList().range(dateStr,0,-1);
        for(Serializable str:lists){

            JSONObject jsStr = JSONObject.parseObject(str.toString());

            Advertisement advertisement = (Advertisement) JSONObject.toJavaObject(jsStr,Advertisement.class);
            if(advertisement.getSegId()!=null) {
                ReturnObject<TimeSegmentDTO> returnObj = timeServiceInterface.getTimesegmentById(advertisement.getSegId());
                TimeSegmentDTO timeSegmentDTO = returnObj.getData();
                if (timeSegmentDTO.getBeginTime().isBefore(localTime) && timeSegmentDTO.getEndTime().isAfter(localTime)) {
                    advertisements.add(advertisement);
                    count++;
                }
            }
            if(count==8){
                break;
            }

        }
        /*for(AdvertisementPo po:pos){
            if(po.getSegId()!=null) {
                ReturnObject<TimeSegmentDTO> returnObj = iTimeService.getTimesegmentById(po.getSegId());
                TimeSegmentDTO timeSegmentDTO = returnObj.getData();
                if (timeSegmentDTO.getBeginTime().isBefore(localTime) && timeSegmentDTO.getEndTime().isAfter(localTime)) {
                    Advertisement advertisement = new Advertisement(po);
                    advertisements.add(advertisement);
                }
            }
            else {
                Advertisement advertisement = new Advertisement(po);
                advertisements.add(advertisement);
            }
        }
         */
        return new ReturnObject<>(advertisements);
    }



    @Transactional
    public ReturnObject<Object> createAdvertisement(Long id, AdvertisementCreateVo vo){
        Advertisement advertisement=vo.createAdvertisement();
        advertisement.setSegId(id);
        ReturnObject returnObject=advertisementDao.createAdvertisement(advertisement);
        AdvertisementPo po=(AdvertisementPo)returnObject.getData();

        if(po==null){
            return returnObject;
        }
        else {
            Advertisement advertisementBo =new Advertisement((AdvertisementPo)returnObject.getData());
            return new ReturnObject<>(advertisementBo);
        }
    }



    @Transactional
    public ReturnObject<Object> insertAdvertisement(Long tid, Long id){
        ReturnObject returnObject=advertisementDao.insertAdvertisement(tid,id);
        AdvertisementPo po=(AdvertisementPo)returnObject.getData();

        if(po==null){
            return returnObject;
        }
        else {
            Advertisement advertisementBo =new Advertisement(po);
            return new ReturnObject<>(advertisementBo);
        }
    }


    @Transactional
    public ReturnObject<PageInfo<VoObject>> getAdvertisementBySegId(Long id, Integer page, Integer pageSize){

        PageHelper.startPage(page,pageSize);
        ReturnObject<PageInfo<AdvertisementPo>>returnObject=advertisementDao.getAdvertisementBySegId(id,page,pageSize);

        PageInfo<AdvertisementPo>pos=returnObject.getData();
        List<VoObject> ret = new ArrayList<>(pos.getList().size());

        for(AdvertisementPo advertisementPo:pos.getList()){
            Advertisement advertisement=new Advertisement(advertisementPo);
            ret.add(advertisement);
        }

        PageInfo<VoObject> advertisementPage = new PageInfo<>(ret);
        advertisementPage.setPages(pos.getPages());
        advertisementPage.setPageNum(pos.getPageNum());
        advertisementPage.setPageSize(pos.getPageSize());
        advertisementPage.setTotal(pos.getTotal());



        return new ReturnObject<>(advertisementPage);
    }




    @Transactional
    public ReturnObject uploadImg(Long id, MultipartFile multipartFile){
        ReturnObject<Advertisement> advertisementReturnObject = advertisementDao.getAdvertisementById(id);

        if(advertisementReturnObject.getCode() == ResponseCode.RESOURCE_ID_NOTEXIST) {
            return advertisementReturnObject;
        }
        Advertisement advertisement = advertisementReturnObject.getData();

        ReturnObject returnObject = new ReturnObject();
        try{
            returnObject = ImgHelper.remoteSaveImg(multipartFile,2,davUsername, davPassword,baseUrl);

            //文件上传错误
            if(returnObject.getCode()!=ResponseCode.OK){
                logger.debug(returnObject.getErrmsg());
                return returnObject;
            }

            String oldFilename = advertisement.getImageUrl();
            advertisement.setImageUrl(returnObject.getData().toString());
            ReturnObject updateReturnObject = advertisementDao.updateAdvertisementImage(advertisement);

            //数据库更新失败，需删除新增的图片
            if(updateReturnObject.getCode()==ResponseCode.FIELD_NOTVALID){
                ImgHelper.deleteRemoteImg(returnObject.getData().toString(),davUsername, davPassword,baseUrl);
                return updateReturnObject;
            }

            //数据库更新成功需删除旧图片，未设置则不删除
            if(oldFilename!=null) {
                ImgHelper.deleteRemoteImg(oldFilename, davUsername, davPassword,baseUrl);
            }
        }
        catch (IOException e){
            logger.debug("uploadImg: I/O Error:" + baseUrl);
            return new ReturnObject(ResponseCode.FILE_NO_WRITE_PERMISSION);
        }
        return returnObject;
    }


    public void loadAdvertisement(){

        LocalDate localDate=LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = localDate.format(fmt);

        ReturnObject<List<AdvertisementPo>> returnObject=advertisementDao.getCurrentAdvertisement(localDate);
        List<AdvertisementPo> pos=returnObject.getData();
        for(AdvertisementPo po:pos){
            Advertisement advertisement=new Advertisement(po);
            String advertisementJson=JacksonUtil.toJson(advertisement);
            redisTemplate.opsForList().rightPush(dateStr,advertisementJson);
        }
        redisTemplate.expire(dateStr,1,TimeUnit.DAYS);
    }
}
