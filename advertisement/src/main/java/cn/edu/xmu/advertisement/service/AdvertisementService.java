package cn.edu.xmu.advertisement.service;

import cn.edu.xmu.advertisement.dao.AdvertisementDao;
import cn.edu.xmu.advertisement.model.bo.Advertisement;
import cn.edu.xmu.advertisement.model.po.AdvertisementPo;
import cn.edu.xmu.advertisement.model.vo.AdvertisementCreateVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ImgHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.other.service.IAddressService;
import cn.edu.xmu.oomall.other.service.IAftersaleService;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;


@Service
public class AdvertisementService{
    private Logger logger = LoggerFactory.getLogger(AdvertisementService.class);

    @DubboReference(version = "0.0.1-SNAPSHOT")
    IAftersaleService iAftersaleService;

    @Autowired
    AdvertisementDao advertisementDao;

    @Value("${privilegeservice.dav.username}")
    private String davUsername;

    @Value("${privilegeservice.dav.password}")
    private String davPassword;

    @Value("${privilegeservice.dav.baseUrl}")
    private String baseUrl;


/*
    @Transactional
    public ReturnObject<Object>getCurrentAdvertisement(LocalDate localDate, LocalTime localTime){
        ReturnObject returnObject=advertisementDao.getCurrentAdvertisement(localDate,localTime);
        return returnObject;
    }

 */

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
        ReturnObject<PageInfo<VoObject>>returnObject=advertisementDao.getAdvertisementBySegId(id,page,pageSize);

        return returnObject;
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
}
