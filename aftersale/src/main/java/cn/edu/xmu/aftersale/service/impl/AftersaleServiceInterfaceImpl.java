package cn.edu.xmu.aftersale.service.impl;

import cn.edu.xmu.aftersale.dao.AftersaleDao;
import cn.edu.xmu.aftersale.model.po.AftersaleServicePo;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.dto.AftersaleRefundDTO;
import cn.edu.xmu.other.service.AftersaleServiceInterface;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(version = "0.0.1-SNAPSHOT")
public class AftersaleServiceInterfaceImpl implements AftersaleServiceInterface {

    @Autowired
    AftersaleDao aftersaleDao;

    @Override
    public Boolean checkIsAftersale(Long orderItemId) {
        return aftersaleDao.checkIsAftersale(orderItemId);
    }

    @Override
    public AftersaleRefundDTO getAftersaleById(Long aftersaleId) {
        ReturnObject returnObject= aftersaleDao.getAftersaleById(aftersaleId);
        AftersaleServicePo po=(AftersaleServicePo)returnObject.getData();

        AftersaleRefundDTO aftersaleRefundDTO=new AftersaleRefundDTO();
        aftersaleRefundDTO.setId(po.getId());
        aftersaleRefundDTO.setCustomerId(po.getCustomerId());
        aftersaleRefundDTO.setBeDeleted(po.getBeDeleted());
        aftersaleRefundDTO.setOrderId(po.getOrderId());

        return aftersaleRefundDTO;
    }
}
