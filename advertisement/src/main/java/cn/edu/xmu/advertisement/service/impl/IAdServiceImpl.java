package cn.edu.xmu.advertisement.service.impl;

import cn.edu.xmu.advertisement.dao.AdvertisementDao;
import cn.edu.xmu.ooad.util.ResponseCode;

import cn.edu.xmu.other.service.AdServiceInterface;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author zwl
 * @param 
 * @return  
 * @Date:  2020/12/14 9:30
*/
@Service
@DubboService(version = "0.0.1")
public class IAdServiceImpl implements AdServiceInterface {

    @Autowired
    AdvertisementDao advertisementDao;
    @Override
    public Boolean updateAdSegId(Long segId) {
       if(advertisementDao.updateAdSegId(segId).getCode().equals(ResponseCode.OK )) {
           return true;
       }else {return false;}
    }
}
