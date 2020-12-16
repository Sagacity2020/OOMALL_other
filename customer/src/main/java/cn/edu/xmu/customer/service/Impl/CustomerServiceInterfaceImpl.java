package cn.edu.xmu.customer.service.Impl;

import cn.edu.xmu.customer.dao.CustomerDao;
import cn.edu.xmu.customer.model.bo.Customer;
import cn.edu.xmu.ooad.util.ReturnObject;
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
        ReturnObject<Customer> ret=customerDao.getCustomerById(id);
        if(ret.getData()!=null){
        Customer bo=ret.getData();
        CustomerDTO retVo=new CustomerDTO();
        retVo.setUserName(bo.getUserName());
        retVo.setName(bo.getRealName());
        retVo.setId(bo.getId());
        return retVo;}
        else{
            return null;
        }
    }

    @Override
    public Boolean getPointFromRefund(Long userId, Integer point) {
        return customerDao.getPointFromRefund(userId,point);
    }

    @Override
    public Boolean payWithPoint(Long userId, Integer amount) {
        if(amount<0){
            return customerDao.getPointFromRefund(userId,-amount);
        }else{
            return customerDao.minusPoint(userId,amount);
        }
    }


}
