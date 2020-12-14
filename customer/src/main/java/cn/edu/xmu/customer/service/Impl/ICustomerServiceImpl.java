package cn.edu.xmu.customer.service.Impl;

import cn.edu.xmu.customer.dao.CustomerDao;
import cn.edu.xmu.customer.model.bo.Customer;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.model.CustomerDTO;
import cn.edu.xmu.other.service.ICustomerService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(version = "0.0.1-SNAPSHOT")
public class ICustomerServiceImpl implements ICustomerService {
    @Autowired
    CustomerDao customerDao;

    @Override
    public CustomerDTO getCustomerInfoById(Long id) {
//        ReturnObject ret=customerDao.getCustomerById(id);
//        Customer bo=ret.getdata();
//        CustomerDTO retVo=new CustomerDTO();
//        retVo.setUserName(bo.getUserName());
//        retVo.setName(bo.getRealName());
//        retVo.setId(bo.getId());
//        return retVo;
        return null;
    }

    @Override
    public Boolean getPointFromRefund(Long userId, Integer point) {
        return customerDao.getPointFromRefund(userId,point);
    }


}
