package cn.edu.xmu.advertisement.service;

import cn.edu.xmu.advertisement.dao.AdvertisementDao;
import cn.edu.xmu.advertisement.model.bo.Advertisement;
import cn.edu.xmu.advertisement.model.po.AdvertisementPo;
import cn.edu.xmu.advertisement.model.vo.AdvertisementCreateVo;
import cn.edu.xmu.advertisement.model.vo.AuditAdVo;
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
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
public class AdvertisementService{
    private Logger logger = LoggerFactory.getLogger(AdvertisementService.class);

    @DubboReference(version = "0.0.1")
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

        Set<String> set=redisTemplate.keys("*");
        for(String str:set){
            JSONObject jsStr=JSONObject.parseObject(redisTemplate.opsForValue().get(str.toString()).toString());
            Advertisement advertisement = (Advertisement) JSONObject.toJavaObject(jsStr, Advertisement.class);
            if (advertisement.getSegId() != null) {
                ReturnObject<TimeSegmentDTO> returnObj = timeServiceInterface.getTimesegmentById(advertisement.getSegId());
                TimeSegmentDTO timeSegmentDTO = returnObj.getData();
                if(timeSegmentDTO!=null) {
                    if (timeSegmentDTO.getBeginTime().isBefore(localTime) && timeSegmentDTO.getEndTime().isAfter(localTime)) {
                        advertisements.add(advertisement);
                    }
                }
            }
        }
        /*Collections.sort(advertisements, new Comparator<Advertisement>() {
            @Override
            public int compare(Advertisement o1, Advertisement o2) {
                int weights=o1.getWeight()-o2.getWeight();
                if(weights<0){
                    return 1;
                }
                else if(weights>0){
                    return -1;
                }
                return 0;
            }
        });
         */

        List<Advertisement> advertisementList=new ArrayList<>();
        for(Advertisement advertisement:advertisements){
            advertisementList.add(advertisement);
            count++;
            if(count==8){
                break;
            }
        }
        return new ReturnObject<>(advertisementList);
    }



    @Transactional
    public ReturnObject<Object> createAdvertisement(Long id, AdvertisementCreateVo vo){
        ReturnObject<TimeSegmentDTO> returnObj=timeServiceInterface.getTimesegmentById(id);
        if(returnObj.getData()==null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

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
        ReturnObject<TimeSegmentDTO> returnObj=timeServiceInterface.getTimesegmentById(id);
        if(returnObj.getData()==null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

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
    public ReturnObject<PageInfo<VoObject>> getAdvertisementBySegId(Long id,LocalDate beginDate,LocalDate endDate, Integer page, Integer pageSize){

        ReturnObject<TimeSegmentDTO> returnObj=timeServiceInterface.getTimesegmentById(id);
        if(returnObj.getData()==null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        PageHelper.startPage(page,pageSize);
        ReturnObject<PageInfo<AdvertisementPo>>returnObject=advertisementDao.getAdvertisementBySegId(id,beginDate,endDate,page,pageSize);

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

        ReturnObject<List<AdvertisementPo>> returnObject=advertisementDao.getCurrentAdvertisement(localDate);
        List<AdvertisementPo> pos=returnObject.getData();
        for(AdvertisementPo po:pos){
            Advertisement advertisement=new Advertisement(po);
            String key="ad_"+advertisement.getId();
            String advertisementJson=JacksonUtil.toJson(advertisement);
            redisTemplate.opsForValue().set(key,advertisementJson);
            redisTemplate.expire(key,1,TimeUnit.DAYS);
        }
    }

    /**
     * 设置默认广告
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/9 9:12
     */
    @Transactional
    public ReturnObject setDefaultAd(Long id){
        ReturnObject retObj = advertisementDao.setDefaultAd(id);
        return retObj;
    }

    /**
     * 修改广告内容
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/9 15:13
     */
    @Transactional
    public ReturnObject updateAd(Long id, Advertisement bo)
    {
        ReturnObject retObj = advertisementDao.updateAd(id,bo);
        return retObj;
    }
    /**
     * 上架广告
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/9 11:32
     */
    @Transactional
    public ReturnObject onshelvesAd(Long id){
        ReturnObject retObj = advertisementDao.onshelvesAd(id);
        return retObj;
    }

    /**
     * 下架广告
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/9 19:05
     */
    @Transactional
    public ReturnObject offshelvesAd(Long id){
        ReturnObject retObj = advertisementDao.offshelvesAd(id);
        return retObj;
    }
    /**
     * 审核广告
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/10 19:53
     */
    public ReturnObject auditAd(Long id, AuditAdVo auditAdVo){
        ReturnObject retObj = advertisementDao.auditAd(id,auditAdVo);
        return retObj;
    }
    /**
     * 删除广告
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/9 15:55
     */
    public ReturnObject deleteAd(Long id){
        ReturnObject retObj=advertisementDao.deleteAd(id);
        return retObj;
    }
}
