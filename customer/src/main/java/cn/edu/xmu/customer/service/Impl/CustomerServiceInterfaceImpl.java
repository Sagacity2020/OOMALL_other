package cn.edu.xmu.customer.service.Impl;

import cn.edu.xmu.customer.dao.CustomerDao;
import cn.edu.xmu.other.dto.CustomerDTO;
import cn.edu.xmu.other.service.CustomerServiceInterface;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(version = "0.0.1-SNAPSHOT")
public class CustomerServiceInterfaceImpl implements CustomerServiceInterface {
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
