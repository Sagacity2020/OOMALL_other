package cn.edu.xmu.aftersale.service.impl;

import cn.edu.xmu.aftersale.dao.AftersaleDao;
import cn.edu.xmu.other.service.IAftersaleService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(version = "0.0.1-SNAPSHOT")
public class IAftersaleServiceImpl implements IAftersaleService {

    @Autowired
    AftersaleDao aftersaleDao;

    @Override
    public boolean checkIsAftersale(Long orderItemId) {
        return aftersaleDao.checkIsAftersale(orderItemId);
    }
}
