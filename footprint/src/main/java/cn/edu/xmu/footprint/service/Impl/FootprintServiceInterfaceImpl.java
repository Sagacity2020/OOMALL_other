package cn.edu.xmu.footprint.service.Impl;

import cn.edu.xmu.footprint.dao.FootprintDao;
import cn.edu.xmu.footprint.model.bo.Footprint;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.service.FootprintServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.dubbo.config.annotation.DubboService;

import java.time.LocalDateTime;
@DubboService(version = "0.0.1-SNAPSHOT")
public class FootprintServiceInterfaceImpl implements FootprintServiceInterface {
    @Autowired
    FootprintDao footprintDao;

    @Override
    public void addFootprint(Long userId, Long skuId) {
        Footprint footprint=new Footprint();
        footprint.setCustomerId(userId);
        footprint.setGoodsSkuId(skuId);
        footprint.setGmtCreate(LocalDateTime.now());
        footprintDao.insertFootprint(footprint);
    }
}
