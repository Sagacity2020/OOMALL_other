package cn.edu.xmu.aftersale.service.impl;

import cn.edu.xmu.aftersale.controller.AftersaleController;
import cn.edu.xmu.aftersale.dao.AftersaleDao;
import cn.edu.xmu.aftersale.model.bo.Aftersale;
import cn.edu.xmu.aftersale.model.po.AftersaleServicePo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.dto.AftersaleRefundDTO;
import cn.edu.xmu.other.dto.ReasonDTO;
import cn.edu.xmu.other.service.AftersaleServiceInterface;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(version = "0.0.1")
public class AftersaleServiceInterfaceImpl implements AftersaleServiceInterface {

    @Autowired
    AftersaleDao aftersaleDao;

    private  static  final Logger logger = LoggerFactory.getLogger(AftersaleServiceInterfaceImpl.class);

    @Override
    public Boolean judgeNotBeingAfterSale(Long orderItemId) {
        return aftersaleDao.checkIsAftersale(orderItemId);
    }

    @Override
    public AftersaleRefundDTO getAftersaleById(Long aftersaleId) {
        logger.error(aftersaleId.toString());
        ReturnObject returnObject= aftersaleDao.getAftersaleById(aftersaleId);
        AftersaleServicePo po=(AftersaleServicePo)returnObject.getData();

        logger.error(JacksonUtil.toJson(po));
        if(po==null){
            return null;
        }
        AftersaleRefundDTO aftersaleRefundDTO=new AftersaleRefundDTO();
        aftersaleRefundDTO.setId(po.getId());
        aftersaleRefundDTO.setCustomerId(po.getCustomerId());
        aftersaleRefundDTO.setBeDeleted(po.getBeDeleted());
        aftersaleRefundDTO.setOrderId(po.getOrderId());
        aftersaleRefundDTO.setShopId(po.getShopId());

        logger.error(aftersaleRefundDTO.toString());
        return aftersaleRefundDTO;
    }

    @Override
    public ReasonDTO CreateAftersalePaymentJudge(Long userId, Long aftersaleId) {
        ReturnObject returnObject=aftersaleDao.getAftersaleById(aftersaleId);
        AftersaleServicePo po=(AftersaleServicePo)returnObject.getData();

        ReasonDTO reasonDTO=new ReasonDTO();
        if(po==null){
            reasonDTO.setResult(false);
            reasonDTO.setErrno(2);
        }
        else if(!po.getCustomerId().equals(userId)){
            reasonDTO.setResult(false);
            reasonDTO.setErrno(0);
        }
        else if(Aftersale.State.getTypeByCode(po.getState().intValue())!=Aftersale.State.REFUNDWAIT && Aftersale.State.getTypeByCode(po.getState().intValue())!=Aftersale.State.DILIVERWAIT){
            reasonDTO.setResult(false);
            reasonDTO.setErrno(1);
        }

        reasonDTO.setResult(true);
        reasonDTO.setErrno(null);

        return reasonDTO;
    }
}
